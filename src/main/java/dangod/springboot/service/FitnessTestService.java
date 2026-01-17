package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FitnessTestService {
    private static final Logger logger = LoggerFactory.getLogger(FitnessTestService.class);

    @Autowired
    private FitnessTestRecordRepository fitnessTestRecordRepository;

    @Autowired
    private FitnessTestDetailRepository fitnessTestDetailRepository;

    @Autowired
    private FitnessMetricRepository fitnessMetricRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private TrainingPlanDetailRepository trainingPlanDetailRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Transactional
    public FitnessTestRecord createFitnessTest(Long memberId, Long branchId, Long testerId, Map<String, BigDecimal> metrics) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }

        FitnessTestRecord record = new FitnessTestRecord();
        record.setMemberId(memberId);
        record.setBranchId(branchId);
        record.setTesterId(testerId);
        record = fitnessTestRecordRepository.save(record);

        List<FitnessMetric> allMetrics = fitnessMetricRepository.findAll();
        List<FitnessTestDetail> details = new ArrayList<>();
        List<FitnessTestDetail> abnormalDetails = new ArrayList<>();

        for (FitnessMetric metric : allMetrics) {
            if (metrics.containsKey(metric.getMetricCode())) {
                FitnessTestDetail detail = new FitnessTestDetail();
                detail.setRecordId(record.getId());
                detail.setMetricCode(metric.getMetricCode());
                detail.setMetricValue(metrics.get(metric.getMetricCode()));

                boolean isAbnormal = checkAbnormal(metric, metrics.get(metric.getMetricCode()), memberOpt.get().getGender());
                detail.setIsAbnormal(isAbnormal ? 1 : 0);

                if (isAbnormal) {
                    detail.setSuggestion(generateSuggestion(metric, metrics.get(metric.getMetricCode()), memberOpt.get().getGender()));
                    abnormalDetails.add(detail);
                }

                details.add(detail);
            }
        }

        fitnessTestDetailRepository.saveAll(details);

        if (!abnormalDetails.isEmpty()) {
            sendMetricAbnormalNotification(memberId, abnormalDetails);
        }

        logger.info("创建体测记录成功，会员ID: {}, 记录ID: {}", memberId, record.getId());
        return record;
    }

    private boolean checkAbnormal(FitnessMetric metric, BigDecimal value, Integer gender) {
        BigDecimal min, max;
        if (gender != null && gender == 1) {
            min = metric.getNormalMinMale();
            max = metric.getNormalMaxMale();
        } else {
            min = metric.getNormalMinFemale();
            max = metric.getNormalMaxFemale();
        }

        if (min == null || max == null) {
            return false;
        }

        return value.compareTo(min) < 0 || value.compareTo(max) > 0;
    }

    private String generateSuggestion(FitnessMetric metric, BigDecimal value, Integer gender) {
        BigDecimal min, max;
        if (gender != null && gender == 1) {
            min = metric.getNormalMinMale();
            max = metric.getNormalMaxMale();
        } else {
            min = metric.getNormalMinFemale();
            max = metric.getNormalMaxFemale();
        }

        if (value.compareTo(min) < 0) {
            return metric.getMetricName() + "低于正常范围(" + min + "-" + max + metric.getUnit() + ")，建议增加营养摄入，进行力量训练。";
        } else if (value.compareTo(max) > 0) {
            return metric.getMetricName() + "高于正常范围(" + min + "-" + max + metric.getUnit() + ")，建议控制饮食，增加有氧运动。";
        }
        return "";
    }

    @Async("asyncExecutor")
    public void sendMetricAbnormalNotification(Long memberId, List<FitnessTestDetail> abnormalDetails) {
        StringBuilder content = new StringBuilder("您的体测指标存在异常：\n");
        for (FitnessTestDetail detail : abnormalDetails) {
            Optional<FitnessMetric> metricOpt = fitnessMetricRepository.findByMetricCode(detail.getMetricCode());
            metricOpt.ifPresent(metric -> {
                content.append(metric.getMetricName()).append(": ")
                        .append(detail.getMetricValue()).append(metric.getUnit())
                        .append(" - ").append(detail.getSuggestion()).append("\n");
            });
        }

        Notification notification = new Notification();
        notification.setMemberId(memberId);
        notification.setTitle("体测指标异常提醒");
        notification.setContent(content.toString());
        notification.setType("METRIC_ABNORMAL");
        notification.setIsRead(0);
        notificationRepository.save(notification);
    }

    public List<FitnessTestRecord> getMemberTestHistory(Long memberId) {
        return fitnessTestRecordRepository.findByMemberIdOrderByTestDateDesc(memberId);
    }

    public Map<String, List<Map<String, Object>>> getMetricTrendData(Long memberId, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        Date afterDate = calendar.getTime();

        List<FitnessTestRecord> records = fitnessTestRecordRepository.findByMemberIdAndTestDateAfter(memberId, afterDate);
        if (records.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> recordIds = records.stream().map(FitnessTestRecord::getId).collect(Collectors.toList());
        List<FitnessTestDetail> details = fitnessTestDetailRepository.findByRecordIds(recordIds);

        Map<Long, FitnessTestRecord> recordMap = records.stream()
                .collect(Collectors.toMap(FitnessTestRecord::getId, r -> r));

        Map<String, List<Map<String, Object>>> trendData = new HashMap<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (FitnessTestDetail detail : details) {
            FitnessTestRecord record = recordMap.get(detail.getRecordId());
            if (record != null) {
                trendData.computeIfAbsent(detail.getMetricCode(), k -> new ArrayList<>());

                Map<String, Object> point = new HashMap<>();
                point.put("date", sdf.format(record.getTestDate()));
                point.put("value", detail.getMetricValue());
                trendData.get(detail.getMetricCode()).add(point);
            }
        }

        for (List<Map<String, Object>> points : trendData.values()) {
            points.sort(Comparator.comparing(p -> (String) p.get("date")));
        }

        return trendData;
    }

    @Transactional
    public TrainingPlan generateTrainingPlan(Long memberId, String planName, Date startDate, Date endDate) {
        FitnessTestRecord latestTest = fitnessTestRecordRepository.findTopByMemberIdOrderByTestDateDesc(memberId);
        if (latestTest == null) {
            throw new RuntimeException("无体测数据，无法生成训练计划");
        }

        List<FitnessTestDetail> details = fitnessTestDetailRepository.findByRecordId(latestTest.getId());

        TrainingPlan plan = new TrainingPlan();
        plan.setMemberId(memberId);
        plan.setPlanName(planName);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan.setStatus(1);
        plan.setDescription("根据体测数据自动生成的训练计划");
        plan = trainingPlanRepository.save(plan);

        List<TrainingPlanDetail> planDetails = generateTrainingContent(details);
        for (TrainingPlanDetail detail : planDetails) {
            detail.setPlanId(plan.getId());
        }

        trainingPlanDetailRepository.saveAll(planDetails);

        logger.info("生成训练计划成功，会员ID: {}, 计划ID: {}", memberId, plan.getId());
        return plan;
    }

    private List<TrainingPlanDetail> generateTrainingContent(List<FitnessTestDetail> details) {
        List<TrainingPlanDetail> planDetails = new ArrayList<>();

        Map<String, FitnessTestDetail> metricMap = details.stream()
                .collect(Collectors.toMap(FitnessTestDetail::getMetricCode, d -> d));

        FitnessTestDetail bmiDetail = metricMap.get("BMI");
        FitnessTestDetail fatDetail = metricMap.get("BODY_FAT");
        FitnessTestDetail muscleDetail = metricMap.get("MUSCLE_MASS");

        int focusType = 0;
        if (bmiDetail != null && bmiDetail.getMetricValue().compareTo(new BigDecimal("24")) > 0) {
            focusType = 1;
        } else if (bmiDetail != null && bmiDetail.getMetricValue().compareTo(new BigDecimal("18.5")) < 0) {
            focusType = 2;
        } else {
            focusType = 3;
        }

        if (focusType == 1) {
            planDetails.addAll(createFatLossPlan());
        } else if (focusType == 2) {
            planDetails.addAll(createMuscleGainPlan());
        } else {
            planDetails.addAll(createMaintenancePlan());
        }

        return planDetails;
    }

    private List<TrainingPlanDetail> createFatLossPlan() {
        List<TrainingPlanDetail> details = new ArrayList<>();

        String[] cardioExercises = {"慢跑", "游泳", "跳绳", "椭圆机", "动感单车"};
        String[] strengthExercises = {"深蹲", "硬拉", "俯卧撑", "引体向上", "平板支撑"};

        for (int day = 1; day <= 7; day++) {
            if (day % 2 == 1) {
                int index = (day - 1) / 2;
                TrainingPlanDetail detail = new TrainingPlanDetail();
                detail.setDayOfWeek(day);
                detail.setExerciseName(cardioExercises[index % cardioExercises.length]);
                detail.setSets(1);
                detail.setReps("45分钟");
                details.add(detail);
            } else {
                int index = day / 2 - 1;
                TrainingPlanDetail detail = new TrainingPlanDetail();
                detail.setDayOfWeek(day);
                detail.setExerciseName(strengthExercises[index % strengthExercises.length]);
                detail.setSets(4);
                detail.setReps("12-15次");
                details.add(detail);
            }
        }

        return details;
    }

    private List<TrainingPlanDetail> createMuscleGainPlan() {
        List<TrainingPlanDetail> details = new ArrayList<>();

        String[] muscleExercises = {"杠铃卧推(胸)", "引体向上(背)", "杠铃深蹲(腿)", "肩推(肩)", "臂弯举(臂)", "硬拉(全身)"};

        for (int day = 1; day <= 6; day++) {
            TrainingPlanDetail detail = new TrainingPlanDetail();
            detail.setDayOfWeek(day);
            detail.setExerciseName(muscleExercises[(day - 1) % muscleExercises.length]);
            detail.setSets(4);
            detail.setReps("8-12次");
            detail.setWeight(new BigDecimal("60"));
            details.add(detail);
        }

        TrainingPlanDetail restDay = new TrainingPlanDetail();
        restDay.setDayOfWeek(7);
        restDay.setExerciseName("休息或轻度拉伸");
        restDay.setSets(1);
        restDay.setReps("30分钟");
        details.add(restDay);

        return details;
    }

    private List<TrainingPlanDetail> createMaintenancePlan() {
        List<TrainingPlanDetail> details = new ArrayList<>();

        String[] exercises = {"全身力量训练", "有氧运动", "核心训练", "瑜伽或普拉提", "自由活动", "球类运动", "休息"};

        for (int day = 1; day <= 7; day++) {
            TrainingPlanDetail detail = new TrainingPlanDetail();
            detail.setDayOfWeek(day);
            detail.setExerciseName(exercises[day - 1]);
            if (day == 7) {
                detail.setSets(1);
                detail.setReps("全天");
            } else {
                detail.setSets(3);
                detail.setReps("30-45分钟");
            }
            details.add(detail);
        }

        return details;
    }

    public List<TrainingPlan> getMemberPlans(Long memberId) {
        return trainingPlanRepository.findByMemberIdOrderByStartDateDesc(memberId);
    }

    public List<TrainingPlanDetail> getPlanDetails(Long planId) {
        return trainingPlanDetailRepository.findByPlanIdOrderByDayOfWeekAsc(planId);
    }

    public Map<String, Object> getHealthReportData(Long memberId) {
        Map<String, Object> reportData = new HashMap<>();

        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        Member member = memberOpt.get();

        FitnessTestRecord latestTest = fitnessTestRecordRepository.findTopByMemberIdOrderByTestDateDesc(memberId);
        if (latestTest == null) {
            throw new RuntimeException("暂无体测数据");
        }

        List<FitnessTestDetail> latestDetails = fitnessTestDetailRepository.findByRecordId(latestTest.getId());
        List<TrainingPlan> activePlans = trainingPlanRepository.findCurrentPlansByMemberId(memberId, new Date());

        reportData.put("memberName", member.getName());
        reportData.put("memberNo", member.getMemberNo());
        reportData.put("testDate", latestTest.getTestDate());
        reportData.put("metrics", latestDetails);

        long abnormalCount = latestDetails.stream().filter(d -> d.getIsAbnormal() == 1).count();
        reportData.put("abnormalCount", abnormalCount);
        reportData.put("totalMetrics", latestDetails.size());

        if (!activePlans.isEmpty()) {
            reportData.put("currentPlan", activePlans.get(0));
            List<TrainingPlanDetail> planDetails = trainingPlanDetailRepository.findByPlanIdOrderByDayOfWeekAsc(activePlans.get(0).getId());
            reportData.put("planDetails", planDetails);
        }

        reportData.put("healthScore", calculateHealthScore(latestDetails));

        return reportData;
    }

    private int calculateHealthScore(List<FitnessTestDetail> details) {
        if (details.isEmpty()) return 100;

        long abnormalCount = details.stream().filter(d -> d.getIsAbnormal() == 1).count();
        int total = details.size();
        int score = 100 - (int) (abnormalCount * 100.0 / total);

        return Math.max(0, Math.min(100, score));
    }

    @Transactional
    public void deleteTestRecord(Long recordId) {
        fitnessTestDetailRepository.deleteByRecordId(recordId);
        fitnessTestRecordRepository.deleteById(recordId);
        logger.info("删除体测记录成功，记录ID: {}", recordId);
    }
}