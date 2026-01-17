package dangod.springboot.service;

import dangod.springboot.entity.Course;
import dangod.springboot.entity.CourseSchedule;
import dangod.springboot.entity.CourseBooking;
import dangod.springboot.entity.BodyMeasurement;
import dangod.springboot.entity.Member;
import dangod.springboot.repository.*;
import dangod.springboot.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseBookingRepository courseBookingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BodyMeasurementRepository bodyMeasurementRepository;

    public Course createCourse(Course course) {
        course.setCreateTime(new Date());
        course.setUpdateTime(new Date());
        course.setStatus(Course.CourseStatus.SCHEDULED);
        course.setCurrentEnrolled(0);
        course.setQueueCount(0);
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course course) {
        Course existingCourse = courseRepository.findOne(id);
        if (existingCourse == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (course.getName() != null) {
            existingCourse.setName(course.getName());
        }
        if (course.getDescription() != null) {
            existingCourse.setDescription(course.getDescription());
        }
        if (course.getInstructor() != null) {
            existingCourse.setInstructor(course.getInstructor());
        }
        if (course.getDuration() != null) {
            existingCourse.setDuration(course.getDuration());
        }
        if (course.getCapacity() != null) {
            existingCourse.setCapacity(course.getCapacity());
        }
        if (course.getType() != null) {
            existingCourse.setType(course.getType());
        }
        if (course.getDifficulty() != null) {
            existingCourse.setDifficulty(course.getDifficulty());
        }
        if (course.getStatus() != null) {
            existingCourse.setStatus(course.getStatus());
        }
        
        existingCourse.setUpdateTime(new Date());
        return courseRepository.save(existingCourse);
    }

    public Course getCourseById(Long id) {
        Course course = courseRepository.findOne(id);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        return course;
    }

    public List<Course> getCoursesByStore(Long storeId) {
        return courseRepository.findByStoreId(storeId);
    }

    public List<Course> getCoursesByType(Course.CourseType type) {
        return courseRepository.findByType(type);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getRecommendedCourses(Long memberId, Long storeId) {
        List<BodyMeasurement> measurements = bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
        
        if (measurements.isEmpty()) {
            return courseRepository.findByStoreId(storeId);
        }
        
        BodyMeasurement latestMeasurement = measurements.get(0);
        
        Course.CourseType recommendedType = determineRecommendedType(latestMeasurement);
        Integer difficultyLevel = determineDifficultyLevel(latestMeasurement);
        
        return courseRepository.findRecommendedCourses(storeId, recommendedType, difficultyLevel);
    }

    private Course.CourseType determineRecommendedType(BodyMeasurement measurement) {
        if (measurement.getStrength() != null && measurement.getStrength() > 7.0) {
            return Course.CourseType.STRENGTH;
        } else if (measurement.getEndurance() != null && measurement.getEndurance() > 7.0) {
            return Course.CourseType.CARDIO;
        } else if (measurement.getFlexibility() != null && measurement.getFlexibility() > 7.0) {
            return Course.CourseType.YOGA;
        } else {
            return Course.CourseType.CARDIO;
        }
    }

    private Integer determineDifficultyLevel(BodyMeasurement measurement) {
        Double healthScore = 0.0;
        if (measurement.getStrength() != null) healthScore += measurement.getStrength();
        if (measurement.getEndurance() != null) healthScore += measurement.getEndurance();
        if (measurement.getFlexibility() != null) healthScore += measurement.getFlexibility();
        
        healthScore = healthScore / 3;
        
        if (healthScore < 4.0) return 1;
        if (healthScore < 6.0) return 2;
        if (healthScore < 8.0) return 3;
        return 4;
    }

    public CourseSchedule createSchedule(CourseSchedule schedule) {
        schedule.setCreateTime(new Date());
        schedule.setUpdateTime(new Date());
        schedule.setStatus(CourseSchedule.ScheduleStatus.UPCOMING);
        return courseScheduleRepository.save(schedule);
    }

    public List<CourseSchedule> getSchedulesByCourse(Long courseId) {
        return courseScheduleRepository.findByCourseId(courseId);
    }

    public List<CourseSchedule> getSchedulesByStore(Long storeId) {
        return courseScheduleRepository.findByStoreId(storeId);
    }

    @Transactional
    public CourseBooking bookCourse(Long memberId, Long scheduleId) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getStatus() != Member.MemberStatus.ACTIVE) {
            throw new BusinessException("会员状态异常，无法预约课程");
        }
        
        CourseSchedule schedule = courseScheduleRepository.findOne(scheduleId);
        if (schedule == null) {
            throw new BusinessException("课程安排不存在");
        }
        
        Course course = courseRepository.findOne(schedule.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        CourseBooking existingBooking = courseBookingRepository.findByMemberIdAndScheduleId(memberId, scheduleId);
        if (existingBooking != null && existingBooking.getStatus() != CourseBooking.BookingStatus.CANCELLED) {
            throw new BusinessException("您已预约该课程");
        }
        
        Integer confirmedCount = courseBookingRepository.countConfirmedBookings(scheduleId);
        
        CourseBooking booking = new CourseBooking();
        booking.setMemberId(memberId);
        booking.setScheduleId(scheduleId);
        booking.setCreateTime(new Date());
        booking.setUpdateTime(new Date());
        
        if (confirmedCount < course.getCapacity()) {
            booking.setStatus(CourseBooking.BookingStatus.CONFIRMED);
            course.setCurrentEnrolled(confirmedCount + 1);
            courseRepository.save(course);
        } else {
            booking.setStatus(CourseBooking.BookingStatus.QUEUED);
            List<CourseBooking> queuedBookings = courseBookingRepository.findQueuedBookings(scheduleId);
            booking.setQueuePosition(queuedBookings.size() + 1);
            course.setQueueCount(course.getQueueCount() + 1);
            courseRepository.save(course);
        }
        
        return courseBookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        CourseBooking booking = courseBookingRepository.findOne(bookingId);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        if (booking.getStatus() == CourseBooking.BookingStatus.CANCELLED) {
            throw new BusinessException("预约已取消");
        }
        
        CourseSchedule schedule = courseScheduleRepository.findOne(booking.getScheduleId());
        if (schedule == null) {
            throw new BusinessException("课程安排不存在");
        }
        
        Course course = courseRepository.findOne(schedule.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (booking.getStatus() == CourseBooking.BookingStatus.CONFIRMED) {
            course.setCurrentEnrolled(course.getCurrentEnrolled() - 1);
            courseRepository.save(course);
            
            autoFillQueue(booking.getScheduleId());
        } else if (booking.getStatus() == CourseBooking.BookingStatus.QUEUED) {
            course.setQueueCount(course.getQueueCount() - 1);
            courseRepository.save(course);
            
            updateQueuePositions(booking.getScheduleId());
        }
        
        booking.setStatus(CourseBooking.BookingStatus.CANCELLED);
        booking.setUpdateTime(new Date());
        courseBookingRepository.save(booking);
    }

    @Transactional
    private void autoFillQueue(Long scheduleId) {
        CourseSchedule schedule = courseScheduleRepository.findOne(scheduleId);
        if (schedule == null) {
            throw new BusinessException("课程安排不存在");
        }
        
        Course course = courseRepository.findOne(schedule.getCourseId());
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (course.getCurrentEnrolled() >= course.getCapacity()) {
            return;
        }
        
        List<CourseBooking> queuedBookings = courseBookingRepository.findQueuedBookings(scheduleId);
        int availableSlots = course.getCapacity() - course.getCurrentEnrolled();
        
        for (int i = 0; i < Math.min(availableSlots, queuedBookings.size()); i++) {
            CourseBooking queuedBooking = queuedBookings.get(i);
            queuedBooking.setStatus(CourseBooking.BookingStatus.CONFIRMED);
            queuedBooking.setQueuePosition(null);
            queuedBooking.setUpdateTime(new Date());
            courseBookingRepository.save(queuedBooking);
            
            course.setCurrentEnrolled(course.getCurrentEnrolled() + 1);
            course.setQueueCount(course.getQueueCount() - 1);
        }
        
        courseRepository.save(course);
    }

    @Transactional
    private void updateQueuePositions(Long scheduleId) {
        List<CourseBooking> queuedBookings = courseBookingRepository.findQueuedBookings(scheduleId);
        for (int i = 0; i < queuedBookings.size(); i++) {
            CourseBooking booking = queuedBookings.get(i);
            booking.setQueuePosition(i + 1);
            booking.setUpdateTime(new Date());
            courseBookingRepository.save(booking);
        }
    }

    @Transactional
    public void checkIn(Long bookingId) {
        CourseBooking booking = courseBookingRepository.findOne(bookingId);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        if (booking.getStatus() != CourseBooking.BookingStatus.CONFIRMED) {
            throw new BusinessException("预约状态异常，无法签到");
        }
        
        if (booking.getIsSignedIn()) {
            throw new BusinessException("已签到");
        }
        
        booking.setIsSignedIn(true);
        booking.setSignInTime(new Date());
        booking.setUpdateTime(new Date());
        courseBookingRepository.save(booking);
    }

    public List<CourseBooking> getMemberBookings(Long memberId) {
        return courseBookingRepository.findByMemberId(memberId);
    }

    public List<CourseBooking> getScheduleBookings(Long scheduleId) {
        return courseBookingRepository.findByScheduleId(scheduleId);
    }

    public List<CourseBooking> getUpcomingBookings(Long memberId) {
        return courseBookingRepository.findUpcomingBookings(memberId, new Date());
    }

    public List<Course> getPopularCourses() {
        return courseRepository.findPopularCourses();
    }
}
