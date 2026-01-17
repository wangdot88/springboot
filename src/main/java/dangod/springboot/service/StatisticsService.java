package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseReservationRepository courseReservationRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EquipmentUsageRepository equipmentUsageRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private BodyTestRepository bodyTestRepository;

    @Transactional(readOnly = true)
    public List<Map<String, Object>> predictMemberRenewal() {
        List<Member> allMembers = memberRepository.findAll();
        List<Map<String, Object>> predictions = new ArrayList<>();

        for (Member member : allMembers) {
            Map<String, Object> prediction = new HashMap<>();
            prediction.put("memberId", member.getId());
            prediction.put("memberName", member.getName());
            prediction.put("phone", member.getPhone());
            prediction.put("cardLevel", member.getCardLevel().getName());
            prediction.put("expireDate", member.getExpireDate());

            int daysUntilExpire = (int) ChronoUnit.DAYS.between(LocalDateTime.now(), member.getExpireDate());
            prediction.put("daysUntilExpire", daysUntilExpire);

            double renewalProbability = calculateRenewalProbability(member);
            prediction.put("renewalProbability", BigDecimal.valueOf(renewalProbability)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue());

            String suggestion = getRenewalSuggestion(renewalProbability, daysUntilExpire);
            prediction.put("suggestion", suggestion);

            predictions.add(prediction);
        }

        return predictions.stream()
                .sorted((a, b) -> Double.compare((Double) b.get("renewalProbability"),
                        (Double) a.get("renewalProbability")))
                .collect(Collectors.toList());
    }

    private double calculateRenewalProbability(Member member) {
        double baseScore = 50.0;

        int daysUntilExpire = (int) ChronoUnit.DAYS.between(LocalDateTime.now(), member.getExpireDate());
        if (daysUntilExpire <= 7) {
            baseScore += 15;
        } else if (daysUntilExpire <= 30) {
            baseScore += 10;
        } else if (daysUntilExpire <= 60) {
            baseScore += 5;
        }

        int courseCount = courseReservationRepository.countByMemberAndAttendedTrue(member);
        if (courseCount >= 10) {
            baseScore += 20;
        } else if (courseCount >= 5) {
            baseScore += 10;
        } else if (courseCount >= 2) {
            baseScore += 5;
        }

        List<BodyTest> bodyTests = bodyTestRepository.findByMemberOrderByTestDateDesc(member, PageRequest.of(0, 5));
        if (!bodyTests.isEmpty()) {
            baseScore += 10;
            
            if (bodyTests.size() >= 3) {
                BodyTest latest = bodyTests.get(0);
                BodyTest oldest = bodyTests.get(bodyTests.size() - 1);
                
                if (latest.getBmi() != null && oldest.getBmi() != null) {
                    double bmiChange = oldest.getBmi() - latest.getBmi();
                    if (bmiChange > 1) {
                        baseScore += 10;
                    } else if (bmiChange > 0.5) {
                        baseScore += 5;
                    }
                }
            }
        }

        if (member.getCardLevel().getLevel() >= 3) {
            baseScore += 10;
        }

        long usageCount = equipmentUsageRepository.countByMember(member);
        if (usageCount >= 20) {
            baseScore += 15;
        } else if (usageCount >= 10) {
            baseScore += 8;
        } else if (usageCount >= 5) {
            baseScore += 3;
        }

        LocalDateTime lastActive = getLastActiveDate(member);
        if (lastActive != null) {
            long daysSinceActive = ChronoUnit.DAYS.between(lastActive, LocalDateTime.now());
            if (daysSinceActive <= 7) {
                baseScore += 10;
            } else if (daysSinceActive <= 14) {
                baseScore += 5;
            } else if (daysSinceActive > 30) {
                baseScore -= 15;
            } else if (daysSinceActive > 60) {
                baseScore -= 25;
            }
        }

        return Math.min(100, Math.max(0, baseScore));
    }

    private LocalDateTime getLastActiveDate(Member member) {
        List<EquipmentUsage> usages = equipmentUsageRepository
                .findByMemberOrderByUsageTimeDesc(member, PageRequest.of(0, 1));
        if (!usages.isEmpty()) {
            return usages.get(0).getUsageTime();
        }

        List<CourseReservation> reservations = courseReservationRepository
                .findByMemberOrderByReservationTimeDesc(member, PageRequest.of(0, 1));
        if (!reservations.isEmpty()) {
            return reservations.get(0).getReservationTime();
        }

        List<BodyTest> bodyTests = bodyTestRepository
                .findByMemberOrderByTestDateDesc(member, PageRequest.of(0, 1));
        if (!bodyTests.isEmpty()) {
            return bodyTests.get(0).getTestDate();
        }

        return null;
    }

    private String getRenewalSuggestion(double probability, int daysUntilExpire) {
        if (probability >= 80) {
            return "高续费意向，建议主动联系推荐升级会员";
        } else if (probability >= 60) {
            return "中等续费意向，发送续费优惠提醒";
        } else if (probability >= 40) {
            return "一般续费意向，提供限时折扣";
        } else {
            if (daysUntilExpire <= 7) {
                return "低续费意向，即将到期，建议回访了解原因";
            } else {
                return "低续费意向，关注会员活跃度，提供个性化服务";
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getCourseHotRank(int days, int limit) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        List<Object[]> results = courseReservationRepository.countReservationsByCourse(startDate, LocalDateTime.now());
        
        return results.stream()
                .limit(limit)
                .map(result -> {
                    Map<String, Object> courseData = new HashMap<>();
                    Course course = (Course) result[0];
                    Long count = (Long) result[1];
                    
                    courseData.put("courseId", course.getId());
                    courseData.put("courseName", course.getName());
                    courseData.put("courseTypeName", course.getCourseType().getName());
                    courseData.put("coachName", course.getCoach().getName());
                    courseData.put("reservationCount", count);
                    courseData.put("maxCapacity", course.getMaxCapacity());
                    courseData.put("popularityRate", BigDecimal.valueOf(count * 100.0 / course.getMaxCapacity())
                            .setScale(2, RoundingMode.HALF_UP).doubleValue());
                    
                    return courseData;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("reservationCount"), (Long) a.get("reservationCount")))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getEquipmentUsageRate(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        
        List<Equipment> allEquipments = equipmentRepository.findAll();
        List<Map<String, Object>> usageStats = new ArrayList<>();

        long totalMinutesInPeriod = days * 24 * 60;

        for (Equipment equipment : allEquipments) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("equipmentId", equipment.getId());
            stats.put("equipmentName", equipment.getName());
            stats.put("equipmentType", equipment.getType());
            stats.put("storeName", equipment.getStore().getName());

            List<EquipmentUsage> usages = equipmentUsageRepository
                    .findByEquipmentAndUsageTimeBetween(equipment, startDate, LocalDateTime.now());
            
            long totalUsageMinutes = usages.stream()
                    .mapToLong(usage -> {
                        if (usage.getEndTime() != null) {
                            return ChronoUnit.MINUTES.between(usage.getUsageTime(), usage.getEndTime());
                        }
                        return 0;
                    })
                    .sum();

            stats.put("totalUsageMinutes", totalUsageMinutes);
            stats.put("usageCount", usages.size());
            
            double usageRate = 0;
            if (totalMinutesInPeriod > 0) {
                usageRate = BigDecimal.valueOf(totalUsageMinutes * 100.0 / totalMinutesInPeriod)
                        .setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            stats.put("usageRate", usageRate);

            String usageLevel = getUsageLevel(usageRate);
            stats.put("usageLevel", usageLevel);

            usageStats.add(stats);
        }

        return usageStats.stream()
                .sorted((a, b) -> Double.compare((Double) b.get("usageRate"), (Double) a.get("usageRate")))
                .collect(Collectors.toList());
    }

    private String getUsageLevel(double usageRate) {
        if (usageRate >= 30) {
            return "高频使用";
        } else if (usageRate >= 15) {
            return "正常使用";
        } else if (usageRate >= 5) {
            return "低频使用";
        } else {
            return "极少使用";
        }
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        long totalMembers = memberRepository.count();
        summary.put("totalMembers", totalMembers);

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long newMembers = memberRepository.countByCreateTimeAfter(thirtyDaysAgo);
        summary.put("newMembers30Days", newMembers);

        LocalDateTime sevenDaysLater = LocalDateTime.now().plusDays(7);
        long expiringSoon = memberRepository.countByExpireDateBeforeAndStatus(sevenDaysLater, Member.Status.ACTIVE);
        summary.put("expiringSoonMembers", expiringSoon);

        long totalCourses = courseRepository.count();
        summary.put("totalCourses", totalCourses);

        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        long todayReservations = courseReservationRepository.countByReservationTimeAfter(todayStart);
        summary.put("todayReservations", todayReservations);

        long completedReservations = courseReservationRepository.countByAttendedTrueAndReservationTimeAfter(thirtyDaysAgo);
        summary.put("completedReservations30Days", completedReservations);

        List<Map<String, Object>> hotCourses = getCourseHotRank(7, 5);
        summary.put("hotCourses", hotCourses);

        List<Map<String, Object>> memberPredictions = predictMemberRenewal().stream()
                .limit(10)
                .collect(Collectors.toList());
        summary.put("memberRenewalPredictions", memberPredictions);

        return summary;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getMemberActivityTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = LocalDateTime.now().minusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime nextDay = date.plusDays(1);
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", date.toLocalDate().toString());
            
            long reservationCount = courseReservationRepository.countByReservationTimeBetween(date, nextDay);
            dayData.put("reservationCount", reservationCount);
            
            long usageCount = equipmentUsageRepository.countByUsageTimeBetween(date, nextDay);
            dayData.put("usageCount", usageCount);
            
            long bodyTestCount = bodyTestRepository.countByTestDateBetween(date, nextDay);
            dayData.put("bodyTestCount", bodyTestCount);
            
            trend.add(dayData);
        }
        
        return trend;
    }
}
