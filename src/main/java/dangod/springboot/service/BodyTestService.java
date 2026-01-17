package dangod.springboot.service;

import dangod.springboot.common.BusinessException;
import dangod.springboot.entity.BodyTest;
import dangod.springboot.entity.Member;
import dangod.springboot.repository.BodyTestRepository;
import dangod.springboot.repository.MemberRepository;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class BodyTestService {
    private static final Logger logger = LoggerFactory.getLogger(BodyTestService.class);
    
    @Autowired
    private BodyTestRepository bodyTestRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private TrainingPlanService trainingPlanService;
    
    @Transactional
    public BodyTest createBodyTest(BodyTest bodyTest) {
        if (bodyTest.getMember() == null || bodyTest.getMember().getId() == null) {
            throw new BusinessException("会员信息不能为空");
        }
        
        Member member = memberRepository.findById(bodyTest.getMember().getId()).orElse(null);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (bodyTest.getTestDate() == null) {
            bodyTest.setTestDate(LocalDateTime.now());
        }
        
        bodyTest.setStatus(1);
        bodyTest.setCreateTime(LocalDateTime.now());
        
        BodyTest saved = bodyTestRepository.save(bodyTest);
        
        checkAbnormalIndicators(saved);
        
        List<BodyTest> historyTests = bodyTestRepository.findByMemberId(member.getId());
        if (historyTests.size() >= 2) {
            generateTrainingPlan(saved, member);
        }
        
        logger.info("创建体测记录成功: 会员{}，日期: {}", member.getId(), bodyTest.getTestDate());
        return saved;
    }
    
    @Transactional
    public BodyTest updateBodyTest(BodyTest bodyTest) {
        BodyTest existing = bodyTestRepository.findById(bodyTest.getId()).orElse(null);
        if (existing == null) {
            throw new BusinessException("体测记录不存在");
        }
        
        if (bodyTest.getHeight() != null) {
            existing.setHeight(bodyTest.getHeight());
        }
        if (bodyTest.getWeight() != null) {
            existing.setWeight(bodyTest.getWeight());
        }
        if (bodyTest.getBmi() != null) {
            existing.setBmi(bodyTest.getBmi());
        }
        if (bodyTest.getBodyFat() != null) {
            existing.setBodyFat(bodyTest.getBodyFat());
        }
        if (bodyTest.getMuscleMass() != null) {
            existing.setMuscleMass(bodyTest.getMuscleMass());
        }
        if (bodyTest.getBoneMass() != null) {
            existing.setBoneMass(bodyTest.getBoneMass());
        }
        if (bodyTest.getVisceralFat() != null) {
            existing.setVisceralFat(bodyTest.getVisceralFat());
        }
        if (bodyTest.getBodyWater() != null) {
            existing.setBodyWater(bodyTest.getBodyWater());
        }
        if (bodyTest.getMetabolism() != null) {
            existing.setMetabolism(bodyTest.getMetabolism());
        }
        if (bodyTest.getProtein() != null) {
            existing.setProtein(bodyTest.getProtein());
        }
        if (bodyTest.getFatFreeMass() != null) {
            existing.setFatFreeMass(bodyTest.getFatFreeMass());
        }
        if (bodyTest.getBasalMetabolism() != null) {
            existing.setBasalMetabolism(bodyTest.getBasalMetabolism());
        }
        if (bodyTest.getSbp() != null) {
            existing.setSbp(bodyTest.getSbp());
        }
        if (bodyTest.getDbp() != null) {
            existing.setDbp(bodyTest.getDbp());
        }
        if (bodyTest.getHeartRate() != null) {
            existing.setHeartRate(bodyTest.getHeartRate());
        }
        if (bodyTest.getLeftArmFat() != null) {
            existing.setLeftArmFat(bodyTest.getLeftArmFat());
        }
        if (bodyTest.getRightArmFat() != null) {
            existing.setRightArmFat(bodyTest.getRightArmFat());
        }
        if (bodyTest.getLeftLegFat() != null) {
            existing.setLeftLegFat(bodyTest.getLeftLegFat());
        }
        if (bodyTest.getRightLegFat() != null) {
            existing.setRightLegFat(bodyTest.getRightLegFat());
        }
        if (bodyTest.getTrunkFat() != null) {
            existing.setTrunkFat(bodyTest.getTrunkFat());
        }
        if (bodyTest.getLeftArmMuscle() != null) {
            existing.setLeftArmMuscle(bodyTest.getLeftArmMuscle());
        }
        if (bodyTest.getRightArmMuscle() != null) {
            existing.setRightArmMuscle(bodyTest.getRightArmMuscle());
        }
        if (bodyTest.getLeftLegMuscle() != null) {
            existing.setLeftLegMuscle(bodyTest.getLeftLegMuscle());
        }
        if (bodyTest.getRightLegMuscle() != null) {
            existing.setRightLegMuscle(bodyTest.getRightLegMuscle());
        }
        if (bodyTest.getTrunkMuscle() != null) {
            existing.setTrunkMuscle(bodyTest.getTrunkMuscle());
        }
        if (bodyTest.getBodyAge() != null) {
            existing.setBodyAge(bodyTest.getBodyAge());
        }
        if (bodyTest.getFatMass() != null) {
            existing.setFatMass(bodyTest.getFatMass());
        }
        if (bodyTest.getRemark() != null) {
            existing.setRemark(bodyTest.getRemark());
        }
        if (bodyTest.getStatus() != null) {
            existing.setStatus(bodyTest.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        
        BodyTest updated = bodyTestRepository.save(existing);
        checkAbnormalIndicators(updated);
        
        logger.info("更新体测记录成功: {}", bodyTest.getId());
        return updated;
    }
    
    @Transactional
    public void deleteBodyTest(Long testId) {
        BodyTest bodyTest = bodyTestRepository.findById(testId).orElse(null);
        if (bodyTest == null) {
            throw new BusinessException("体测记录不存在");
        }
        
        bodyTestRepository.delete(bodyTest);
        logger.info("删除体测记录成功: {}", testId);
    }
    
    public BodyTest getBodyTestById(Long testId) {
        return bodyTestRepository.findById(testId).orElse(null);
    }
    
    public List<BodyTest> getBodyTestsByMember(Long memberId) {
        return bodyTestRepository.findAllByMemberIdOrderByTestDateDesc(memberId);
    }
    
    public BodyTest getLatestBodyTest(Long memberId) {
        List<BodyTest> tests = bodyTestRepository.findLatestByMemberId(memberId, 1);
        return tests.isEmpty() ? null : tests.get(0);
    }
    
    public byte[] generateBodyTestChart(Long memberId, String indicator) {
        List<BodyTest> tests = bodyTestRepository.findAllByMemberIdOrderByTestDateDesc(memberId);
        if (tests.isEmpty()) {
            throw new BusinessException("该会员暂无体测数据");
        }
        
        Collections.reverse(tests);
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (BodyTest test : tests) {
            String dateStr = test.getTestDate().format(formatter);
            Double value = getIndicatorValue(test, indicator);
            if (value != null) {
                dataset.addValue(value, indicator, dateStr);
            }
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            getIndicatorName(indicator) + "变化趋势",
            "日期",
            getIndicatorName(indicator),
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 400);
            return outputStream.toByteArray();
        } catch (Exception e) {
            logger.error("生成图表失败", e);
            throw new BusinessException("生成图表失败: " + e.getMessage());
        }
    }
    
    private Double getIndicatorValue(BodyTest test, String indicator) {
        switch (indicator) {
            case "weight":
                return test.getWeight();
            case "bmi":
                return test.getBmi();
            case "bodyFat":
                return test.getBodyFat();
            case "muscleMass":
                return test.getMuscleMass();
            case "visceralFat":
                return test.getVisceralFat();
            case "bodyWater":
                return test.getBodyWater();
            case "metabolism":
                return test.getMetabolism() != null ? test.getMetabolism().doubleValue() : null;
            default:
                return test.getWeight();
        }
    }
    
    private String getIndicatorName(String indicator) {
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("weight", "体重(kg)");
        nameMap.put("bmi", "BMI指数");
        nameMap.put("bodyFat", "体脂率(%)");
        nameMap.put("muscleMass", "肌肉量(kg)");
        nameMap.put("visceralFat", "内脏脂肪等级");
        nameMap.put("bodyWater", "水分(%)");
        nameMap.put("metabolism", "基础代谢(kcal)");
        return nameMap.getOrDefault(indicator, indicator);
    }
    
    private void checkAbnormalIndicators(BodyTest test) {
        List<String> abnormalIndicators = new ArrayList<>();
        
        if (test.getBmi() != null) {
            if (test.getBmi().doubleValue() < 18.5) {
                abnormalIndicators.add("BMI指数偏低(" + test.getBmi() + ")");
            } else if (test.getBmi().doubleValue() > 28) {
                abnormalIndicators.add("BMI指数偏高(" + test.getBmi() + ")");
            }
        }
        
        if (test.getBodyFat() != null && test.getBodyFat().doubleValue() > 30) {
            abnormalIndicators.add("体脂率偏高(" + test.getBodyFat() + "%)");
        }
        
        if (test.getVisceralFat() != null && test.getVisceralFat().intValue() > 15) {
            abnormalIndicators.add("内脏脂肪等级偏高(" + test.getVisceralFat() + ")");
        }
        
        if (test.getSbp() != null && test.getSbp().intValue() > 140) {
            abnormalIndicators.add("收缩压偏高(" + test.getSbp() + ")");
        }
        
        if (test.getDbp() != null && test.getDbp().intValue() > 90) {
            abnormalIndicators.add("舒张压偏高(" + test.getDbp() + ")");
        }
        
        if (test.getHeartRate() != null) {
            if (test.getHeartRate().intValue() < 60) {
                abnormalIndicators.add("心率偏低(" + test.getHeartRate() + ")");
            } else if (test.getHeartRate().intValue() > 100) {
                abnormalIndicators.add("心率偏高(" + test.getHeartRate() + ")");
            }
        }
        
        if (!abnormalIndicators.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("您的体测结果显示以下指标异常：");
            for (String indicator : abnormalIndicators) {
                message.append("\n- " + indicator);
            }
            message.append("\n\n建议您咨询教练制定针对性训练计划。");
            
            Member member = test.getMember();
            if (member.getEmail() != null && !member.getEmail().isEmpty()) {
                try {
                    emailService.sendSimpleEmail(member.getEmail(), "体测指标异常提醒", message.toString());
                    logger.info("发送体测异常提醒邮件给会员{}: {}", member.getId(), member.getEmail());
                } catch (Exception e) {
                    logger.error("发送体测异常提醒邮件失败: {}", e.getMessage());
                }
            }
            
            test.setRemark((test.getRemark() == null ? "" : test.getRemark() + "\n") + 
                          "【系统提醒】发现异常指标: " + String.join(", ", abnormalIndicators));
            bodyTestRepository.save(test);
        }
    }
    
    private void generateTrainingPlan(BodyTest latestTest, Member member) {
        List<BodyTest> historyTests = bodyTestRepository.findLatestByMemberId(member.getId(), 3);
        
        String goal = determineGoal(latestTest, historyTests);
        String duration = "4周";
        String trainingSchedule = generateTrainingSchedule(latestTest);
        String nutritionAdvice = generateNutritionAdvice(latestTest);
        String notes = "根据最近体测数据自动生成";
        
        trainingPlanService.createPlanFromBodyTest(member.getId(), null, goal, duration, trainingSchedule, nutritionAdvice, notes);
        
        logger.info("为会员{}自动生成训练计划", member.getId());
    }
    
    private String determineGoal(BodyTest latestTest, List<BodyTest> historyTests) {
        if (latestTest.getBmi() != null && latestTest.getBmi().doubleValue() > 28) {
            return "减重减脂";
        }
        
        if (latestTest.getMuscleMass() != null && latestTest.getMuscleMass().doubleValue() < 50) {
            return "增肌塑形";
        }
        
        if (latestTest.getVisceralFat() != null && latestTest.getVisceralFat().intValue() > 12) {
            return "健康减脂";
        }
        
        return "维持健康";
    }
    
    private String generateTrainingSchedule(BodyTest test) {
        StringBuilder schedule = new StringBuilder();
        
        if (test.getBmi() != null && test.getBmi().doubleValue() > 28) {
            schedule.append("每周5次训练:\n");
            schedule.append("- 周一、三、五: 有氧训练45分钟(跑步/椭圆机)\n");
            schedule.append("- 周二、四: 力量训练(全身循环)\n");
            schedule.append("- 周六: HIIT训练30分钟\n");
            schedule.append("- 周日: 休息或轻度拉伸");
        } else if (test.getMuscleMass() != null && test.getMuscleMass().doubleValue() < 50) {
            schedule.append("每周4次训练:\n");
            schedule.append("- 周一: 胸部+三头肌\n");
            schedule.append("- 周三: 背部+二头肌\n");
            schedule.append("- 周五: 腿部+肩部\n");
            schedule.append("- 周日: 核心训练+有氧30分钟");
        } else {
            schedule.append("每周3-4次训练:\n");
            schedule.append("- 周一、三: 力量训练(分化训练)\n");
            schedule.append("- 周五: 有氧训练45分钟\n");
            schedule.append("- 可选周日: 瑜伽或普拉提");
        }
        
        return schedule.toString();
    }
    
    private String generateNutritionAdvice(BodyTest test) {
        StringBuilder advice = new StringBuilder();
        
        if (test.getBmi() != null && test.getBmi().doubleValue() > 28) {
            advice.append("饮食建议:\n");
            advice.append("1. 每日热量摄入控制在基础代谢的80-90%\n");
            advice.append("2. 蛋白质摄入量: 1.2-1.6g/kg体重\n");
            advice.append("3. 碳水化合物选择低GI食物\n");
            advice.append("4. 每日饮水量: 30-40ml/kg体重\n");
            advice.append("5. 避免高糖、高脂肪食物");
        } else if (test.getMuscleMass() != null && test.getMuscleMass().doubleValue() < 50) {
            advice.append("饮食建议:\n");
            advice.append("1. 每日热量摄入高于基础代谢300-500大卡\n");
            advice.append("2. 蛋白质摄入量: 1.6-2.2g/kg体重\n");
            advice.append("3. 训练后30分钟内补充蛋白质和碳水\n");
            advice.append("4. 每日分5-6餐进食\n");
            advice.append("5. 保证充足睡眠(7-9小时)");
        } else {
            advice.append("饮食建议:\n");
            advice.append("1. 保持均衡饮食，摄入各类营养素\n");
            advice.append("2. 每日饮水量: 25-30ml/kg体重\n");
            advice.append("3. 控制晚餐热量，避免睡前2小时进食\n");
            advice.append("4. 多摄入蔬菜和水果\n");
            advice.append("5. 减少加工食品和高盐食物");
        }
        
        return advice.toString();
    }
    
    public List<BodyTest> getAbnormalTests() {
        return bodyTestRepository.findAbnormalTests();
    }
}
