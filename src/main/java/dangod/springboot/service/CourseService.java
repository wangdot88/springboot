package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseScheduleRepository scheduleRepository;

    @Autowired
    private MemberCourseEnrollmentRepository enrollmentRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private NotificationRepository notificationRepository;

    // 课程CRUD
    @Transactional
    public Course createCourse(Course course) {
        course.setCourseCode(generateCourseCode());
        if (course.getStatus() == null) {
            course.setStatus(1);
        }
        return courseRepository.save(course);
    }

    private String generateCourseCode() {
        return "CRS" + System.currentTimeMillis();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> getCourseByCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    public List<Course> getCoursesByCategory(String categoryCode) {
        return courseRepository.findByCategoryCode(categoryCode);
    }

    public List<Course> getActiveCourses() {
        return courseRepository.findByStatus(1);
    }

    @Transactional
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    // 课程排期
    @Transactional
    public CourseSchedule createSchedule(CourseSchedule schedule) {
        if (schedule.getCurrentParticipants() == null) {
            schedule.setCurrentParticipants(0);
        }
        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }
        return scheduleRepository.save(schedule);
    }

    public List<CourseSchedule> getSchedulesByBranch(Long branchId) {
        return scheduleRepository.findByBranchId(branchId);
    }

    public List<CourseSchedule> getSchedulesByDate(Date date) {
        return scheduleRepository.findByScheduleDate(date);
    }

    public List<CourseSchedule> getSchedulesByBranchAndDate(Long branchId, Date date) {
        return scheduleRepository.findByBranchIdAndScheduleDate(branchId, date);
    }

    public Optional<CourseSchedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<CourseSchedule> getUpcomingSchedules() {
        return scheduleRepository.findUpcomingSchedules(new Date());
    }

    // 会员约课 - 核心功能
    @Transactional
    public MemberCourseEnrollment enrollCourse(Long memberId, Long scheduleId) {
        // 1. 检查会员是否存在且有效
        Optional<Member> memberOpt = memberService.getMemberById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        Member member = memberOpt.get();
        if (member.getStatus() != 1) {
            throw new RuntimeException("会员状态异常，无法约课");
        }

        // 2. 检查排期是否存在且有效
        Optional<CourseSchedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (!scheduleOpt.isPresent()) {
            throw new RuntimeException("课程排期不存在");
        }
        CourseSchedule schedule = scheduleOpt.get();
        if (schedule.getStatus() != 1) {
            throw new RuntimeException("课程已取消");
        }

        // 3. 检查是否已约过该课程(防重复约课)
        Optional<MemberCourseEnrollment> existingEnrollment = 
            enrollmentRepository.findByMemberIdAndScheduleId(memberId, scheduleId);
        if (existingEnrollment.isPresent()) {
            MemberCourseEnrollment enrollment = existingEnrollment.get();
            if (enrollment.getStatus() == 0) {
                throw new RuntimeException("您已报名该课程");
            } else if (enrollment.getStatus() == 1) {
                throw new RuntimeException("您已参加过该课程");
            }
        }

        // 4. 检查当天是否有约课冲突
        List<MemberCourseEnrollment> dayEnrollments = 
            enrollmentRepository.findMemberCoursesOnDate(memberId, schedule.getScheduleDate());
        if (!dayEnrollments.isEmpty()) {
            throw new RuntimeException("您当天已有约课，无法重复预约");
        }

        // 5. 检查课程是否已满
        Long enrolledCount = enrollmentRepository.countEnrolledMembers(scheduleId);
        if (enrolledCount < schedule.getMaxParticipants()) {
            // 课程未满，直接报名
            return createEnrollment(memberId, scheduleId, 0);
        } else {
            // 课程已满，进入排队
            Integer queuePosition = enrollmentRepository.getMaxQueuePosition(scheduleId);
            queuePosition = (queuePosition == null) ? 1 : queuePosition + 1;
            MemberCourseEnrollment enrollment = createEnrollment(memberId, scheduleId, queuePosition);
            
            // 发送排队通知
            sendNotification(memberId, "课程排队成功", 
                "您已成功进入\"" + getCourseName(schedule.getCourseId()) + "\"课程的排队队列，当前位置：" + queuePosition, 
                "CLASS_QUEUE");
            
            return enrollment;
        }
    }

    private MemberCourseEnrollment createEnrollment(Long memberId, Long scheduleId, int queuePosition) {
        MemberCourseEnrollment enrollment = new MemberCourseEnrollment();
        enrollment.setMemberId(memberId);
        enrollment.setScheduleId(scheduleId);
        enrollment.setStatus(0);
        enrollment.setQueuePosition(queuePosition);
        return enrollmentRepository.save(enrollment);
    }

    private String getCourseName(Long courseId) {
        return courseRepository.findById(courseId)
            .map(Course::getCourseName)
            .orElse("未知课程");
    }

    // 取消约课 - 自动补位
    @Transactional
    public void cancelEnrollment(Long enrollmentId) {
        Optional<MemberCourseEnrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (!enrollmentOpt.isPresent()) {
            throw new RuntimeException("报名记录不存在");
        }

        MemberCourseEnrollment enrollment = enrollmentOpt.get();
        if (enrollment.getStatus() != 0) {
            throw new RuntimeException("课程状态不允许取消");
        }

        Long scheduleId = enrollment.getScheduleId();
        Long memberId = enrollment.getMemberId();

        // 如果是已报名的会员（非排队），需要触发补位
        if (enrollment.getQueuePosition() == 0) {
            // 查找排队列表中的第一个会员
            List<MemberCourseEnrollment> queueList = 
                enrollmentRepository.getCourseQueueList(scheduleId);
            
            if (!queueList.isEmpty()) {
                // 第一个排队会员补位
                MemberCourseEnrollment firstInQueue = queueList.get(0);
                firstInQueue.setQueuePosition(0);
                enrollmentRepository.save(firstInQueue);

                // 发送补位成功通知
                sendNotification(firstInQueue.getMemberId(), "课程补位成功",
                    "您已成功补位\"" + getCourseName(getCourseIdFromSchedule(scheduleId)) + "\"课程，请准时参加。",
                    "CLASS_ENROLL_SUCCESS");

                // 更新后续排队位置
                for (int i = 1; i < queueList.size(); i++) {
                    MemberCourseEnrollment qe = queueList.get(i);
                    qe.setQueuePosition(qe.getQueuePosition() - 1);
                    enrollmentRepository.save(qe);

                    // 发送排队位置更新通知
                    sendNotification(qe.getMemberId(), "排队位置更新",
                        "您在\"" + getCourseName(getCourseIdFromSchedule(scheduleId)) + "\"课程的排队位置已更新为：" + (qe.getQueuePosition()),
                        "QUEUE_POSITION_UPDATE");
                }
            }
        } else {
            // 如果是排队会员，需要更新后续排队位置
            Integer currentPosition = enrollment.getQueuePosition();
            List<MemberCourseEnrollment> queueList = 
                enrollmentRepository.getCourseQueueList(scheduleId);
            
            for (MemberCourseEnrollment qe : queueList) {
                if (qe.getQueuePosition() > currentPosition) {
                    qe.setQueuePosition(qe.getQueuePosition() - 1);
                    enrollmentRepository.save(qe);
                }
            }
        }

        // 更新报名状态为已取消
        enrollment.setStatus(2);
        enrollmentRepository.save(enrollment);

        // 发送取消成功通知
        sendNotification(memberId, "课程已取消",
            "您已成功取消\"" + getCourseName(getCourseIdFromSchedule(scheduleId)) + "\"课程的报名。",
            "CLASS_CANCEL_SUCCESS");
    }

    private Long getCourseIdFromSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
            .map(CourseSchedule::getCourseId)
            .orElse(0L);
    }

    // 上课签到
    @Transactional
    public void signInCourse(Long enrollmentId) {
        Optional<MemberCourseEnrollment> enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (!enrollmentOpt.isPresent()) {
            throw new RuntimeException("报名记录不存在");
        }

        MemberCourseEnrollment enrollment = enrollmentOpt.get();
        if (enrollment.getStatus() != 0) {
            throw new RuntimeException("无法签到：课程状态异常");
        }

        if (enrollment.getQueuePosition() > 0) {
            throw new RuntimeException("无法签到：您正在排队中");
        }

        enrollment.setStatus(1);
        enrollmentRepository.save(enrollment);

        // 发送签到成功通知
        sendNotification(enrollment.getMemberId(), "签到成功",
            "您已成功签到\"" + getCourseName(getCourseIdFromSchedule(enrollment.getScheduleId())) + "\"课程。",
            "CLASS_SIGN_IN");
    }

    // 体测数据推荐课程
    public List<Course> recommendCourses(Long memberId, Map<String, Double> fitnessMetrics) {
        // 简化的推荐逻辑：根据体脂率、BMI等指标推荐课程
        List<Course> allCourses = getActiveCourses();
        List<Course> recommended = new ArrayList<>();

        Double bodyFat = fitnessMetrics.get("BODY_FAT");
        Double bmi = fitnessMetrics.get("BMI");

        for (Course course : allCourses) {
            boolean shouldRecommend = false;

            // 根据体脂率推荐
            if (bodyFat != null) {
                if (bodyFat > 25 && course.getCourseName().contains("减脂")) {
                    shouldRecommend = true;
                } else if (bodyFat < 15 && course.getCourseName().contains("增肌")) {
                    shouldRecommend = true;
                }
            }

            // 根据BMI推荐
            if (bmi != null) {
                if (bmi > 28 && course.getCourseName().contains("减肥")) {
                    shouldRecommend = true;
                } else if (bmi < 18.5 && course.getCourseName().contains("塑形")) {
                    shouldRecommend = true;
                }
            }

            if (shouldRecommend) {
                recommended.add(course);
            }
        }

        return recommended;
    }

    // 获取会员的约课列表
    public List<MemberCourseEnrollment> getMemberEnrollments(Long memberId) {
        return enrollmentRepository.findByMemberId(memberId);
    }

    // 获取会员的排队列表
    public List<MemberCourseEnrollment> getMemberQueueingCourses(Long memberId) {
        return enrollmentRepository.findMemberQueueingCourses(memberId);
    }

    // 获取课程的排队列表
    public List<MemberCourseEnrollment> getCourseQueueList(Long scheduleId) {
        return enrollmentRepository.getCourseQueueList(scheduleId);
    }

    private void sendNotification(Long memberId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setMemberId(memberId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(0);
        notificationRepository.save(notification);
    }
}
