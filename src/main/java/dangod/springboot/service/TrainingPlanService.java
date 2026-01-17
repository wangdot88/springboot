package dangod.springboot.service;

import dangod.springboot.common.BusinessException;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.TrainingPlan;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.TrainingPlanRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dangod.springboot.entity.BodyTest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TrainingPlanService {
    private static final Logger logger = LoggerFactory.getLogger(TrainingPlanService.class);
    
    @Autowired
    private TrainingPlanRepository trainingPlanRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Transactional
    public TrainingPlan createPlan(TrainingPlan plan) {
        if (plan.getMember() == null || plan.getMember().getId() == null) {
            throw new BusinessException("会员信息不能为空");
        }
        
        Member member = memberRepository.findById(plan.getMember().getId()).orElse(null);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        if (plan.getStartDate() == null) {
            plan.setStartDate(LocalDateTime.now());
        }
        
        if (plan.getGoal() == null || plan.getGoal().isEmpty()) {
            throw new BusinessException("训练目标不能为空");
        }
        
        plan.setStatus(1);
        plan.setCreateTime(LocalDateTime.now());
        
        TrainingPlan saved = trainingPlanRepository.save(plan);
        logger.info("创建训练计划成功: 会员{}，目标: {}", member.getId(), plan.getGoal());
        return saved;
    }
    
    @Transactional
    public TrainingPlan createPlanFromBodyTest(Long memberId, Long coachId, String goal, String duration, 
                                               String trainingSchedule, String nutritionAdvice, String notes) {
        TrainingPlan plan = new TrainingPlan();
        
        Member member = new Member();
        member.setId(memberId);
        plan.setMember(member);
        
        plan.setGoal(goal);
        plan.setDuration(duration);
        plan.setTrainingSchedule(trainingSchedule);
        plan.setNutritionAdvice(nutritionAdvice);
        plan.setNotes(notes);
        plan.setStartDate(LocalDateTime.now());
        plan.setStatus(1);
        plan.setCreateTime(LocalDateTime.now());
        plan.setCreateBy("system");
        
        return trainingPlanRepository.save(plan);
    }
    
    @Transactional
    public TrainingPlan updatePlan(TrainingPlan plan) {
        TrainingPlan existing = trainingPlanRepository.findById(plan.getId()).orElse(null);
        if (existing == null) {
            throw new BusinessException("训练计划不存在");
        }
        
        if (plan.getGoal() != null) {
            existing.setGoal(plan.getGoal());
        }
        if (plan.getDuration() != null) {
            existing.setDuration(plan.getDuration());
        }
        if (plan.getStartDate() != null) {
            existing.setStartDate(plan.getStartDate());
        }
        if (plan.getEndDate() != null) {
            existing.setEndDate(plan.getEndDate());
        }
        if (plan.getExercisePlan() != null) {
            existing.setExercisePlan(plan.getExercisePlan());
        }
        if (plan.getNutritionSuggestion() != null) {
            existing.setNutritionSuggestion(plan.getNutritionSuggestion());
        }
        if (plan.getRestSuggestion() != null) {
            existing.setRestSuggestion(plan.getRestSuggestion());
        }
        if (plan.getStatus() != null) {
            existing.setStatus(plan.getStatus());
        }
        existing.setUpdateTime(LocalDateTime.now());
        
        return trainingPlanRepository.save(existing);
    }
    
    @Transactional
    public void deletePlan(Long planId) {
        TrainingPlan plan = trainingPlanRepository.findById(planId).orElse(null);
        if (plan == null) {
            throw new BusinessException("训练计划不存在");
        }
        
        trainingPlanRepository.delete(plan);
        logger.info("删除训练计划成功: {}", planId);
    }
    
    public TrainingPlan getPlanById(Long planId) {
        return trainingPlanRepository.findById(planId).orElse(null);
    }
    
    public List<TrainingPlan> getPlansByMember(Long memberId) {
        return trainingPlanRepository.findLatestByMemberId(memberId);
    }
    
    public TrainingPlan getActivePlan(Long memberId) {
        return trainingPlanRepository.findByMemberIdAndStatus(memberId, 1);
    }
    
    public byte[] exportHealthReport(Long memberId) throws IOException {
        Member member = memberRepository.findById(memberId).orElse(null);
        if (member == null) {
            throw new BusinessException("会员不存在");
        }
        
        BodyTestService bodyTestService = new BodyTestService();
        List<BodyTest> bodyTests = bodyTestService.getBodyTestsByMember(memberId);
        TrainingPlan activePlan = getActivePlan(memberId);
        
        Workbook workbook = new XSSFWorkbook();
        
        Sheet memberSheet = workbook.createSheet("会员信息");
        createMemberSheet(memberSheet, member);
        
        Sheet bodyTestSheet = workbook.createSheet("体测记录");
        createBodyTestSheet(bodyTestSheet, bodyTests);
        
        if (activePlan != null) {
            Sheet planSheet = workbook.createSheet("训练计划");
            createPlanSheet(planSheet, activePlan);
        }
        
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        logger.info("导出健康报告成功: 会员{}", memberId);
        return outputStream.toByteArray();
    }
    
    private void createMemberSheet(Sheet sheet, Member member) {
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("健康报告 - 会员信息");
        
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerStyle.setFont(headerFont);
        headerCell.setCellStyle(headerStyle);
        
        int rowNum = 2;
        createKeyValueRow(sheet, rowNum++, "姓名", member.getName());
        createKeyValueRow(sheet, rowNum++, "手机号", member.getPhone());
        createKeyValueRow(sheet, rowNum++, "性别", member.getGender() != null ? member.getGender() : "");
        createKeyValueRow(sheet, rowNum++, "会员等级", member.getCardLevel() != null ? member.getCardLevel().getLevelName() : "");
        createKeyValueRow(sheet, rowNum++, "会员卡等级", member.getCardLevel() != null ? member.getCardLevel().getLevelName() : "");
        createKeyValueRow(sheet, rowNum++, "会员状态", member.getStatus() != null ? (member.getStatus() == 1 ? "正常" : "已冻结") : "");
        createKeyValueRow(sheet, rowNum++, "到期日期", member.getExpireDate() != null ? member.getExpireDate().toString() : "");
        createKeyValueRow(sheet, rowNum++, "剩余积分", String.valueOf(member.getTotalPoints() - member.getUsedPoints()));
        createKeyValueRow(sheet, rowNum++, "注册日期", member.getRegisterDate() != null ? member.getRegisterDate().toString() : "");
        createKeyValueRow(sheet, rowNum++, "所属门店", member.getStore() != null ? member.getStore().getStoreName() : "");
    }
    
    private void createBodyTestSheet(Sheet sheet, List<BodyTest> bodyTests) {
        Row headerRow = sheet.createRow(0);
        String[] headers = {"体测日期", "体重(kg)", "BMI", "体脂率(%)", "肌肉量(kg)", "内脏脂肪", "基础代谢", "肌肉率(%)", "水分率(%)", "代谢年龄"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            
            CellStyle style = sheet.getWorkbook().createCellStyle();
            Font font = sheet.getWorkbook().createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }
        
        int rowNum = 1;
        for (BodyTest test : bodyTests) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(test.getTestDate() != null ? test.getTestDate().toString() : "");
            row.createCell(1).setCellValue(test.getWeight() != null ? test.getWeight().toString() : "");
            row.createCell(2).setCellValue(test.getBmi() != null ? test.getBmi().toString() : "");
            row.createCell(3).setCellValue(test.getBodyFatRate() != null ? test.getBodyFatRate().toString() : "");
            row.createCell(4).setCellValue(test.getMuscleMass() != null ? test.getMuscleMass().toString() : "");
            row.createCell(5).setCellValue(test.getVisceralFat() != null ? test.getVisceralFat().toString() : "");
            row.createCell(6).setCellValue(test.getBasalMetabolism() != null ? test.getBasalMetabolism().toString() : "");
            row.createCell(7).setCellValue(test.getMuscleRate() != null ? test.getMuscleRate().toString() : "");
            row.createCell(8).setCellValue(test.getWaterRate() != null ? test.getWaterRate().toString() : "");
            row.createCell(9).setCellValue(test.getMetabolicAge() != null ? test.getMetabolicAge().toString() : "");
        }
    }
    
    private void createPlanSheet(Sheet sheet, TrainingPlan plan) {
        Row headerRow = sheet.createRow(0);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("训练计划");
        
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerStyle.setFont(headerFont);
        headerCell.setCellStyle(headerStyle);
        
        int rowNum = 2;
        createKeyValueRow(sheet, rowNum++, "训练目标", plan.getGoal());
        createKeyValueRow(sheet, rowNum++, "训练周期", plan.getDuration());
        createKeyValueRow(sheet, rowNum++, "开始日期", plan.getStartDate() != null ? plan.getStartDate().toString() : "");
        createKeyValueRow(sheet, rowNum++, "训练安排", plan.getTrainingSchedule());
        createKeyValueRow(sheet, rowNum++, "营养建议", plan.getNutritionAdvice());
        createKeyValueRow(sheet, rowNum++, "备注", plan.getNotes());
    }
    
    private void createKeyValueRow(Sheet sheet, int rowNum, String key, String value) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value != null ? value : "");
    }
}
