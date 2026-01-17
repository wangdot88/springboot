package dangod.springboot.service;

import dangod.springboot.entity.BodyMeasurement;
import dangod.springboot.entity.TrainingPlan;
import dangod.springboot.repository.BodyMeasurementRepository;
import dangod.springboot.repository.TrainingPlanRepository;
import dangod.springboot.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BodyMeasurementService {

    @Autowired
    private BodyMeasurementRepository bodyMeasurementRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    public BodyMeasurement createMeasurement(BodyMeasurement measurement) {
        measurement.setCreateTime(new Date());
        measurement.setUpdateTime(new Date());
        calculateBMI(measurement);
        checkAbnormalIndicators(measurement);
        return bodyMeasurementRepository.save(measurement);
    }

    public BodyMeasurement updateMeasurement(Long id, BodyMeasurement measurement) {
        BodyMeasurement existingMeasurement = bodyMeasurementRepository.findOne(id);
        if (existingMeasurement == null) {
            throw new BusinessException("体测记录不存在");
        }
        
        if (measurement.getHeight() != null) {
            existingMeasurement.setHeight(measurement.getHeight());
        }
        if (measurement.getWeight() != null) {
            existingMeasurement.setWeight(measurement.getWeight());
        }
        if (measurement.getBodyFat() != null) {
            existingMeasurement.setBodyFat(measurement.getBodyFat());
        }
        if (measurement.getMuscleMass() != null) {
            existingMeasurement.setMuscleMass(measurement.getMuscleMass());
        }
        if (measurement.getVisceralFat() != null) {
            existingMeasurement.setVisceralFat(measurement.getVisceralFat());
        }
        if (measurement.getBasalMetabolicRate() != null) {
            existingMeasurement.setBasalMetabolicRate(measurement.getBasalMetabolicRate());
        }
        if (measurement.getBloodPressureSystolic() != null) {
            existingMeasurement.setBloodPressureSystolic(measurement.getBloodPressureSystolic());
        }
        if (measurement.getBloodPressureDiastolic() != null) {
            existingMeasurement.setBloodPressureDiastolic(measurement.getBloodPressureDiastolic());
        }
        if (measurement.getHeartRate() != null) {
            existingMeasurement.setHeartRate(measurement.getHeartRate());
        }
        if (measurement.getFlexibility() != null) {
            existingMeasurement.setFlexibility(measurement.getFlexibility());
        }
        if (measurement.getEndurance() != null) {
            existingMeasurement.setEndurance(measurement.getEndurance());
        }
        if (measurement.getStrength() != null) {
            existingMeasurement.setStrength(measurement.getStrength());
        }
        
        calculateBMI(existingMeasurement);
        checkAbnormalIndicators(existingMeasurement);
        existingMeasurement.setUpdateTime(new Date());
        
        return bodyMeasurementRepository.save(existingMeasurement);
    }

    public BodyMeasurement getMeasurementById(Long id) {
        BodyMeasurement measurement = bodyMeasurementRepository.findOne(id);
        if (measurement == null) {
            throw new BusinessException("体测记录不存在");
        }
        return measurement;
    }

    public List<BodyMeasurement> getMeasurementsByMember(Long memberId) {
        return bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
    }

    public List<BodyMeasurement> getMeasurementsByDateRange(Long memberId, Date startDate, Date endDate) {
        return bodyMeasurementRepository.findByMemberIdAndDateRange(memberId, startDate, endDate);
    }

    public Map<String, List<Object>> getMeasurementChartData(Long memberId, String indicator) {
        List<BodyMeasurement> measurements = bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
        Collections.reverse(measurements);
        
        Map<String, List<Object>> chartData = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        
        for (BodyMeasurement measurement : measurements) {
            dates.add(measurement.getCreateTime().toString());
            Double value = getIndicatorValue(measurement, indicator);
            if (value != null) {
                values.add(value);
            }
        }
        
        chartData.put("dates", new ArrayList<>(dates));
        chartData.put("values", new ArrayList<>(values));
        
        return chartData;
    }

    private Double getIndicatorValue(BodyMeasurement measurement, String indicator) {
        switch (indicator.toLowerCase()) {
            case "weight":
                return measurement.getWeight();
            case "bodyfat":
                return measurement.getBodyFat();
            case "musclemass":
                return measurement.getMuscleMass();
            case "bmi":
                return measurement.getBmi();
            case "visceralfat":
                return measurement.getVisceralFat();
            case "basalmetabolicrate":
                return measurement.getBasalMetabolicRate();
            case "flexibility":
                return measurement.getFlexibility();
            case "endurance":
                return measurement.getEndurance();
            case "strength":
                return measurement.getStrength();
            case "heartrate":
                return measurement.getHeartRate() != null ? measurement.getHeartRate().doubleValue() : null;
            default:
                return null;
        }
    }

    private void calculateBMI(BodyMeasurement measurement) {
        if (measurement.getHeight() != null && measurement.getWeight() != null) {
            double heightInMeters = measurement.getHeight() / 100.0;
            double bmi = measurement.getWeight() / (heightInMeters * heightInMeters);
            measurement.setBmi(Math.round(bmi * 10.0) / 10.0);
        }
    }

    private void checkAbnormalIndicators(BodyMeasurement measurement) {
        List<String> abnormalIndicators = new ArrayList<>();
        boolean hasAbnormal = false;
        
        if (measurement.getBmi() != null) {
            if (measurement.getBmi() < 18.5 || measurement.getBmi() > 24.9) {
                abnormalIndicators.add("BMI异常");
                hasAbnormal = true;
            }
        }
        
        if (measurement.getBodyFat() != null) {
            if (measurement.getBodyFat() > 25.0) {
                abnormalIndicators.add("体脂率偏高");
                hasAbnormal = true;
            }
        }
        
        if (measurement.getBloodPressureSystolic() != null && measurement.getBloodPressureDiastolic() != null) {
            if (measurement.getBloodPressureSystolic() > 140 || measurement.getBloodPressureDiastolic() > 90) {
                abnormalIndicators.add("血压偏高");
                hasAbnormal = true;
            }
        }
        
        if (measurement.getHeartRate() != null) {
            if (measurement.getHeartRate() > 100 || measurement.getHeartRate() < 60) {
                abnormalIndicators.add("心率异常");
                hasAbnormal = true;
            }
        }
        
        if (measurement.getVisceralFat() != null) {
            if (measurement.getVisceralFat() > 15.0) {
                abnormalIndicators.add("内脏脂肪偏高");
                hasAbnormal = true;
            }
        }
        
        measurement.setHasAbnormalIndicator(hasAbnormal);
        measurement.setAbnormalIndicators(String.join(", ", abnormalIndicators));
        
        calculateHealthScore(measurement);
    }

    private void calculateHealthScore(BodyMeasurement measurement) {
        double score = 0.0;
        int count = 0;
        
        if (measurement.getFlexibility() != null) {
            score += measurement.getFlexibility();
            count++;
        }
        if (measurement.getEndurance() != null) {
            score += measurement.getEndurance();
            count++;
        }
        if (measurement.getStrength() != null) {
            score += measurement.getStrength();
            count++;
        }
        
        if (count > 0) {
            double avgScore = score / count;
            measurement.setHealthScore(String.format("%.1f", avgScore));
        }
    }

    public List<BodyMeasurement> getAbnormalMeasurements() {
        return bodyMeasurementRepository.findAbnormalMeasurements();
    }

    public List<BodyMeasurement> getAbnormalMeasurementsByMember(Long memberId) {
        return bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(memberId).stream()
                .filter(BodyMeasurement::getHasAbnormalIndicator)
                .collect(Collectors.toList());
    }

    @Transactional
    public TrainingPlan generateTrainingPlan(Long memberId, String goals) {
        List<BodyMeasurement> measurements = bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
        
        if (measurements.isEmpty()) {
            throw new BusinessException("暂无体测数据，无法生成训练计划");
        }
        
        BodyMeasurement latestMeasurement = measurements.get(0);
        
        TrainingPlan plan = new TrainingPlan();
        plan.setMemberId(memberId);
        plan.setName("个性化训练计划");
        plan.setGoals(goals);
        plan.setStatus(TrainingPlan.PlanStatus.ACTIVE);
        plan.setStartDate(new Date());
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 3);
        plan.setEndDate(calendar.getTime());
        
        String weeklySchedule = generateWeeklySchedule(latestMeasurement, goals);
        plan.setWeeklySchedule(weeklySchedule);
        plan.setDescription(generatePlanDescription(latestMeasurement));
        
        return trainingPlanRepository.save(plan);
    }

    private String generateWeeklySchedule(BodyMeasurement measurement, String goals) {
        StringBuilder schedule = new StringBuilder();
        
        if (measurement.getStrength() != null && measurement.getStrength() < 6.0) {
            schedule.append("周一：力量训练（重点：上肢）\n");
            schedule.append("周三：力量训练（重点：下肢）\n");
            schedule.append("周五：力量训练（全身）\n");
        }
        
        if (measurement.getEndurance() != null && measurement.getEndurance() < 6.0) {
            schedule.append("周二：有氧训练（跑步机30分钟）\n");
            schedule.append("周四：有氧训练（椭圆机30分钟）\n");
        }
        
        if (measurement.getFlexibility() != null && measurement.getFlexibility() < 6.0) {
            schedule.append("周六：瑜伽/拉伸课程\n");
        }
        
        schedule.append("周日：休息或轻度活动\n");
        
        return schedule.toString();
    }

    private String generatePlanDescription(BodyMeasurement measurement) {
        StringBuilder description = new StringBuilder();
        description.append("基于您的体测数据生成的个性化训练计划：\n\n");
        
        if (measurement.getBmi() != null) {
            description.append(String.format("BMI: %.1f\n", measurement.getBmi()));
        }
        if (measurement.getBodyFat() != null) {
            description.append(String.format("体脂率: %.1f%%\n", measurement.getBodyFat()));
        }
        if (measurement.getMuscleMass() != null) {
            description.append(String.format("肌肉量: %.1fkg\n", measurement.getMuscleMass()));
        }
        
        description.append("\n训练目标：\n");
        if (measurement.getStrength() != null && measurement.getStrength() < 6.0) {
            description.append("- 提升肌肉力量\n");
        }
        if (measurement.getEndurance() != null && measurement.getEndurance() < 6.0) {
            description.append("- 增强心肺功能\n");
        }
        if (measurement.getFlexibility() != null && measurement.getFlexibility() < 6.0) {
            description.append("- 改善身体柔韧性\n");
        }
        
        return description.toString();
    }

    public String exportHealthReport(Long memberId) {
        List<BodyMeasurement> measurements = bodyMeasurementRepository.findByMemberIdOrderByCreateTimeDesc(memberId);
        
        if (measurements.isEmpty()) {
            throw new BusinessException("暂无体测数据");
        }
        
        StringBuilder report = new StringBuilder();
        report.append("=== 健康报告 ===\n\n");
        report.append("生成时间：").append(new Date()).append("\n\n");
        
        BodyMeasurement latestMeasurement = measurements.get(0);
        report.append("最新体测数据：\n");
        report.append("------------------\n");
        
        if (latestMeasurement.getHeight() != null) {
            report.append(String.format("身高：%.1f cm\n", latestMeasurement.getHeight()));
        }
        if (latestMeasurement.getWeight() != null) {
            report.append(String.format("体重：%.1f kg\n", latestMeasurement.getWeight()));
        }
        if (latestMeasurement.getBmi() != null) {
            report.append(String.format("BMI：%.1f\n", latestMeasurement.getBmi()));
        }
        if (latestMeasurement.getBodyFat() != null) {
            report.append(String.format("体脂率：%.1f%%\n", latestMeasurement.getBodyFat()));
        }
        if (latestMeasurement.getMuscleMass() != null) {
            report.append(String.format("肌肉量：%.1f kg\n", latestMeasurement.getMuscleMass()));
        }
        if (latestMeasurement.getVisceralFat() != null) {
            report.append(String.format("内脏脂肪：%.1f\n", latestMeasurement.getVisceralFat()));
        }
        if (latestMeasurement.getBasalMetabolicRate() != null) {
            report.append(String.format("基础代谢率：%.0f kcal\n", latestMeasurement.getBasalMetabolicRate()));
        }
        if (latestMeasurement.getBloodPressureSystolic() != null && latestMeasurement.getBloodPressureDiastolic() != null) {
            report.append(String.format("血压：%d/%d mmHg\n", 
                latestMeasurement.getBloodPressureSystolic(), 
                latestMeasurement.getBloodPressureDiastolic()));
        }
        if (latestMeasurement.getHeartRate() != null) {
            report.append(String.format("静息心率：%d bpm\n", latestMeasurement.getHeartRate()));
        }
        
        report.append("\n体能评分：\n");
        report.append("------------------\n");
        if (latestMeasurement.getStrength() != null) {
            report.append(String.format("力量：%.1f/10\n", latestMeasurement.getStrength()));
        }
        if (latestMeasurement.getEndurance() != null) {
            report.append(String.format("耐力：%.1f/10\n", latestMeasurement.getEndurance()));
        }
        if (latestMeasurement.getFlexibility() != null) {
            report.append(String.format("柔韧性：%.1f/10\n", latestMeasurement.getFlexibility()));
        }
        if (latestMeasurement.getHealthScore() != null) {
            report.append(String.format("\n综合健康评分：%s/10\n", latestMeasurement.getHealthScore()));
        }
        
        if (latestMeasurement.getHasAbnormalIndicator()) {
            report.append("\n⚠️ 异常指标提醒：\n");
            report.append("------------------\n");
            report.append(latestMeasurement.getAbnormalIndicators()).append("\n");
        }
        
        report.append("\n=== 历史趋势 ===\n");
        report.append("------------------\n");
        report.append("共记录 ").append(measurements.size()).append(" 次体测\n");
        
        return report.toString();
    }

    public TrainingPlan getActiveTrainingPlan(Long memberId) {
        List<TrainingPlan> plans = trainingPlanRepository.findByMemberIdAndStatus(memberId, TrainingPlan.PlanStatus.ACTIVE);
        return plans.isEmpty() ? null : plans.get(0);
    }

    public List<TrainingPlan> getTrainingPlans(Long memberId) {
        return trainingPlanRepository.findByMemberId(memberId);
    }

    @Transactional
    public TrainingPlan updateTrainingPlan(Long id, TrainingPlan plan) {
        TrainingPlan existingPlan = trainingPlanRepository.findOne(id);
        if (existingPlan == null) {
            throw new BusinessException("训练计划不存在");
        }
        
        if (plan.getName() != null) {
            existingPlan.setName(plan.getName());
        }
        if (plan.getDescription() != null) {
            existingPlan.setDescription(plan.getDescription());
        }
        if (plan.getGoals() != null) {
            existingPlan.setGoals(plan.getGoals());
        }
        if (plan.getWeeklySchedule() != null) {
            existingPlan.setWeeklySchedule(plan.getWeeklySchedule());
        }
        if (plan.getStatus() != null) {
            existingPlan.setStatus(plan.getStatus());
        }
        if (plan.getStartDate() != null) {
            existingPlan.setStartDate(plan.getStartDate());
        }
        if (plan.getEndDate() != null) {
            existingPlan.setEndDate(plan.getEndDate());
        }
        
        existingPlan.setUpdateTime(new Date());
        return trainingPlanRepository.save(existingPlan);
    }

    @Transactional
    public void deleteMeasurement(Long id) {
        bodyMeasurementRepository.delete(id);
    }
}
