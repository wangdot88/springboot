package dangod.springboot.service;

import dangod.springboot.common.BusinessException;
import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class CourseServiceTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseTypeRepository courseTypeRepository;

    @Autowired
    private CoachRepository coachRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseReservationRepository courseReservationRepository;

    @Test
    public void testCreateCourse() {
        CourseType courseType = new CourseType();
        courseType.setName("测试课程类型");
        courseType.setDescription("测试描述");
        courseType = courseTypeRepository.save(courseType);

        Coach coach = new Coach();
        coach.setName("测试教练");
        coach.setSpecialty("测试专长");
        coach.setPhone("13900139000");
        coach = coachRepository.save(coach);

        Course course = new Course();
        course.setName("测试课程");
        course.setCourseType(courseType);
        course.setCoach(coach);
        course.setDescription("测试课程描述");
        course.setMaxCapacity(20);
        course.setCourseDate(LocalDateTime.now().plusDays(7));
        course.setDuration(60);

        Course created = courseService.createCourse(course);

        assertNotNull(created.getId());
        assertEquals("测试课程", created.getName());
        assertEquals(20, created.getMaxCapacity());
    }

    @Test
    public void testReserveCourse() {
        Member member = new Member();
        member.setName("预约用户");
        member.setPhone("13800138010");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        member.setExpireDate(LocalDateTime.now().plusDays(365));
        member = memberRepository.save(member);

        CourseType courseType = new CourseType();
        courseType.setName("测试课程类型");
        courseType = courseTypeRepository.save(courseType);

        Coach coach = new Coach();
        coach.setName("测试教练");
        coach = coachRepository.save(coach);

        Course course = new Course();
        course.setName("测试课程");
        course.setCourseType(courseType);
        course.setCoach(coach);
        course.setMaxCapacity(20);
        course.setCourseDate(LocalDateTime.now().plusDays(7));
        course.setDuration(60);
        course = courseRepository.save(course);

        CourseReservation reservation = courseService.reserveCourse(member.getId(), course.getId());

        assertNotNull(reservation.getId());
        assertEquals(member.getId(), reservation.getMemberId());
        assertEquals(course.getId(), reservation.getCourseId());
        assertEquals(CourseReservation.Status.CONFIRMED, reservation.getStatus());
    }

    @Test(expected = BusinessException.class)
    public void testReserveCourseFull() {
        Member member = new Member();
        member.setName("满员预约用户");
        member.setPhone("13800138011");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        member.setExpireDate(LocalDateTime.now().plusDays(365));
        member = memberRepository.save(member);

        CourseType courseType = new CourseType();
        courseType.setName("测试课程类型");
        courseType = courseTypeRepository.save(courseType);

        Coach coach = new Coach();
        coach.setName("测试教练");
        coach = coachRepository.save(coach);

        Course course = new Course();
        course.setName("测试课程");
        course.setCourseType(courseType);
        course.setCoach(coach);
        course.setMaxCapacity(1);
        course.setCourseDate(LocalDateTime.now().plusDays(7));
        course.setDuration(60);
        course = courseRepository.save(course);

        Member otherMember = new Member();
        otherMember.setName("其他用户");
        otherMember.setPhone("13800138012");
        otherMember.setPassword("123456");
        otherMember.setStatus(Member.Status.ACTIVE);
        otherMember.setExpireDate(LocalDateTime.now().plusDays(365));
        otherMember = memberRepository.save(otherMember);

        courseService.reserveCourse(otherMember.getId(), course.getId());

        courseService.reserveCourse(member.getId(), course.getId());
    }

    @Test
    public void testCancelReservationAndAutoFill() {
        Member member1 = new Member();
        member1.setName("用户1");
        member1.setPhone("13800138021");
        member1.setPassword("123456");
        member1.setStatus(Member.Status.ACTIVE);
        member1.setExpireDate(LocalDateTime.now().plusDays(365));
        member1 = memberRepository.save(member1);

        Member member2 = new Member();
        member2.setName("用户2");
        member2.setPhone("13800138022");
        member2.setPassword("123456");
        member2.setStatus(Member.Status.ACTIVE);
        member2.setExpireDate(LocalDateTime.now().plusDays(365));
        member2 = memberRepository.save(member2);

        CourseType courseType = new CourseType();
        courseType.setName("测试课程类型");
        courseType = courseTypeRepository.save(courseType);

        Coach coach = new Coach();
        coach.setName("测试教练");
        coach = coachRepository.save(coach);

        Course course = new Course();
        course.setName("测试课程");
        course.setCourseType(courseType);
        course.setCoach(coach);
        course.setMaxCapacity(1);
        course.setCourseDate(LocalDateTime.now().plusDays(7));
        course.setDuration(60);
        course = courseRepository.save(course);

        courseService.reserveCourse(member1.getId(), course.getId());
        CourseReservation reservation2 = courseService.reserveCourse(member2.getId(), course.getId());
        
        assertEquals(CourseReservation.Status.WAITING, reservation2.getStatus());

        courseService.cancelReservation(reservation2.getId());
        
        CourseReservation updated = courseReservationRepository.findById(reservation2.getId()).orElse(null);
        assertEquals(CourseReservation.Status.CANCELLED, updated.getStatus());
    }

    @Test
    public void testAttendCourse() {
        Member member = new Member();
        member.setName("签到用户");
        member.setPhone("13800138030");
        member.setPassword("123456");
        member.setStatus(Member.Status.ACTIVE);
        member.setExpireDate(LocalDateTime.now().plusDays(365));
        member = memberRepository.save(member);

        CourseType courseType = new CourseType();
        courseType.setName("测试课程类型");
        courseType = courseTypeRepository.save(courseType);

        Coach coach = new Coach();
        coach.setName("测试教练");
        coach = coachRepository.save(coach);

        Course course = new Course();
        course.setName("测试课程");
        course.setCourseType(courseType);
        course.setCoach(coach);
        course.setMaxCapacity(20);
        course.setCourseDate(LocalDateTime.now().plusDays(7));
        course.setDuration(60);
        course = courseRepository.save(course);

        CourseReservation reservation = courseService.reserveCourse(member.getId(), course.getId());
        courseService.attendCourse(reservation.getId());

        CourseReservation attended = courseReservationRepository.findById(reservation.getId()).orElse(null);
        assertTrue(attended.getAttended());
    }
}
