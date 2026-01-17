package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseBookingRepository courseBookingRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentUsageRepository equipmentUsageRepository;

    @Autowired
    private BodyMeasurementRepository bodyMeasurementRepository;

    public Map<String, Object> predictMemberRenewal(Long memberId) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        
        Map<String, Object> prediction = new HashMap<>();
        double renewalProbability = calculateRenewalProbability(member);
        
        prediction.put("memberId", memberId);
        prediction.put("memberName", member.getName());
        prediction.put("renewalProbability", Math.round(renewalProbability * 100.0) / 100.0);
        prediction.put("prediction", getPredictionLabel(renewalProbability));
        prediction.put("factors", getRenewalFactors(member));
        
        return prediction;
    }

    private double calculateRenewalProbability(Member member) {
        double score = 0.0;
        
        List<CourseBooking> bookings = courseBookingRepository.findByMemberId(member.getId());
        int activeBookings = (int) bookings.stream()
                .filter(b -> b.getStatus() == CourseBooking.BookingStatus.CONFIRMED)
                .count();
        
        if (activeBookings > 10) score += 0.3;
        else if (activeBookings > 5) score += 0.2;
        else if (activeBookings > 0) score += 0.1;
        
        List<BodyMeasurement> measurements = bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(member.getId());
        if (!measurements.isEmpty()) {
            score += 0.2;
        }
        
        if (member.getPoints() != null && member.getPoints() > 1000) {
            score += 0.2;
        } else if (member.getPoints() != null && member.getPoints() > 500) {
            score += 0.1;
        }
        
        if (member.getCardLevel() == Member.CardLevel.DIAMOND) {
            score += 0.3;
        } else if (member.getCardLevel() == Member.CardLevel.GOLD) {
            score += 0.2;
        } else if (member.getCardLevel() == Member.CardLevel.SILVER) {
            score += 0.1;
        }
        
        return Math.min(score, 1.0);
    }

    private String getPredictionLabel(double probability) {
        if (probability >= 0.8) return "极有可能续费";
        if (probability >= 0.6) return "很可能续费";
        if (probability >= 0.4) return "可能续费";
        if (probability >= 0.2) return "不太可能续费";
        return "极不可能续费";
    }

    private List<String> getRenewalFactors(Member member) {
        List<String> factors = new ArrayList<>();
        
        List<CourseBooking> bookings = courseBookingRepository.findByMemberId(member.getId());
        long activeBookings = bookings.stream()
                .filter(b -> b.getStatus() == CourseBooking.BookingStatus.CONFIRMED)
                .count();
        
        if (activeBookings > 5) {
            factors.add("活跃度高，经常参加课程");
        }
        
        List<BodyMeasurement> measurements = bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(member.getId());
        if (!measurements.isEmpty()) {
            factors.add("定期进行体测");
        }
        
        if (member.getPoints() != null && member.getPoints() > 500) {
            factors.add("积分较高，参与度好");
        }
        
        if (member.getCardLevel() != Member.CardLevel.BRONZE) {
            factors.add("会员等级较高");
        }
        
        return factors;
    }

    public List<Map<String, Object>> getCoursePopularityRanking(Long storeId, int limit) {
        List<Course> courses;
        if (storeId != null) {
            courses = courseRepository.findByStoreId(storeId);
        } else {
            courses = courseRepository.findAll();
        }
        
        List<Map<String, Object>> ranking = new ArrayList<>();
        
        for (Course course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("courseId", course.getId());
            courseData.put("courseName", course.getName());
            courseData.put("type", course.getType());
            courseData.put("totalBookings", course.getCurrentEnrolled() + course.getQueueCount());
            courseData.put("currentEnrolled", course.getCurrentEnrolled());
            courseData.put("queueCount", course.getQueueCount());
            courseData.put("capacity", course.getCapacity());
            courseData.put("fillRate", calculateFillRate(course));
            ranking.add(courseData);
        }
        
        ranking.sort((a, b) -> {
            Integer bookingsA = (Integer) a.get("totalBookings");
            Integer bookingsB = (Integer) b.get("totalBookings");
            return bookingsB.compareTo(bookingsA);
        });
        
        return ranking.stream().limit(limit).collect(Collectors.toList());
    }

    private double calculateFillRate(Course course) {
        if (course.getCapacity() == 0) return 0.0;
        return Math.round((double) course.getCurrentEnrolled() / course.getCapacity() * 100.0 * 10) / 10.0;
    }

    public Map<String, Object> getEquipmentUsageStatistics(Long equipmentId, Date startDate, Date endDate) {
        Equipment equipment = equipmentRepository.findOne(equipmentId);
        if (equipment == null) {
            throw new RuntimeException("器材不存在");
        }
        
        Map<String, Object> statistics = new HashMap<>();
        
        Double totalUsageHours = equipmentUsageRepository.getTotalUsageHours(equipmentId, startDate, endDate);
        Long usageCount = equipmentUsageRepository.getUsageCount(equipmentId, startDate, endDate);
        
        statistics.put("equipmentId", equipmentId);
        statistics.put("equipmentName", equipment.getName());
        statistics.put("type", equipment.getType());
        statistics.put("totalUsageHours", totalUsageHours != null ? totalUsageHours : 0.0);
        statistics.put("usageCount", usageCount != null ? usageCount : 0L);
        statistics.put("averageUsageTime", usageCount != null && usageCount > 0 
            ? Math.round((totalUsageHours / usageCount) * 10.0) / 10.0 : 0.0);
        statistics.put("usageRate", calculateUsageRate(equipmentId, startDate, endDate));
        
        return statistics;
    }

    private double calculateUsageRate(Long equipmentId, Date startDate, Date endDate) {
        long days = (endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
        if (days == 0) days = 1;
        
        Long usageCount = equipmentUsageRepository.getUsageCount(equipmentId, startDate, endDate);
        double maxPossibleUsage = days * 12.0;
        
        return Math.round((usageCount / maxPossibleUsage) * 100.0 * 10) / 10.0;
    }

    public List<Map<String, Object>> getAllEquipmentUsageStatistics(Long storeId, Date startDate, Date endDate) {
        List<Equipment> equipments;
        if (storeId != null) {
            equipments = equipmentRepository.findByStoreId(storeId);
        } else {
            equipments = equipmentRepository.findAll();
        }
        
        List<Map<String, Object>> statisticsList = new ArrayList<>();
        
        for (Equipment equipment : equipments) {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("equipmentId", equipment.getId());
            statistics.put("equipmentName", equipment.getName());
            statistics.put("type", equipment.getType());
            statistics.put("brand", equipment.getBrand());
            
            Double totalUsageHours = equipmentUsageRepository.getTotalUsageHours(equipment.getId(), startDate, endDate);
            Long usageCount = equipmentUsageRepository.getUsageCount(equipment.getId(), startDate, endDate);
            
            statistics.put("totalUsageHours", totalUsageHours != null ? totalUsageHours : 0.0);
            statistics.put("usageCount", usageCount != null ? usageCount : 0L);
            statistics.put("averageUsageTime", usageCount != null && usageCount > 0 
                ? Math.round((totalUsageHours / usageCount) * 10.0) / 10.0 : 0.0);
            statistics.put("usageRate", calculateUsageRate(equipment.getId(), startDate, endDate));
            
            statisticsList.add(statistics);
        }
        
        statisticsList.sort((a, b) -> {
            Double hoursA = (Double) a.get("totalUsageHours");
            Double hoursB = (Double) b.get("totalUsageHours");
            return hoursB.compareTo(hoursA);
        });
        
        return statisticsList;
    }

    public Map<String, Object> getOverallStatistics(Long storeId) {
        Map<String, Object> overallStats = new HashMap<>();
        
        long totalMembers = storeId != null 
            ? memberRepository.findByStoreId(storeId).size()
            : memberRepository.count();
        
        long activeMembers = storeId != null
            ? memberRepository.findByStoreId(storeId).stream()
                .filter(m -> m.getStatus() == Member.MemberStatus.ACTIVE)
                .count()
            : memberRepository.findByStatus(Member.MemberStatus.ACTIVE).size();
        
        long totalCourses = storeId != null
            ? courseRepository.findByStoreId(storeId).size()
            : courseRepository.count();
        
        long totalEquipments = storeId != null
            ? equipmentRepository.findByStoreId(storeId).size()
            : equipmentRepository.count();
        
        overallStats.put("totalMembers", totalMembers);
        overallStats.put("activeMembers", activeMembers);
        overallStats.put("totalCourses", totalCourses);
        overallStats.put("totalEquipments", totalEquipments);
        overallStats.put("memberActivityRate", totalMembers > 0 
            ? Math.round((double) activeMembers / totalMembers * 100.0 * 10) / 10.0 : 0.0);
        
        return overallStats;
    }

    public Map<String, Object> getMemberDistributionByLevel(Long storeId) {
        Map<String, Object> distribution = new HashMap<>();
        
        List<Member> members = storeId != null
            ? memberRepository.findByStoreId(storeId)
            : memberRepository.findAll();
        
        Map<Member.CardLevel, Long> levelCount = members.stream()
            .collect(Collectors.groupingBy(Member::getCardLevel, Collectors.counting()));
        
        distribution.put("BRONZE", levelCount.getOrDefault(Member.CardLevel.BRONZE, 0L));
        distribution.put("SILVER", levelCount.getOrDefault(Member.CardLevel.SILVER, 0L));
        distribution.put("GOLD", levelCount.getOrDefault(Member.CardLevel.GOLD, 0L));
        distribution.put("DIAMOND", levelCount.getOrDefault(Member.CardLevel.DIAMOND, 0L));
        
        return distribution;
    }

    public Map<String, Object> getMemberDistributionByStatus(Long storeId) {
        Map<String, Object> distribution = new HashMap<>();
        
        List<Member> members = storeId != null
            ? memberRepository.findByStoreId(storeId)
            : memberRepository.findAll();
        
        Map<Member.MemberStatus, Long> statusCount = members.stream()
            .collect(Collectors.groupingBy(Member::getStatus, Collectors.counting()));
        
        distribution.put("ACTIVE", statusCount.getOrDefault(Member.MemberStatus.ACTIVE, 0L));
        distribution.put("FROZEN", statusCount.getOrDefault(Member.MemberStatus.FROZEN, 0L));
        distribution.put("EXPIRED", statusCount.getOrDefault(Member.MemberStatus.EXPIRED, 0L));
        distribution.put("PENDING_FREEZE", statusCount.getOrDefault(Member.MemberStatus.PENDING_FREEZE, 0L));
        
        return distribution;
    }
}
