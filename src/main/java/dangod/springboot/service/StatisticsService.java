package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberCardRepository memberCardRepository;

    @Autowired
    private RenewalPredictionRepository renewalPredictionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EquipmentUsageRepository equipmentUsageRepository;

    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyRenewalPrediction() {
        List<MemberCard> expiringCards = memberCardRepository.findExpiringCards(LocalDate.now().plusDays(30));
        
        for (MemberCard card : expiringCards) {
            double probability = calculateRenewalProbability(card);
            
            RenewalPrediction prediction = renewalPredictionRepository
                    .findByCardIdAndPredictionDate(card.getId(), LocalDate.now())
                    .orElse(new RenewalPrediction());
            
            prediction.setCardId(card.getId());
            prediction.setMemberId(card.getMemberId());
            prediction.setRenewalProbability(probability);
            prediction.setSuggestedAction(getSuggestedAction(probability, card));
            prediction.setPredictionDate(LocalDate.now());
            prediction.setStatus(probability > 0.7 ? 1 : (probability > 0.4 ? 2 : 3));
            prediction.setCreatedAt(LocalDateTime.now());
            prediction.setUpdatedAt(LocalDateTime.now());
            
            renewalPredictionRepository.save(prediction);
        }
    }

    private double calculateRenewalProbability(MemberCard card) {
        double score = 0.0;
        
        long monthsActive = ChronoUnit.MONTHS.between(card.getStartDate(), card.getEndDate());
        if (monthsActive >= 12) {
            score += 0.3;
        } else if (monthsActive >= 6) {
            score += 0.2;
        }
        
        List<Reservation> reservations = reservationRepository.findByMemberIdAndStatus(card.getMemberId(), 2);
        if (reservations.size() >= 20) {
            score += 0.3;
        } else if (reservations.size() >= 10) {
            score += 0.2;
        } else if (reservations.size() >= 5) {
            score += 0.1;
        }
        
        Optional<Member> member = memberRepository.findById(card.getMemberId());
        if (member.isPresent()) {
            if (member.get().getLevelId() >= 3) {
                score += 0.2;
            }
        }
        
        long daysUntilExpiry = ChronoUnit.DAYS.between(LocalDate.now(), card.getEndDate());
        if (daysUntilExpiry <= 7) {
            score += 0.2;
        }
        
        return Math.min(score, 1.0);
    }

    private String getSuggestedAction(double probability, MemberCard card) {
        if (probability >= 0.7) {
            return "高概率续费，可主动联系提供专属优惠";
        } else if (probability >= 0.4) {
            return "中等概率续费，建议发送续费提醒和优惠信息";
        } else {
            return "低概率续费，建议了解不续费原因，提供挽留方案";
        }
    }

    public List<RenewalPrediction> getRenewalPredictions(int status) {
        return renewalPredictionRepository.findByStatusOrderByRenewalProbabilityDesc(status);
    }

    public List<Map<String, Object>> getCourseHotRanking(Long gymId) {
        LocalDateTime startOfWeek = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        
        List<Course> courses = courseRepository.findByGymIdAndStartTimeBetween(gymId, startOfWeek, endOfWeek);
        
        return courses.stream().map(course -> {
            Map<String, Object> map = new HashMap<>();
            map.put("courseId", course.getId());
            map.put("courseName", course.getCourseName());
            map.put("coachId", course.getCoachId());
            map.put("maxCapacity", course.getMaxCapacity());
            map.put("currentCapacity", course.getCurrentCapacity());
            map.put("hotScore", calculateHotScore(course));
            map.put("startTime", course.getStartTime());
            return map;
        }).sorted((a, b) -> Double.compare((Double) b.get("hotScore"), (Double) a.get("hotScore")))
          .collect(Collectors.toList());
    }

    private double calculateHotScore(Course course) {
        if (course.getMaxCapacity() == 0) return 0;
        double occupancyRate = (double) course.getCurrentCapacity() / course.getMaxCapacity();
        double waitingScore = 0;
        if (course.getCurrentCapacity() >= course.getMaxCapacity()) {
            waitingScore = 0.3;
        }
        return occupancyRate * 0.7 + waitingScore;
    }

    public Map<String, Object> getEquipmentUsageStatistics(Long gymId) {
        List<Equipment> equipments = equipmentRepository.findByGymId(gymId);
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> usageList = new ArrayList<>();
        
        int totalMinutes = 0;
        int totalEquipment = equipments.size();
        int usedEquipment = 0;
        
        for (Equipment equipment : equipments) {
            List<EquipmentUsage> usages = equipmentUsageRepository
                    .findByEquipmentIdAndStartTimeBetween(equipment.getId(), startOfMonth, endOfMonth);
            
            int usageMinutes = usages.stream()
                    .mapToInt(u -> (int) ChronoUnit.MINUTES.between(u.getStartTime(), u.getEndTime()))
                    .sum();
            
            totalMinutes += usageMinutes;
            
            if (usageMinutes > 0) {
                usedEquipment++;
            }
            
            Map<String, Object> usageMap = new HashMap<>();
            usageMap.put("equipmentId", equipment.getId());
            usageMap.put("equipmentName", equipment.getName());
            usageMap.put("equipmentType", equipment.getType());
            usageMap.put("usageMinutes", usageMinutes);
            usageMap.put("usageHours", BigDecimal.valueOf(usageMinutes / 60.0)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue());
            usageMap.put("usageCount", usages.size());
            usageList.add(usageMap);
        }
        
        usageList.sort((a, b) -> Integer.compare((Integer) b.get("usageMinutes"), (Integer) a.get("usageMinutes")));
        
        result.put("totalEquipment", totalEquipment);
        result.put("usedEquipment", usedEquipment);
        result.put("totalUsageMinutes", totalMinutes);
        result.put("totalUsageHours", BigDecimal.valueOf(totalMinutes / 60.0)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        result.put("averageUsagePerEquipment", totalEquipment > 0 ? 
                BigDecimal.valueOf(totalMinutes / (double) totalEquipment)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);
        result.put("usageRate", totalEquipment > 0 ? 
                BigDecimal.valueOf((usedEquipment / (double) totalEquipment) * 100)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);
        result.put("equipmentDetails", usageList);
        
        return result;
    }

    public Map<String, Object> getOverallStatistics(Long gymId) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalMembers = memberRepository.count();
        long activeMembers = memberCardRepository.countActiveCards(LocalDate.now());
        long expiringThisWeek = memberCardRepository.countExpiringInDays(LocalDate.now().plusDays(7));
        
        stats.put("totalMembers", totalMembers);
        stats.put("activeMembers", activeMembers);
        stats.put("activeRate", BigDecimal.valueOf((activeMembers / (double) totalMembers) * 100)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        stats.put("expiringThisWeek", expiringThisWeek);
        
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Reservation> monthlyReservations = reservationRepository
                .findByReservationTimeBetween(startOfMonth, LocalDateTime.now());
        
        stats.put("monthlyReservations", monthlyReservations.size());
        stats.put("monthlyCheckIns", monthlyReservations.stream()
                .filter(r -> r.getStatus() == 2).count());
        
        List<Course> weeklyCourses = courseRepository.findByGymIdAndStartTimeBetween(
                gymId,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7));
        stats.put("weeklyCourses", weeklyCourses.size());
        stats.put("weeklyAvailableSlots", weeklyCourses.stream()
                .mapToInt(c -> c.getMaxCapacity() - c.getCurrentCapacity()).sum());
        
        return stats;
    }
}
