package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DataStatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(DataStatisticsService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCourseEnrollmentRepository memberCourseEnrollmentRepository;

    @Autowired
    private CourseScheduleRepository courseScheduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentUsageRecordRepository equipmentUsageRecordRepository;

    @Autowired
    private FitnessTestRecordRepository fitnessTestRecordRepository;

    public Map<String, Object> predictMemberRenewal(Long memberId) {
        Map<String, Object> prediction = new HashMap<>();

        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        Member member = memberOpt.get();

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MONTH, -3);
        Date threeMonthsAgo = calendar.getTime();

        long courseCount = memberCourseEnrollmentRepository.countByMemberIdAndEnrollTimeBetween(memberId, threeMonthsAgo, now);
        long testCount = fitnessTestRecordRepository.countByMemberId(memberId);

        double usageScore = 0;
        if (courseCount >= 12) {
            usageScore = 90;
        } else if (courseCount >= 6) {
            usageScore = 70;
        } else if (courseCount >= 3) {
            usageScore = 50;
        } else {
            usageScore = 20;
        }

        double testScore = testCount >= 2 ? 90 : (testCount == 1 ? 60 : 30);

        Date cardEndDate = member.getCardEndDate();
        long daysUntilExpire = (cardEndDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
        double timeScore = daysUntilExpire > 30 ? 80 : (daysUntilExpire > 7 ? 50 : 20);

        double totalScore = usageScore * 0.4 + testScore * 0.3 + timeScore * 0.3;
        double renewalProbability = Math.min(100, Math.max(0, totalScore));

        prediction.put("memberId", memberId);
        prediction.put("memberName", member.getName());
        prediction.put("memberNo", member.getMemberNo());
        prediction.put("currentLevel", member.getLevelCode());
        prediction.put("cardEndDate", cardEndDate);
        prediction.put("daysUntilExpire", daysUntilExpire);
        prediction.put("courseCount3Months", courseCount);
        prediction.put("testCountTotal", testCount);
        prediction.put("usageScore", usageScore);
        prediction.put("testScore", testScore);
        prediction.put("timeScore", timeScore);
        prediction.put("renewalProbability", renewalProbability);
        prediction.put("suggestion", getRenewalSuggestion(renewalProbability, member));

        return prediction;
    }

    private String getRenewalSuggestion(double probability, Member member) {
        if (probability >= 70) {
            return "该会员续费意愿高，建议推荐升级卡种或长期套餐。";
        } else if (probability >= 40) {
            return "该会员续费意愿中等，建议提供限时优惠或赠送额外权益。";
        } else {
            return "该会员续费意愿较低，建议进行电话回访了解原因，提供个性化挽留方案。";
        }
    }

    public List<Map<String, Object>> getCourseHotRank(int days) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date startDate = calendar.getTime();

        List<CourseSchedule> schedules = courseScheduleRepository.findByScheduleDateBetween(startDate, endDate);
        if (schedules.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> courseEnrollmentCount = new HashMap<>();

        for (CourseSchedule schedule : schedules) {
            Long count = memberCourseEnrollmentRepository.countByScheduleIdAndStatus(schedule.getId(), 0);
            count += memberCourseEnrollmentRepository.countByScheduleIdAndStatus(schedule.getId(), 1);

            courseEnrollmentCount.merge(schedule.getCourseId(), count, Long::sum);
        }

        List<Map.Entry<Long, Long>> sortedCourses = courseEnrollmentCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;

        for (Map.Entry<Long, Long> entry : sortedCourses) {
            Optional<Course> courseOpt = courseRepository.findById(entry.getKey());
            if (courseOpt.isPresent()) {
                Course course = courseOpt.get();
                Map<String, Object> item = new HashMap<>();
                item.put("rank", rank++);
                item.put("courseId", course.getId());
                item.put("courseName", course.getCourseName());
                item.put("courseCode", course.getCourseCode());
                item.put("totalEnrollments", entry.getValue());
                item.put("avgEnrollments", (double) entry.getValue() / days);
                item.put("price", course.getPrice());
                item.put("difficultyLevel", course.getDifficultyLevel());
                result.add(item);
            }
        }

        return result;
    }

    public List<Map<String, Object>> getEquipmentUsageStatistics(Long branchId, int days) {
        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date startDate = calendar.getTime();

        List<Equipment> equipments = branchId == null ? equipmentRepository.findAll() : equipmentRepository.findByBranchId(branchId);
        if (equipments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (Equipment equipment : equipments) {
            Long usageCount = equipmentUsageRecordRepository.countUsageByEquipmentIdAndDate(equipment.getId(), startDate);
            Long totalDuration = equipmentUsageRecordRepository.sumUsageDurationByEquipmentIdAndDate(equipment.getId(), startDate);

            int dailyHours = 12;
            double usageRate = 0;
            if (totalDuration != null && totalDuration > 0) {
                usageRate = (totalDuration * 100.0) / (days * dailyHours * 60);
            }

            Map<String, Object> item = new HashMap<>();
            item.put("equipmentId", equipment.getId());
            item.put("equipmentName", equipment.getEquipmentName());
            item.put("equipmentCode", equipment.getEquipmentCode());
            item.put("type", equipment.getType());
            item.put("branchId", equipment.getBranchId());
            item.put("status", equipment.getStatus());
            item.put("usageCount", usageCount);
            item.put("totalDuration", totalDuration != null ? totalDuration : 0);
            item.put("avgDailyDuration", totalDuration != null ? (double) totalDuration / days : 0);
            item.put("usageRate", usageRate);

            result.add(item);
        }

        result.sort((a, b) -> Double.compare((Double) b.get("usageRate"), (Double) a.get("usageRate")));

        return result;
    }

    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.countByStatus(1);
        long expiredMembers = memberRepository.countByStatus(0);
        long frozenMembers = memberRepository.countByStatus(2);

        long totalCourses = courseRepository.count();
        long activeCourses = courseRepository.countByStatus(1);

        long totalEquipments = equipmentRepository.count();
        long activeEquipments = equipmentRepository.countActiveEquipment(null);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();

        long todayCourses = courseScheduleRepository.countByScheduleDate(today);
        long yesterdayCourses = courseScheduleRepository.countByScheduleDate(yesterday);

        long todayEnrollments = memberCourseEnrollmentRepository.countByEnrollTimeBetween(yesterday, today);

        statistics.put("totalMembers", totalMembers);
        statistics.put("activeMembers", activeMembers);
        statistics.put("expiredMembers", expiredMembers);
        statistics.put("frozenMembers", frozenMembers);
        statistics.put("activeMembersRate", totalMembers > 0 ? (activeMembers * 100.0 / totalMembers) : 0);

        statistics.put("totalCourses", totalCourses);
        statistics.put("activeCourses", activeCourses);
        statistics.put("activeCoursesRate", totalCourses > 0 ? (activeCourses * 100.0 / totalCourses) : 0);

        statistics.put("totalEquipments", totalEquipments);
        statistics.put("activeEquipments", activeEquipments);
        statistics.put("equipmentActiveRate", totalEquipments > 0 ? (activeEquipments * 100.0 / totalEquipments) : 0);

        statistics.put("todayCourses", todayCourses);
        statistics.put("yesterdayCourses", yesterdayCourses);
        statistics.put("courseChangeRate", yesterdayCourses > 0 ? ((todayCourses - yesterdayCourses) * 100.0 / yesterdayCourses) : 0);

        statistics.put("todayEnrollments", todayEnrollments);

        return statistics;
    }

    public List<Map<String, Object>> getMemberActivityRank(int topN) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date oneMonthAgo = calendar.getTime();

        List<Object[]> memberActivity = memberCourseEnrollmentRepository.countEnrollmentsByMember(oneMonthAgo, now);

        List<Map<String, Object>> result = new ArrayList<>();
        int rank = 1;

        for (Object[] row : memberActivity) {
            if (rank > topN) break;

            Long memberId = (Long) row[0];
            Long count = (Long) row[1];

            Optional<Member> memberOpt = memberRepository.findById(memberId);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                Map<String, Object> item = new HashMap<>();
                item.put("rank", rank++);
                item.put("memberId", memberId);
                item.put("memberName", member.getName());
                item.put("memberNo", member.getMemberNo());
                item.put("level", member.getLevelCode());
                item.put("courseCount", count);
                result.add(item);
            }
        }

        return result;
    }

    public Map<String, Object> getBranchStatistics(Long branchId) {
        Map<String, Object> statistics = new HashMap<>();

        long memberCount = memberRepository.countByBranchId(branchId);
        long activeMemberCount = memberRepository.countByBranchIdAndStatus(branchId, 1);

        long equipmentCount = equipmentRepository.countTotalEquipment(branchId);
        long activeEquipmentCount = equipmentRepository.countActiveEquipment(branchId);

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.MONTH, -1);
        Date oneMonthAgo = calendar.getTime();

        long courseCount = courseScheduleRepository.countByBranchIdAndScheduleDateBetween(branchId, oneMonthAgo, now);

        statistics.put("branchId", branchId);
        statistics.put("memberCount", memberCount);
        statistics.put("activeMemberCount", activeMemberCount);
        statistics.put("memberActiveRate", memberCount > 0 ? (activeMemberCount * 100.0 / memberCount) : 0);
        statistics.put("equipmentCount", equipmentCount);
        statistics.put("activeEquipmentCount", activeEquipmentCount);
        statistics.put("equipmentActiveRate", equipmentCount > 0 ? (activeEquipmentCount * 100.0 / equipmentCount) : 0);
        statistics.put("monthlyCourseCount", courseCount);

        return statistics;
    }
}