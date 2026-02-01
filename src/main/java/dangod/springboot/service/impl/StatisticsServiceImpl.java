package dangod.springboot.service.impl;

import dangod.springboot.model.EquipmentUsage;
import dangod.springboot.model.Member;
import dangod.springboot.model.Gym;
import dangod.springboot.model.Course;
import dangod.springboot.model.CourseBooking;
import dangod.springboot.dto.EquipmentUsageDto;
import dangod.springboot.dto.RenewalPredictionDto;
import dangod.springboot.dto.CoursePopularityDto;
import dangod.springboot.repository.EquipmentUsageRepository;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.GymRepository;
import dangod.springboot.repository.CourseRepository;
import dangod.springboot.repository.CourseBookingRepository;
import dangod.springboot.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private EquipmentUsageRepository equipmentUsageRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private GymRepository gymRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseBookingRepository courseBookingRepository;

    @Override
    public EquipmentUsage recordEquipmentUsage(EquipmentUsageDto usageDto) {
        // 检查会员是否存在
        Member member = memberRepository.findOne(usageDto.getMemberId());
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        
        // 检查门店是否存在
        Gym gym = gymRepository.findByStoreId(usageDto.getGymId());
        if (gym == null) {
            throw new RuntimeException("门店不存在");
        }
        
        // 计算使用时长（分钟）
        int durationMinutes = 0;
        if (usageDto.getDurationMinutes() == null) {
            durationMinutes = (int) Duration.between(usageDto.getStartTime(), usageDto.getEndTime()).toMinutes();
        } else {
            durationMinutes = usageDto.getDurationMinutes();
        }
        
        // 创建器材使用记录
        EquipmentUsage usage = new EquipmentUsage();
        usage.setMember(member);
        usage.setStartTime(usageDto.getStartTime());
        usage.setEndTime(usageDto.getEndTime());
        usage.setDurationMinutes(durationMinutes);
        usage.setNotes(usageDto.getNotes());
        
        return equipmentUsageRepository.save(usage);
    }

    @Override
    public List<EquipmentUsage> getEquipmentUsagesByEquipment(String equipmentId) {
        return equipmentUsageRepository.findByEquipmentId(equipmentId);
    }

    @Override
    public List<EquipmentUsage> getEquipmentUsagesBetween(LocalDateTime startTime, LocalDateTime endTime) {
        return equipmentUsageRepository.findUsagesBetween(startTime, endTime);
    }

    @Override
    public List<EquipmentUsage> getEquipmentUsagesBetweenByStore(LocalDateTime startTime, LocalDateTime endTime, String storeId) {
        return equipmentUsageRepository.findUsagesBetweenByStore(startTime, endTime, storeId);
    }

    @Override
    public Map<String, Object> getEquipmentUsageStats(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> stats = equipmentUsageRepository.getEquipmentUsageStats(startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> equipmentStats = new ArrayList<>();
        
        for (Object[] stat : stats) {
            Map<String, Object> equipmentStat = new HashMap<>();
            equipmentStat.put("equipmentId", stat[0]);
            equipmentStat.put("usageCount", stat[1]);
            equipmentStat.put("totalDuration", stat[2]);
            
            // 计算平均使用时长
            int usageCount = ((Number) stat[1]).intValue();
            int totalDuration = ((Number) stat[2]).intValue();
            double avgDuration = usageCount > 0 ? (double) totalDuration / usageCount : 0;
            equipmentStat.put("avgDuration", avgDuration);
            
            equipmentStats.add(equipmentStat);
        }
        
        // 按使用次数排序
        equipmentStats.sort((a, b) -> ((Integer) b.get("usageCount")).compareTo((Integer) a.get("usageCount")));
        
        result.put("equipmentStats", equipmentStats);
        result.put("totalEquipmentCount", equipmentStats.size());
        
        return result;
    }

    @Override
    public Map<String, Object> getEquipmentUsageStatsByStore(LocalDateTime startTime, LocalDateTime endTime, String storeId) {
        List<Object[]> stats = equipmentUsageRepository.getEquipmentUsageStatsByStore(startTime, endTime, storeId);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> equipmentStats = new ArrayList<>();
        
        for (Object[] stat : stats) {
            Map<String, Object> equipmentStat = new HashMap<>();
            equipmentStat.put("equipmentId", stat[0]);
            equipmentStat.put("usageCount", stat[1]);
            equipmentStat.put("totalDuration", stat[2]);
            
            // 计算平均使用时长
            int usageCount = ((Number) stat[1]).intValue();
            int totalDuration = ((Number) stat[2]).intValue();
            double avgDuration = usageCount > 0 ? (double) totalDuration / usageCount : 0;
            equipmentStat.put("avgDuration", avgDuration);
            
            equipmentStats.add(equipmentStat);
        }
        
        // 按使用次数排序
        equipmentStats.sort((a, b) -> ((Integer) b.get("usageCount")).compareTo((Integer) a.get("usageCount")));
        
        result.put("equipmentStats", equipmentStats);
        result.put("storeId", storeId);
        result.put("totalEquipmentCount", equipmentStats.size());
        
        return result;
    }

    @Override
    public Map<String, Object> getSpecificEquipmentUsageStats(String equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> stats = equipmentUsageRepository.getSpecificEquipmentUsageStats(equipmentId, startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        
        if (!stats.isEmpty()) {
            Object[] stat = stats.get(0);
            result.put("equipmentId", stat[0]);
            result.put("usageCount", stat[1]);
            result.put("totalDuration", stat[2]);
            
            // 计算平均使用时长
            int usageCount = ((Number) stat[1]).intValue();
            int totalDuration = ((Number) stat[2]).intValue();
            double avgDuration = usageCount > 0 ? (double) totalDuration / usageCount : 0;
            result.put("avgDuration", avgDuration);
            
            // 获取详细使用记录
            List<EquipmentUsage> usages = equipmentUsageRepository.findEquipmentUsagesByTimeRange(equipmentId, startTime, endTime);
            result.put("usages", usages);
        }
        
        return result;
    }

    @Override
    public List<RenewalPredictionDto> predictMemberRenewals() {
        // 获取所有会员
        List<Member> members = memberRepository.findAll();
        List<RenewalPredictionDto> predictions = new ArrayList<>();
        
        for (Member member : members) {
            RenewalPredictionDto prediction = predictMemberRenewal(member);
            predictions.add(prediction);
        }
        
        // 按续费概率排序
        predictions.sort((a, b) -> b.getRenewalProbability().compareTo(a.getRenewalProbability()));
        
        return predictions;
    }

    @Override
    public List<RenewalPredictionDto> predictMemberRenewalsByStore(String storeId) {
        // 获取指定门店的会员
        List<Member> members = memberRepository.findByStoreId(storeId);
        List<RenewalPredictionDto> predictions = new ArrayList<>();
        
        for (Member member : members) {
            RenewalPredictionDto prediction = predictMemberRenewal(member);
            predictions.add(prediction);
        }
        
        // 按续费概率排序
        predictions.sort((a, b) -> b.getRenewalProbability().compareTo(a.getRenewalProbability()));
        
        return predictions;
    }

    @Override
    public List<RenewalPredictionDto> predictMemberRenewalsByRiskLevel(String riskLevel) {
        List<RenewalPredictionDto> allPredictions = predictMemberRenewals();
        
        return allPredictions.stream()
                .filter(p -> riskLevel.equals(p.getRiskLevel()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CoursePopularityDto> getCoursePopularityRanking() {
        // 获取所有课程
        List<Course> courses = courseRepository.findAll();
        List<CoursePopularityDto> popularityList = new ArrayList<>();
        
        for (Course course : courses) {
            CoursePopularityDto popularity = calculateCoursePopularity(course);
            popularityList.add(popularity);
        }
        
        // 按热度分数排序
        popularityList.sort((a, b) -> b.getPopularityScore().compareTo(a.getPopularityScore()));
        
        return popularityList;
    }

    @Override
    public List<CoursePopularityDto> getCoursePopularityRankingByStore(String storeId) {
        // 获取指定门店的课程
        List<Course> courses = courseRepository.findByGym_StoreId(storeId);
        List<CoursePopularityDto> popularityList = new ArrayList<>();
        
        for (Course course : courses) {
            CoursePopularityDto popularity = calculateCoursePopularity(course);
            popularityList.add(popularity);
        }
        
        // 按热度分数排序
        popularityList.sort((a, b) -> b.getPopularityScore().compareTo(a.getPopularityScore()));
        
        return popularityList;
    }

    @Override
    public List<CoursePopularityDto> getCoursePopularityRankingByCategory(String category) {
        // 获取指定类别的课程
        List<Course> courses = courseRepository.findByCategory(category);
        List<CoursePopularityDto> popularityList = new ArrayList<>();
        
        for (Course course : courses) {
            CoursePopularityDto popularity = calculateCoursePopularity(course);
            popularityList.add(popularity);
        }
        
        // 按热度分数排序
        popularityList.sort((a, b) -> b.getPopularityScore().compareTo(a.getPopularityScore()));
        
        return popularityList;
    }

    @Override
    public List<CoursePopularityDto> getCoursePopularityRankingByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        // 获取指定时间范围内的课程
        List<Course> courses = courseRepository.findCoursesBetween(startTime, endTime);
        List<CoursePopularityDto> popularityList = new ArrayList<>();
        
        for (Course course : courses) {
            CoursePopularityDto popularity = calculateCoursePopularity(course);
            popularityList.add(popularity);
        }
        
        // 按热度分数排序
        popularityList.sort((a, b) -> b.getPopularityScore().compareTo(a.getPopularityScore()));
        
        return popularityList;
    }

    @Override
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 会员统计
        long totalMembers = memberRepository.count();
        long activeMembers = memberRepository.findByStatus(dangod.springboot.enums.MemberStatus.ACTIVE).size();
        long frozenMembers = memberRepository.findByStatus(dangod.springboot.enums.MemberStatus.FROZEN).size();
        long expiredMembers = memberRepository.findByStatus(dangod.springboot.enums.MemberStatus.EXPIRED).size();
        
        Map<String, Object> memberStats = new HashMap<>();
        memberStats.put("total", totalMembers);
        memberStats.put("active", activeMembers);
        memberStats.put("frozen", frozenMembers);
        memberStats.put("expired", expiredMembers);
        stats.put("members", memberStats);
        
        // 课程统计
        long totalCourses = courseRepository.count();
        long scheduledCourses = courseRepository.findByStatus(dangod.springboot.enums.CourseStatus.SCHEDULED).size();
        long completedCourses = courseRepository.findByStatus(dangod.springboot.enums.CourseStatus.COMPLETED).size();
        long cancelledCourses = courseRepository.findByStatus(dangod.springboot.enums.CourseStatus.CANCELLED).size();
        
        Map<String, Object> courseStats = new HashMap<>();
        courseStats.put("total", totalCourses);
        courseStats.put("scheduled", scheduledCourses);
        courseStats.put("completed", completedCourses);
        courseStats.put("cancelled", cancelledCourses);
        stats.put("courses", courseStats);
        
        // 器材使用统计
        long totalEquipmentUsages = equipmentUsageRepository.count();
        
        Map<String, Object> equipmentStats = new HashMap<>();
        equipmentStats.put("totalUsages", totalEquipmentUsages);
        stats.put("equipment", equipmentStats);
        
        return stats;
    }

    @Override
    public Map<String, Object> getStoreStatistics(String storeId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 会员统计
        List<Member> storeMembers = memberRepository.findByStoreId(storeId);
        long totalMembers = storeMembers.size();
        long activeMembers = storeMembers.stream()
                .filter(m -> m.getStatus() == dangod.springboot.enums.MemberStatus.ACTIVE)
                .count();
        long frozenMembers = storeMembers.stream()
                .filter(m -> m.getStatus() == dangod.springboot.enums.MemberStatus.FROZEN)
                .count();
        long expiredMembers = storeMembers.stream()
                .filter(m -> m.getStatus() == dangod.springboot.enums.MemberStatus.EXPIRED)
                .count();
        
        Map<String, Object> memberStats = new HashMap<>();
        memberStats.put("total", totalMembers);
        memberStats.put("active", activeMembers);
        memberStats.put("frozen", frozenMembers);
        memberStats.put("expired", expiredMembers);
        stats.put("members", memberStats);
        
        // 课程统计
        List<Course> storeCourses = courseRepository.findByGym_StoreId(storeId);
        long totalCourses = storeCourses.size();
        long scheduledCourses = storeCourses.stream()
                .filter(c -> c.getStatus() == dangod.springboot.enums.CourseStatus.SCHEDULED)
                .count();
        long completedCourses = storeCourses.stream()
                .filter(c -> c.getStatus() == dangod.springboot.enums.CourseStatus.COMPLETED)
                .count();
        long cancelledCourses = storeCourses.stream()
                .filter(c -> c.getStatus() == dangod.springboot.enums.CourseStatus.CANCELLED)
                .count();
        
        Map<String, Object> courseStats = new HashMap<>();
        courseStats.put("total", totalCourses);
        courseStats.put("scheduled", scheduledCourses);
        courseStats.put("completed", completedCourses);
        courseStats.put("cancelled", cancelledCourses);
        stats.put("courses", courseStats);
        
        // 器材使用统计
        List<EquipmentUsage> storeUsages = equipmentUsageRepository.findUsagesBetweenByStore(
                LocalDateTime.now().minusMonths(1), LocalDateTime.now(), storeId);
        long totalEquipmentUsages = storeUsages.size();
        
        Map<String, Object> equipmentStats = new HashMap<>();
        equipmentStats.put("totalUsages", totalEquipmentUsages);
        stats.put("equipment", equipmentStats);
        
        stats.put("storeId", storeId);
        
        return stats;
    }

    @Override
    public Map<String, Object> getMemberStatistics(Long memberId) {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new RuntimeException("会员不存在");
        }
        Map<String, Object> stats = new HashMap<>();
        
        // 基本信息
        stats.put("memberId", member.getId());
        stats.put("memberNumber", member.getMemberNumber());
        stats.put("name", member.getName());
        stats.put("cardType", member.getCardType());
        stats.put("status", member.getStatus());
        stats.put("joinDate", member.getJoinDate());
        stats.put("expiryDate", member.getExpiryDate());
        stats.put("totalSessions", member.getTotalSessions());
        stats.put("usedSessions", member.getUsedSessions());
        stats.put("remainingSessions", member.getTotalSessions() - member.getUsedSessions());
        
        // 课程预约统计
        List<CourseBooking> bookings = courseBookingRepository.findByMember_Id(memberId);
        long totalBookings = bookings.size();
        long attendedBookings = bookings.stream()
                .filter(b -> b.getStatus() == dangod.springboot.enums.BookingStatus.ATTENDED)
                .count();
        long cancelledBookings = bookings.stream()
                .filter(b -> b.getStatus() == dangod.springboot.enums.BookingStatus.CANCELLED)
                .count();
        
        Map<String, Object> bookingStats = new HashMap<>();
        bookingStats.put("total", totalBookings);
        bookingStats.put("attended", attendedBookings);
        bookingStats.put("cancelled", cancelledBookings);
        bookingStats.put("attendanceRate", totalBookings > 0 ? (double) attendedBookings / totalBookings : 0);
        stats.put("bookings", bookingStats);
        
        // 器材使用统计
        List<EquipmentUsage> usages = equipmentUsageRepository.findByEquipmentId(""); // 需要修改查询方法
        long totalUsages = usages.size();
        
        Map<String, Object> equipmentStats = new HashMap<>();
        equipmentStats.put("totalUsages", totalUsages);
        stats.put("equipment", equipmentStats);
        
        return stats;
    }

    @Override
    public Map<String, Object> getTrainerStatistics(Long trainerId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 课程统计
        List<Course> trainerCourses = courseRepository.findByTrainer_Id(trainerId);
        long totalCourses = trainerCourses.size();
        long scheduledCourses = trainerCourses.stream()
                .filter(c -> c.getStatus() == dangod.springboot.enums.CourseStatus.SCHEDULED)
                .count();
        long completedCourses = trainerCourses.stream()
                .filter(c -> c.getStatus() == dangod.springboot.enums.CourseStatus.COMPLETED)
                .count();
        long cancelledCourses = trainerCourses.stream()
                .filter(c -> c.getStatus() == dangod.springboot.enums.CourseStatus.CANCELLED)
                .count();
        
        Map<String, Object> courseStats = new HashMap<>();
        courseStats.put("total", totalCourses);
        courseStats.put("scheduled", scheduledCourses);
        courseStats.put("completed", completedCourses);
        courseStats.put("cancelled", cancelledCourses);
        stats.put("courses", courseStats);
        
        // 课程类别统计
        Map<String, Long> categoryStats = trainerCourses.stream()
                .collect(Collectors.groupingBy(Course::getCategory, Collectors.counting()));
        stats.put("categories", categoryStats);
        
        stats.put("trainerId", trainerId);
        
        return stats;
    }

    @Override
    public Map<String, Object> getCourseStatistics(Long courseId) {
        Course course = courseRepository.findOne(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        Map<String, Object> stats = new HashMap<>();
        
        // 基本信息
        stats.put("courseId", course.getId());
        stats.put("name", course.getName());
        stats.put("category", course.getCategory());
        stats.put("status", course.getStatus());
        stats.put("startTime", course.getStartTime());
        stats.put("endTime", course.getEndTime());
        stats.put("maxCapacity", course.getMaxCapacity());
        stats.put("currentBookings", course.getCurrentBookings());
        stats.put("bookingRate", (double) course.getCurrentBookings() / course.getMaxCapacity());
        
        // 预约统计
        List<CourseBooking> bookings = courseBookingRepository.findByCourse_Id(courseId);
        long totalBookings = bookings.size();
        long attendedBookings = bookings.stream()
                .filter(b -> b.getStatus() == dangod.springboot.enums.BookingStatus.ATTENDED)
                .count();
        long cancelledBookings = bookings.stream()
                .filter(b -> b.getStatus() == dangod.springboot.enums.BookingStatus.CANCELLED)
                .count();
        long waitingBookings = bookings.stream()
                .filter(b -> b.getStatus() == dangod.springboot.enums.BookingStatus.WAITING)
                .count();
        
        Map<String, Object> bookingStats = new HashMap<>();
        bookingStats.put("total", totalBookings);
        bookingStats.put("attended", attendedBookings);
        bookingStats.put("cancelled", cancelledBookings);
        bookingStats.put("waiting", waitingBookings);
        bookingStats.put("attendanceRate", totalBookings > 0 ? (double) attendedBookings / totalBookings : 0);
        stats.put("bookings", bookingStats);
        
        return stats;
    }

    @Override
    public void deleteEquipmentUsage(Long usageId) {
        EquipmentUsage usage = equipmentUsageRepository.findOne(usageId);
        if (usage == null) {
            throw new RuntimeException("器材使用记录不存在");
        }
        
        equipmentUsageRepository.delete(usageId);
    }
    
    // 辅助方法：预测会员续费
    private RenewalPredictionDto predictMemberRenewal(Member member) {
        RenewalPredictionDto prediction = new RenewalPredictionDto();
        prediction.setMemberId(member.getId());
        prediction.setMemberNumber(member.getMemberNumber());
        prediction.setMemberName(member.getName());
        prediction.setCardType(member.getCardType().toString());
        prediction.setExpiryDate(member.getExpiryDate().atStartOfDay());
        
        // 计算续费概率
        double renewalProbability = calculateRenewalProbability(member);
        prediction.setRenewalProbability(renewalProbability);
        
        // 确定风险等级
        String riskLevel = determineRiskLevel(renewalProbability);
        prediction.setRiskLevel(riskLevel);
        
        // 生成预测原因
        String reason = generatePredictionReason(member, renewalProbability);
        prediction.setPredictionReason(reason);
        
        return prediction;
    }
    
    // 辅助方法：计算续费概率
    private double calculateRenewalProbability(Member member) {
        double probability = 0.5; // 基础概率
        
        // 根据会员卡类型调整
        switch (member.getCardType()) {
            case DIAMOND:
                probability += 0.3;
                break;
            case GOLD:
                probability += 0.2;
                break;
            case SILVER:
                probability += 0.1;
                break;
            case BRONZE:
                probability -= 0.1;
                break;
        }
        
        // 根据会员状态调整
        switch (member.getStatus()) {
            case ACTIVE:
                probability += 0.2;
                break;
            case FROZEN:
                probability -= 0.1;
                break;
            case EXPIRED:
                probability -= 0.3;
                break;
        }
        
        // 根据会员使用情况调整
        if (member.getTotalSessions() > 0) {
            double usageRate = (double) member.getUsedSessions() / member.getTotalSessions();
            probability += usageRate * 0.2;
        }
        
        // 根据会员到期时间调整
        if (member.getExpiryDate() != null) {
            long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDateTime.now(), member.getExpiryDate().atStartOfDay());
            if (daysUntilExpiry < 0) {
                probability -= 0.3;
            } else if (daysUntilExpiry < 7) {
                probability -= 0.1;
            } else if (daysUntilExpiry < 30) {
                probability += 0.1;
            }
        }
        
        // 确保概率在0-1之间
        return Math.max(0, Math.min(1, probability));
    }
    
    // 辅助方法：确定风险等级
    private String determineRiskLevel(double renewalProbability) {
        if (renewalProbability >= 0.7) {
            return "低风险";
        } else if (renewalProbability >= 0.4) {
            return "中风险";
        } else {
            return "高风险";
        }
    }
    
    // 辅助方法：生成预测原因
    private String generatePredictionReason(Member member, double renewalProbability) {
        StringBuilder reason = new StringBuilder();
        
        if (member.getCardType() == dangod.springboot.enums.MemberCardType.DIAMOND || 
            member.getCardType() == dangod.springboot.enums.MemberCardType.GOLD) {
            reason.append("高等级会员，续费意愿较强。");
        }
        
        if (member.getStatus() == dangod.springboot.enums.MemberStatus.ACTIVE) {
            reason.append("会员状态正常，");
        } else if (member.getStatus() == dangod.springboot.enums.MemberStatus.FROZEN) {
            reason.append("会员已冻结，");
        } else if (member.getStatus() == dangod.springboot.enums.MemberStatus.EXPIRED) {
            reason.append("会员已过期，");
        }
        
        if (member.getTotalSessions() > 0) {
            double usageRate = (double) member.getUsedSessions() / member.getTotalSessions();
            if (usageRate > 0.7) {
                reason.append("使用频率高，");
            } else if (usageRate < 0.3) {
                reason.append("使用频率低，");
            }
        }
        
        if (renewalProbability >= 0.7) {
            reason.append("预计会续费。");
        } else if (renewalProbability >= 0.4) {
            reason.append("续费意愿不确定，需要关注。");
        } else {
            reason.append("续费可能性较低，需要采取措施。");
        }
        
        return reason.toString();
    }
    
    // 辅助方法：计算课程热度
    private CoursePopularityDto calculateCoursePopularity(Course course) {
        CoursePopularityDto popularity = new CoursePopularityDto();
        
        // 基本信息
        popularity.setCourseId(course.getId());
        popularity.setCourseName(course.getName());
        popularity.setCategory(course.getCategory());
        popularity.setTrainerName(course.getTrainer().getName());
        popularity.setStartTime(course.getStartTime());
        popularity.setMaxCapacity(course.getMaxCapacity());
        popularity.setCurrentBookings(course.getCurrentBookings());
        
        // 预约统计
        List<CourseBooking> bookings = courseBookingRepository.findByCourse_Id(course.getId());
        long totalBookings = bookings.size();
        long attendedBookings = bookings.stream()
                .filter(b -> b.getStatus() == dangod.springboot.enums.BookingStatus.ATTENDED)
                .count();
        long waitingBookings = bookings.stream()
                .filter(b -> b.getStatus() == dangod.springboot.enums.BookingStatus.WAITING)
                .count();
        
        popularity.setTotalBookings((int) totalBookings);
        popularity.setWaitingCount((int) waitingBookings);
        popularity.setAttendanceCount((int) attendedBookings);
        
        // 计算预约率
        double bookingRate = course.getMaxCapacity() > 0 ? 
                (double) course.getCurrentBookings() / course.getMaxCapacity() : 0;
        popularity.setBookingRate(bookingRate);
        
        // 计算出席率
        double attendanceRate = totalBookings > 0 ? (double) attendedBookings / totalBookings : 0;
        popularity.setAttendanceRate(attendanceRate);
        
        // 计算热度分数
        int popularityScore = calculatePopularityScore(bookingRate, attendanceRate, waitingBookings);
        popularity.setPopularityScore(popularityScore);
        
        return popularity;
    }
    
    // 辅助方法：计算热度分数
    private int calculatePopularityScore(double bookingRate, double attendanceRate, long waitingCount) {
        int score = 0;
        
        // 预约率权重40%
        score += bookingRate * 40;
        
        // 出席率权重30%
        score += attendanceRate * 30;
        
        // 排队人数权重30%
        score += Math.min(waitingCount * 5, 30);
        
        return score;
    }
}