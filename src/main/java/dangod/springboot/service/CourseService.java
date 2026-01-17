package dangod.springboot.service;

import dangod.springboot.common.BusinessException;
import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseTypeRepository courseTypeRepository;
    
    @Autowired
    private CourseReservationRepository reservationRepository;
    
    @Autowired
    private CoachRepository coachRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private BodyTestRepository bodyTestRepository;
    
    @Value("${gym.course.auto-queue:false}")
    private boolean autoQueue;
    
    @Value("${gym.course.auto-fill:false}")
    private boolean autoFill;
    
    @Value("${gym.course.max-per-course:20}")
    private Integer maxPerCourse;
    
    @Transactional
    public Course createCourse(Course course) {
        if (course.getMaxParticipants() == null || course.getMaxParticipants() <= 0) {
            course.setMaxParticipants(maxPerCourse);
        }
        
        if (course.getCoach() == null || course.getCoach().getId() == null) {
            throw new BusinessException("教练信息不能为空");
        }
        
        if (course.getCourseType() == null || course.getCourseType().getId() == null) {
            throw new BusinessException("课程类型不能为空");
        }
        
        if (course.getCourseDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("课程时间不能早于当前时间");
        }
        
        if (course.getStartTime().isAfter(course.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        
        Coach coach = coachRepository.findById(course.getCoach().getId()).orElse(null);
        if (coach == null || coach.getStatus() != 1) {
            throw new BusinessException("教练不存在或不可用");
        }
        
        CourseType courseType = courseTypeRepository.findById(course.getCourseType().getId()).orElse(null);
        if (courseType == null || courseType.getStatus() != 1) {
            throw new BusinessException("课程类型不存在或不可用");
        }
        
        course.setCurrentParticipants(0);
        course.setWaitingCount(0);
        course.setStatus(1);
        course.setHeatScore(0);
        course.setCreateTime(LocalDateTime.now());
        
        Course savedCourse = courseRepository.save(course);
        logger.info("创建课程成功: {}", savedCourse.getCourseName());
        return savedCourse;
    }
    
    @Transactional
    public Course updateCourse(Course course) {
        Course existing = courseRepository.findById(course.getId()).orElse(null);
        if (existing == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (existing.getCourseDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("已开始的课程无法修改");
        }
        
        if (course.getCoach() != null && course.getCoach().getId() != null) {
            Coach coach = coachRepository.findById(course.getCoach().getId()).orElse(null);
            if (coach == null || coach.getStatus() != 1) {
                throw new BusinessException("教练不存在或不可用");
            }
            existing.setCoach(course.getCoach());
        }
        
        if (course.getCourseType() != null && course.getCourseType().getId() != null) {
            CourseType courseType = courseTypeRepository.findById(course.getCourseType().getId()).orElse(null);
            if (courseType == null || courseType.getStatus() != 1) {
                throw new BusinessException("课程类型不存在或不可用");
            }
            existing.setCourseType(course.getCourseType());
        }
        
        if (course.getCourseName() != null) {
            existing.setCourseName(course.getCourseName());
        }
        if (course.getCourseDate() != null && !course.getCourseDate().isBefore(LocalDateTime.now())) {
            existing.setCourseDate(course.getCourseDate());
        }
        if (course.getStartTime() != null) {
            existing.setStartTime(course.getStartTime());
        }
        if (course.getEndTime() != null) {
            existing.setEndTime(course.getEndTime());
        }
        if (course.getMaxParticipants() != null && course.getMaxParticipants() > 0) {
            existing.setMaxParticipants(course.getMaxParticipants());
        }
        if (course.getLocation() != null) {
            existing.setLocation(course.getLocation());
        }
        if (course.getStatus() != null) {
            existing.setStatus(course.getStatus());
        }
        if (course.getNote() != null) {
            existing.setNote(course.getNote());
        }
        if (course.getUpdateBy() != null) {
            existing.setUpdateBy(course.getUpdateBy());
        }
        existing.setUpdateTime(LocalDateTime.now());
        
        Course updated = courseRepository.save(existing);
        logger.info("更新课程成功: {}", updated.getCourseName());
        return updated;
    }
    
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (course.getCourseDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("已开始的课程无法删除");
        }
        
        List<CourseReservation> reservations = reservationRepository.findByCourseId(courseId);
        if (!reservations.isEmpty()) {
            throw new BusinessException("课程已有预约，无法删除");
        }
        
        courseRepository.delete(course);
        logger.info("删除课程成功: {}", courseId);
    }
    
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }
    
    public List<Course> getCoursesByStore(Long storeId) {
        return courseRepository.findByStoreIdAndStatus(storeId, 1);
    }
    
    public List<Course> getCoursesByDateRange(Long storeId, LocalDateTime startDate, LocalDateTime endDate) {
        return courseRepository.findCoursesByDateRange(storeId, startDate, endDate);
    }
    
    public List<Course> getUpcomingCourses(Long storeId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(30);
        return courseRepository.findCoursesByDateRange(storeId, now, endDate);
    }
    
    @Transactional
    public CourseReservation reserveCourse(Long memberId, Long courseId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (member.getStatus() != 1) {
            throw new BusinessException("会员状态异常，无法预约课程");
        }
        
        if (member.getExpireDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("会员已过期，请续费后再预约");
        }
        
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            throw new BusinessException("课程不存在");
        }
        
        if (course.getStatus() != 1) {
            throw new BusinessException("课程不可预约");
        }
        
        if (course.getCourseDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("课程已过期");
        }
        
        CourseReservation existing = reservationRepository.findByMemberIdAndCourseId(memberId, courseId);
        if (existing != null) {
            if (existing.getStatus() == 1) {
                throw new BusinessException("您已预约该课程，请勿重复预约");
            } else if (existing.getStatus() == 0) {
                throw new BusinessException("您已在该课程的等待队列中");
            }
        }
        
        LocalDateTime startOfDay = course.getCourseDate().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = course.getCourseDate().withHour(23).withMinute(59).withSecond(59);
        long dailyCount = reservationRepository.countMemberReservationsInPeriod(memberId, startOfDay, endOfDay);
        if (dailyCount >= 3) {
            throw new BusinessException("每天最多预约3节课");
        }
        
        CourseReservation reservation = new CourseReservation();
        reservation.setMember(member);
        reservation.setCourse(course);
        reservation.setCourseDate(course.getCourseDate());
        
        if (course.getCurrentParticipants() >= course.getMaxParticipants()) {
            if (!autoQueue) {
                throw new BusinessException("课程已满，无法预约");
            }
            
            long waitingCount = reservationRepository.countWaitingReservations(courseId);
            reservation.setStatus(0);
            reservation.setWaitingPosition((int) (waitingCount + 1));
            course.setWaitingCount(course.getWaitingCount() + 1);
            courseRepository.save(course);
        } else {
            reservation.setStatus(1);
            reservation.setWaitingPosition(0);
            course.setCurrentParticipants(course.getCurrentParticipants() + 1);
            course.setHeatScore(course.getHeatScore() + 5);
            courseRepository.save(course);
        }
        
        reservation.setCreateBy("member_" + memberId);
        reservation.setCreateTime(LocalDateTime.now());
        
        CourseReservation saved = reservationRepository.save(reservation);
        logger.info("会员{}预约课程{}，状态: {}", memberId, courseId, saved.getStatus() == 1 ? "已确认" : "等待中");
        return saved;
    }
    
    @Transactional
    public void cancelReservation(Long reservationId, Long memberId) {
        CourseReservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        if (!reservation.getMember().getId().equals(memberId)) {
            throw new BusinessException("无权取消该预约");
        }
        
        if (reservation.getStatus() == 2) {
            throw new BusinessException("该预约已取消");
        }
        
        if (reservation.getCourse().getCourseDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("课程已开始，无法取消");
        }
        
        Course course = reservation.getCourse();
        reservation.setStatus(2);
        reservation.setCancelTime(LocalDateTime.now());
        reservation.setUpdateBy("member_" + memberId);
        reservation.setUpdateTime(LocalDateTime.now());
        
        if (reservation.getStatus() == 1) {
            course.setCurrentParticipants(course.getCurrentParticipants() - 1);
            
            if (autoFill && course.getWaitingCount() > 0) {
                List<CourseReservation> waitingList = reservationRepository.findFirstInWaitingList(course.getId());
                if (!waitingList.isEmpty()) {
                    CourseReservation firstWaiting = waitingList.get(0);
                    firstWaiting.setStatus(1);
                    firstWaiting.setWaitingPosition(0);
                    firstWaiting.setUpdateTime(LocalDateTime.now());
                    firstWaiting.setUpdateBy("system");
                    reservationRepository.save(firstWaiting);
                    
                    course.setCurrentParticipants(course.getCurrentParticipants() + 1);
                    course.setWaitingCount(course.getWaitingCount() - 1);
                    
                    updateWaitingPositions(course.getId());
                    
                    logger.info("会员{}取消预约，自动补位给会员{}", memberId, firstWaiting.getMember().getId());
                }
            }
        } else {
            course.setWaitingCount(course.getWaitingCount() - 1);
            updateWaitingPositions(course.getId());
        }
        
        courseRepository.save(course);
        reservationRepository.save(reservation);
        logger.info("会员{}取消预约成功: {}", memberId, reservationId);
    }
    
    private void updateWaitingPositions(Long courseId) {
        List<CourseReservation> waitingList = reservationRepository.findWaitingListByCourse(courseId);
        for (int i = 0; i < waitingList.size(); i++) {
            CourseReservation r = waitingList.get(i);
            r.setWaitingPosition(i + 1);
            r.setUpdateTime(LocalDateTime.now());
            reservationRepository.save(r);
        }
    }
    
    @Transactional
    public void signIn(Long reservationId) {
        CourseReservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        
        if (reservation.getStatus() != 1) {
            throw new BusinessException("只有已确认的预约才能签到");
        }
        
        Course course = reservation.getCourse();
        if (course.getStartTime().isBefore(LocalDateTime.now().minusMinutes(30))) {
            throw new BusinessException("签到时间已过");
        }
        
        if (course.getStartTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("签到时间未到");
        }
        
        reservation.setSignInTime(LocalDateTime.now());
        reservation.setUpdateTime(LocalDateTime.now());
        reservation.setUpdateBy("system");
        reservationRepository.save(reservation);
        
        Member member = reservation.getMember();
        member.setTotalPoints(member.getTotalPoints() + 10);
        memberRepository.save(member);
        
        logger.info("会员{}签到成功: {}", member.getId(), reservationId);
    }
    
    public List<CourseReservation> getMemberReservations(Long memberId) {
        return reservationRepository.findUpcomingReservations(memberId, LocalDateTime.now());
    }
    
    public List<CourseReservation> getCourseReservations(Long courseId) {
        return reservationRepository.findAllReservationsForCourse(courseId);
    }
    
    public List<Course> recommendCourses(Long memberId) {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        List<BodyTest> bodyTests = bodyTestRepository.findLatestByMemberId(memberId, 1);
        List<Course> recommendations = new ArrayList<>();
        
        List<Course> hotCourses = courseRepository.findUpcomingHotCourses(LocalDateTime.now());
        if (!hotCourses.isEmpty()) {
            recommendations.addAll(hotCourses.subList(0, Math.min(3, hotCourses.size())));
        }
        
        if (!bodyTests.isEmpty()) {
            BodyTest latestTest = bodyTests.get(0);
            List<CourseType> suitableTypes = courseTypeRepository.findSuitableByAge(25);
            
            if (latestTest.getBmi() != null && latestTest.getBmi().doubleValue() > 28) {
                List<CourseType> weightLossTypes = courseTypeRepository.findByBenefitsContaining("减重");
                suitableTypes.addAll(weightLossTypes);
            }
            
            if (latestTest.getMuscleMass() != null && latestTest.getMuscleMass().doubleValue() < 50) {
                List<CourseType> muscleTypes = courseTypeRepository.findByBenefitsContaining("增肌");
                suitableTypes.addAll(muscleTypes);
            }
            
            for (CourseType type : suitableTypes) {
                List<Course> courses = courseRepository.findByCourseTypeId(type.getId());
                if (!courses.isEmpty()) {
                    recommendations.add(courses.get(0));
                }
            }
        }
        
        return recommendations.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }
    
    public List<Course> getHotCourses(int limit) {
        return courseRepository.findHotCourses(limit);
    }
    
    public List<CourseType> getCourseTypes() {
        return courseTypeRepository.findActiveTypes();
    }
    
    public CourseType getCourseTypeById(Long typeId) {
        return courseTypeRepository.findById(typeId).orElse(null);
    }
    
    @Transactional
    public CourseType createCourseType(CourseType courseType) {
        if (courseType.getTypeCode() == null || courseType.getTypeCode().isEmpty()) {
            throw new BusinessException("课程类型编码不能为空");
        }
        
        if (courseType.getTypeName() == null || courseType.getTypeName().isEmpty()) {
            throw new BusinessException("课程类型名称不能为空");
        }
        
        CourseType existing = courseTypeRepository.findByTypeCode(courseType.getTypeCode());
        if (existing != null) {
            throw new BusinessException("课程类型编码已存在");
        }
        
        existing = courseTypeRepository.findByTypeName(courseType.getTypeName());
        if (existing != null) {
            throw new BusinessException("课程类型名称已存在");
        }
        
        courseType.setStatus(1);
        courseType.setCreateTime(LocalDateTime.now());
        return courseTypeRepository.save(courseType);
    }
    
    @Transactional
    public CourseType updateCourseType(CourseType courseType) {
        CourseType existing = courseTypeRepository.findById(courseType.getId()).orElse(null);
        if (existing == null) {
            throw new BusinessException("课程类型不存在");
        }
        
        if (courseType.getTypeName() != null) {
            existing.setTypeName(courseType.getTypeName());
        }
        if (courseType.getDescription() != null) {
            existing.setDescription(courseType.getDescription());
        }
        if (courseType.getDuration() != null) {
            existing.setDuration(courseType.getDuration());
        }
        if (courseType.getBasePrice() != null) {
            existing.setBasePrice(courseType.getBasePrice());
        }
        if (courseType.getDifficulty() != null) {
            existing.setDifficulty(courseType.getDifficulty());
        }
        if (courseType.getSuitableBodyType() != null) {
            existing.setSuitableBodyType(courseType.getSuitableBodyType());
        }
        if (courseType.getMinAge() != null) {
            existing.setMinAge(courseType.getMinAge());
        }
        if (courseType.getMaxAge() != null) {
            existing.setMaxAge(courseType.getMaxAge());
        }
        if (courseType.getStatus() != null) {
            existing.setStatus(courseType.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        
        return courseTypeRepository.save(existing);
    }
    
    @Transactional
    public void deleteCourseType(Long typeId) {
        CourseType type = courseTypeRepository.findById(typeId).orElse(null);
        if (type == null) {
            throw new BusinessException("课程类型不存在");
        }
        
        List<Course> courses = courseRepository.findByCourseTypeId(typeId);
        if (!courses.isEmpty()) {
            throw new BusinessException("该课程类型下还有课程，无法删除");
        }
        
        courseTypeRepository.delete(type);
        logger.info("删除课程类型成功: {}", typeId);
    }
}
