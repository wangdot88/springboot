package dangod.springboot.service.impl;

import dangod.springboot.model.FitnessTest;
import dangod.springboot.model.TrainingPlan;
import dangod.springboot.model.Member;
import dangod.springboot.model.Trainer;
import dangod.springboot.dto.FitnessTestDto;
import dangod.springboot.dto.TrainingPlanDto;
import dangod.springboot.repository.FitnessTestRepository;
import dangod.springboot.repository.TrainingPlanRepository;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.TrainerRepository;
import dangod.springboot.service.FitnessTestService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class FitnessTestServiceImpl implements FitnessTestService {

    @Autowired
    private FitnessTestRepository fitnessTestRepository;
    
    @Autowired
    private TrainingPlanRepository trainingPlanRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private TrainerRepository trainerRepository;

    @Override
    public FitnessTest createFitnessTest(FitnessTestDto testDto) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberRepository.findById(testDto.getMemberId());
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Member member = memberOpt.get();
        
        // 计算BMI
        double heightInMeters = testDto.getHeight() / 100;
        double bmi = testDto.getWeight() / (heightInMeters * heightInMeters);
        
        // 创建体测记录
        FitnessTest fitnessTest = new FitnessTest(
            member,
            testDto.getTestDate(),
            testDto.getWeight(),
            testDto.getHeight(),
            bmi,
            testDto.getBodyFatPercentage(),
            testDto.getMuscleMass(),
            testDto.getWaterContent(),
            testDto.getBasalMetabolicRate(),
            testDto.getHeartRate(),
            testDto.getBloodPressureSystolic(),
            testDto.getBloodPressureDiastolic(),
            testDto.getLungCapacity(),
            testDto.getFlexibility(),
            testDto.getNotes()
        );
        
        return fitnessTestRepository.save(fitnessTest);
    }

    @Override
    public FitnessTest getFitnessTestById(Long testId) {
        return fitnessTestRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("体测记录不存在"));
    }

    @Override
    public List<FitnessTest> getFitnessTestsByMember(Long memberId) {
        return fitnessTestRepository.findByMember_Id(memberId);
    }

    @Override
    public List<FitnessTest> getFitnessTestsForMemberOrdered(Long memberId) {
        return fitnessTestRepository.findByMemberIdOrderByDateDesc(memberId);
    }

    @Override
    public List<FitnessTest> getFitnessTestsBetween(LocalDate startDate, LocalDate endDate) {
        return fitnessTestRepository.findTestsBetween(startDate, endDate);
    }

    @Override
    public List<FitnessTest> getFitnessTestsByDate(LocalDate date) {
        return fitnessTestRepository.findTestsByDate(date);
    }

    @Override
    public List<FitnessTest> getAbnormalWeightTests(Double minWeight, Double maxWeight) {
        return fitnessTestRepository.findAbnormalWeight(minWeight, maxWeight);
    }

    @Override
    public List<FitnessTest> getAbnormalBodyFatTests(Double minBfp, Double maxBfp) {
        return fitnessTestRepository.findAbnormalBodyFat(minBfp, maxBfp);
    }

    @Override
    public List<FitnessTest> getAbnormalHeartRateTests(Integer minHr, Integer maxHr) {
        return fitnessTestRepository.findAbnormalHeartRate(minHr, maxHr);
    }

    @Override
    public List<FitnessTest> getAbnormalBloodPressureTests(Integer maxSystolic, Integer maxDiastolic) {
        return fitnessTestRepository.findAbnormalBloodPressure(maxSystolic, maxDiastolic);
    }

    @Override
    public List<FitnessTest> getAbnormalBMITests(Double minBmi, Double maxBmi) {
        return fitnessTestRepository.findAbnormalBMI(minBmi, maxBmi);
    }

    @Override
    public FitnessTest getLatestTestForMember(Long memberId) {
        return fitnessTestRepository.findLatestTestForMember(memberId);
    }

    @Override
    public List<FitnessTest> getAllAbnormalTests() {
        List<FitnessTest> abnormalTests = new ArrayList<>();
        
        // 添加体重异常的测试
        abnormalTests.addAll(getAbnormalWeightTests(40.0, 150.0));
        
        // 添加体脂率异常的测试
        abnormalTests.addAll(getAbnormalBodyFatTests(5.0, 50.0));
        
        // 添加心率异常的测试
        abnormalTests.addAll(getAbnormalHeartRateTests(40, 120));
        
        // 添加血压异常的测试
        abnormalTests.addAll(getAbnormalBloodPressureTests(140, 90));
        
        // 添加BMI异常的测试
        abnormalTests.addAll(getAbnormalBMITests(16.0, 30.0));
        
        // 去重
        return abnormalTests.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void sendAbnormalTestAlerts() {
        List<FitnessTest> abnormalTests = getAllAbnormalTests();
        
        for (FitnessTest test : abnormalTests) {
            Member member = test.getMember();
            
            // 这里应该实现发送提醒的逻辑，例如发送邮件或短信
            // 简化实现，只打印日志
            System.out.println("发送异常指标提醒给会员: " + member.getName() + 
                              " (会员号: " + member.getMemberNumber() + ")");
        }
    }

    @Override
    public TrainingPlan createTrainingPlan(TrainingPlanDto planDto) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberRepository.findById(planDto.getMemberId());
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        // 检查教练是否存在
        Optional<Trainer> trainerOpt = trainerRepository.findById(planDto.getTrainerId());
        if (!trainerOpt.isPresent()) {
            throw new RuntimeException("教练不存在");
        }
        
        TrainingPlan trainingPlan = new TrainingPlan(
            memberOpt.get(),
            trainerOpt.get(),
            planDto.getPlanName(),
            planDto.getDescription(),
            planDto.getStartDate(),
            planDto.getEndDate(),
            planDto.getWeeklyFrequency(),
            planDto.getSessionDuration(),
            planDto.getPrimaryGoal(),
            planDto.getSecondaryGoals(),
            planDto.getExerciseDetails(),
            planDto.getNutritionGuidelines(),
            planDto.getNotes()
        );
        
        return trainingPlanRepository.save(trainingPlan);
    }

    @Override
    public TrainingPlan getTrainingPlanById(Long planId) {
        return trainingPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("训练计划不存在"));
    }

    @Override
    public List<TrainingPlan> getTrainingPlansByMember(Long memberId) {
        return trainingPlanRepository.findByMemberIdOrderByDateDesc(memberId);
    }

    @Override
    public List<TrainingPlan> getActivePlansForMember(Long memberId) {
        return trainingPlanRepository.findActivePlansForMember(memberId);
    }

    @Override
    public List<TrainingPlan> getTrainingPlansByTrainer(Long trainerId) {
        return trainingPlanRepository.findByTrainerId(trainerId);
    }

    @Override
    public List<TrainingPlan> getPlansForDate(LocalDate date) {
        return trainingPlanRepository.findPlansForDate(date);
    }

    @Override
    public List<TrainingPlan> getExpiredActivePlans() {
        return trainingPlanRepository.findExpiredActivePlans(LocalDate.now());
    }

    @Override
    public TrainingPlan getLatestActivePlanForMember(Long memberId) {
        return trainingPlanRepository.findLatestActivePlanForMember(memberId);
    }

    @Override
    public TrainingPlan updateTrainingPlan(Long planId, TrainingPlanDto planDto) {
        Optional<TrainingPlan> planOpt = trainingPlanRepository.findById(planId);
        if (!planOpt.isPresent()) {
            throw new RuntimeException("训练计划不存在");
        }
        
        // 检查会员是否存在
        Optional<Member> memberOpt = memberRepository.findById(planDto.getMemberId());
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        // 检查教练是否存在
        Optional<Trainer> trainerOpt = trainerRepository.findById(planDto.getTrainerId());
        if (!trainerOpt.isPresent()) {
            throw new RuntimeException("教练不存在");
        }
        
        TrainingPlan plan = planOpt.get();
        
        // 更新训练计划信息
        plan.setMember(memberOpt.get());
        plan.setTrainer(trainerOpt.get());
        plan.setPlanName(planDto.getPlanName());
        plan.setDescription(planDto.getDescription());
        plan.setStartDate(planDto.getStartDate());
        plan.setEndDate(planDto.getEndDate());
        plan.setWeeklyFrequency(planDto.getWeeklyFrequency());
        plan.setSessionDuration(planDto.getSessionDuration());
        plan.setPrimaryGoal(planDto.getPrimaryGoal());
        plan.setSecondaryGoals(planDto.getSecondaryGoals());
        plan.setExerciseDetails(planDto.getExerciseDetails());
        plan.setNutritionGuidelines(planDto.getNutritionGuidelines());
        plan.setNotes(planDto.getNotes());
        
        return trainingPlanRepository.save(plan);
    }

    @Override
    public TrainingPlan deactivateTrainingPlan(Long planId) {
        Optional<TrainingPlan> planOpt = trainingPlanRepository.findById(planId);
        if (!planOpt.isPresent()) {
            throw new RuntimeException("训练计划不存在");
        }
        
        TrainingPlan plan = planOpt.get();
        plan.setActive(false);
        
        return trainingPlanRepository.save(plan);
    }

    @Override
    public TrainingPlan generateTrainingPlanFromLatestTest(Long memberId) {
        // 获取会员最新的体测数据
        FitnessTest latestTest = getLatestTestForMember(memberId);
        if (latestTest == null) {
            throw new RuntimeException("会员没有体测数据");
        }
        
        // 获取会员信息
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Member member = memberOpt.get();
        
        // 根据体测数据生成训练计划
        String primaryGoal = determinePrimaryGoal(latestTest);
        String exerciseDetails = generateExerciseDetails(latestTest);
        String nutritionGuidelines = generateNutritionGuidelines(latestTest);
        
        // 创建训练计划
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(3);
        
        TrainingPlanDto planDto = new TrainingPlanDto();
        planDto.setMemberId(memberId);
        planDto.setTrainerId(1L); // 默认教练ID，实际应用中应该有更合理的逻辑
        planDto.setPlanName("基于体测数据的个性化训练计划");
        planDto.setDescription("根据最新体测数据生成的个性化训练计划");
        planDto.setStartDate(today);
        planDto.setEndDate(endDate);
        planDto.setWeeklyFrequency(3);
        planDto.setSessionDuration(60);
        planDto.setPrimaryGoal(primaryGoal);
        planDto.setExerciseDetails(exerciseDetails);
        planDto.setNutritionGuidelines(nutritionGuidelines);
        planDto.setNotes("此计划由系统根据最新体测数据自动生成");
        
        return createTrainingPlan(planDto);
    }

    @Override
    public ModelAndView exportHealthReport(Long memberId, LocalDate startDate, LocalDate endDate) {
        // 获取会员信息
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (!memberOpt.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Member member = memberOpt.get();
        
        // 获取体测数据
        List<FitnessTest> tests = getFitnessTestsBetween(startDate, endDate);
        
        // 创建Excel工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("健康报告");
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"测试日期", "体重(kg)", "身高(cm)", "BMI", "体脂率(%)", "肌肉量(kg)", 
                           "水分含量(%)", "基础代谢率", "心率", "收缩压", "舒张压", "肺活量", "柔韧性", "备注"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // 填充数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (int i = 0; i < tests.size(); i++) {
            Row row = sheet.createRow(i + 1);
            FitnessTest test = tests.get(i);
            
            row.createCell(0).setCellValue(test.getTestDate().format(formatter));
            row.createCell(1).setCellValue(test.getWeight());
            row.createCell(2).setCellValue(test.getHeight());
            row.createCell(3).setCellValue(test.getBmi());
            row.createCell(4).setCellValue(test.getBodyFatPercentage());
            row.createCell(5).setCellValue(test.getMuscleMass());
            row.createCell(6).setCellValue(test.getWaterContent());
            row.createCell(7).setCellValue(test.getBasalMetabolicRate());
            row.createCell(8).setCellValue(test.getHeartRate());
            row.createCell(9).setCellValue(test.getBloodPressureSystolic());
            row.createCell(10).setCellValue(test.getBloodPressureDiastolic());
            row.createCell(11).setCellValue(test.getLungCapacity());
            row.createCell(12).setCellValue(test.getFlexibility());
            row.createCell(13).setCellValue(test.getNotes() != null ? test.getNotes() : "");
        }
        
        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        try {
            // 将工作簿写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            
            // 创建ModelAndView用于下载
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("reportData", outputStream.toByteArray());
            modelAndView.addObject("fileName", "健康报告_" + member.getName() + "_" + 
                                 LocalDate.now().format(formatter) + ".xlsx");
            
            return modelAndView;
        } catch (IOException e) {
            throw new RuntimeException("导出健康报告失败", e);
        }
    }

    @Override
    public Map<String, Object> getFitnessTestChartData(Long memberId, LocalDate startDate, LocalDate endDate) {
        List<FitnessTest> tests = getFitnessTestsBetween(startDate, endDate);
        
        Map<String, Object> chartData = new HashMap<>();
        
        List<String> dates = new ArrayList<>();
        List<Double> weights = new ArrayList<>();
        List<Double> bmis = new ArrayList<>();
        List<Double> bodyFatPercentages = new ArrayList<>();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (FitnessTest test : tests) {
            dates.add(test.getTestDate().format(formatter));
            weights.add(test.getWeight());
            bmis.add(test.getBmi());
            bodyFatPercentages.add(test.getBodyFatPercentage());
        }
        
        chartData.put("dates", dates);
        chartData.put("weights", weights);
        chartData.put("bmis", bmis);
        chartData.put("bodyFatPercentages", bodyFatPercentages);
        
        return chartData;
    }

    @Override
    public void deleteFitnessTest(Long testId) {
        if (!fitnessTestRepository.existsById(testId)) {
            throw new RuntimeException("体测记录不存在");
        }
        
        fitnessTestRepository.deleteById(testId);
    }

    @Override
    public void deleteTrainingPlan(Long planId) {
        if (!trainingPlanRepository.existsById(planId)) {
            throw new RuntimeException("训练计划不存在");
        }
        
        trainingPlanRepository.deleteById(planId);
    }
    
    // 辅助方法：根据体测数据确定主要目标
    private String determinePrimaryGoal(FitnessTest test) {
        if (test.getBmi() > 25) {
            return "减脂";
        } else if (test.getBmi() < 18.5) {
            return "增肌";
        } else if (test.getBodyFatPercentage() > 25) {
            return "塑形";
        } else {
            return "保持健康";
        }
    }
    
    // 辅助方法：根据体测数据生成训练详情
    private String generateExerciseDetails(FitnessTest test) {
        StringBuilder details = new StringBuilder();
        
        if (test.getBmi() > 25) {
            details.append("有氧运动：每周3-4次，每次30-45分钟，如跑步、游泳、单车等。\n");
            details.append("力量训练：每周2-3次，重点训练大肌群，如深蹲、硬拉、卧推等。\n");
        } else if (test.getBmi() < 18.5) {
            details.append("力量训练：每周3-4次，重点训练大肌群，增加肌肉量。\n");
            details.append("有氧运动：每周1-2次，每次20-30分钟，避免过度消耗。\n");
        } else {
            details.append("综合训练：每周3-4次，包括有氧和力量训练。\n");
            details.append("柔韧性训练：每周1-2次，如瑜伽、拉伸等。\n");
        }
        
        return details.toString();
    }
    
    // 辅助方法：根据体测数据生成营养指导
    private String generateNutritionGuidelines(FitnessTest test) {
        StringBuilder guidelines = new StringBuilder();
        
        if (test.getBmi() > 25) {
            guidelines.append("控制总热量摄入，建议每日热量缺口300-500千卡。\n");
            guidelines.append("增加蛋白质摄入，建议每公斤体重1.5-2.0克蛋白质。\n");
            guidelines.append("减少高糖、高脂肪食物，增加蔬菜水果摄入。\n");
        } else if (test.getBmi() < 18.5) {
            guidelines.append("增加总热量摄入，建议每日热量盈余300-500千卡。\n");
            guidelines.append("增加蛋白质摄入，建议每公斤体重1.8-2.2克蛋白质。\n");
            guidelines.append("增加优质碳水化合物和健康脂肪的摄入。\n");
        } else {
            guidelines.append("保持均衡饮食，确保各类营养素摄入充足。\n");
            guidelines.append("控制食物份量，避免过量摄入。\n");
            guidelines.append("增加蔬菜水果摄入，减少加工食品。\n");
        }
        
        return guidelines.toString();
    }
}