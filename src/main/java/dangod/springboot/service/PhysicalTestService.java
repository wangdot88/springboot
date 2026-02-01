package dangod.springboot.service;

import dangod.springboot.core.common.ResultCode;
import dangod.springboot.dto.PhysicalTestDTO;
import dangod.springboot.entity.Member;
import dangod.springboot.entity.PhysicalTest;
import dangod.springboot.entity.TrainingPlan;
import dangod.springboot.core.exception.BusinessException;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.repository.PhysicalTestRepository;
import dangod.springboot.repository.TrainingPlanRepository;
import dangod.springboot.core.util.IdUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PhysicalTestService {

    @Autowired
    private PhysicalTestRepository physicalTestRepository;

    @Autowired
    private TrainingPlanRepository trainingPlanRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public PhysicalTest createTest(PhysicalTest test) {
        test.setTestNo(IdUtil.generateTestNo());
        test.setTestTime(LocalDateTime.now());
        test.setCreatedAt(LocalDateTime.now());
        test.setUpdatedAt(LocalDateTime.now());
        test = physicalTestRepository.save(test);

        checkAbnormalData(test);
        
        return test;
    }

    public PhysicalTest updateTest(PhysicalTest test) {
        test.setUpdatedAt(LocalDateTime.now());
        return physicalTestRepository.save(test);
    }

    public void deleteTest(Long testId) {
        PhysicalTest test = physicalTestRepository.findOne(testId);
        if (test != null) {
            physicalTestRepository.delete(test);
        }
    }

    public PhysicalTest getTestById(Long testId) {
        PhysicalTest test = physicalTestRepository.findOne(testId);
        if (test == null) {
            throw new BusinessException(ResultCode.TEST_NOT_FOUND);
        }
        return test;
    }

    public List<PhysicalTest> getMemberTests(Long memberId) {
        return physicalTestRepository.findByMemberIdOrderByTestTimeDesc(memberId);
    }

    public List<PhysicalTest> getMemberTestsByDateRange(Long memberId, LocalDateTime start, LocalDateTime end) {
        return physicalTestRepository.findByMemberIdAndTestTimeBetween(memberId, start, end);
    }

    public Map<String, List<PhysicalTestDTO>> getTestChartData(Long memberId) {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<PhysicalTest> tests = physicalTestRepository.findLastThreeMonthsData(memberId, threeMonthsAgo);
        
        Map<String, List<PhysicalTestDTO>> chartData = new HashMap<>();
        chartData.put("bmi", tests.stream().map(t -> {
            PhysicalTestDTO dto = new PhysicalTestDTO();
            dto.setTestTime(t.getTestTime());
            dto.setBmi(t.getBmi());
            return dto;
        }).toList());
        chartData.put("bodyFat", tests.stream().map(t -> {
            PhysicalTestDTO dto = new PhysicalTestDTO();
            dto.setTestTime(t.getTestTime());
            dto.setBodyFatRate(t.getBodyFatRate());
            return dto;
        }).toList());
        chartData.put("muscle", tests.stream().map(t -> {
            PhysicalTestDTO dto = new PhysicalTestDTO();
            dto.setTestTime(t.getTestTime());
            dto.setMuscleMass(t.getMuscleMass());
            return dto;
        }).toList());
        
        return chartData;
    }

    private void checkAbnormalData(PhysicalTest test) {
        StringBuilder message = new StringBuilder();
        
        if (test.getBmi().compareTo(new BigDecimal("28")) > 0) {
            message.append("BMI指数偏高(>28)，建议进行减脂训练；");
        } else if (test.getBmi().compareTo(new BigDecimal("18.5")) < 0) {
            message.append("BMI指数偏低(<18.5)，建议增肌训练；");
        }
        
        if (test.getBodyFatRate().compareTo(new BigDecimal("25")) > 0) {
            message.append("体脂率偏高(>25%)，建议增加有氧运动；");
        }
        
        if (test.getRestingHeartRate() > 80) {
            message.append("静息心率偏高(>80)，建议进行心肺训练；");
        } else if (test.getRestingHeartRate() < 60) {
            message.append("静息心率偏低(<60)，请注意身体健康；");
        }
        
        if (test.getMuscleMass().compareTo(new BigDecimal("40")) < 0) {
            message.append("肌肉量偏低(<40kg)，建议增加力量训练；");
        }
        
        if (message.length() > 0) {
            notificationService.createNotification(
                    test.getMemberId(),
                    "体测数据异常提醒",
                    message.toString()
            );
        }
    }

    @Transactional
    public TrainingPlan generateTrainingPlan(Long testId) {
        PhysicalTest test = getTestById(testId);
        
        TrainingPlan plan = new TrainingPlan();
        plan.setPlanNo(IdUtil.generatePlanNo());
        plan.setMemberId(test.getMemberId());
        plan.setTestId(testId);
        plan.setPlanContent(generatePlanContent(test));
        plan.setStartDate(LocalDateTime.now().toLocalDate());
        plan.setEndDate(LocalDateTime.now().plusMonths(1).toLocalDate());
        plan.setStatus(1);
        plan.setCreatedAt(LocalDateTime.now());
        plan.setUpdatedAt(LocalDateTime.now());
        
        return trainingPlanRepository.save(plan);
    }

    private String generatePlanContent(PhysicalTest test) {
        StringBuilder content = new StringBuilder();
        content.append("=== 个人训练计划 ===\n\n");
        content.append("一、基础数据\n");
        content.append("BMI: ").append(String.format("%.1f", test.getBmi())).append("\n");
        content.append("体脂率: ").append(String.format("%.1f%%", test.getBodyFatRate())).append("\n");
        content.append("肌肉量: ").append(String.format("%.1fkg", test.getMuscleMass())).append("\n\n");
        
        content.append("二、训练建议\n");
        
        if (test.getBmi().compareTo(new BigDecimal("28")) > 0 || test.getBodyFatRate().compareTo(new BigDecimal("25")) > 0) {
            content.append("1. 减脂期（每周5-6次）\n");
            content.append("   - 有氧训练：跑步40分钟/游泳30分钟/动感单车45分钟\n");
            content.append("   - 力量训练：全身循环训练，每组12-15次\n");
        }
        
        if (test.getMuscleMass().compareTo(new BigDecimal("40")) < 0) {
            content.append("2. 增肌期（每周4-5次）\n");
            content.append("   - 上肢训练：卧推、引体向上、哑铃划船\n");
            content.append("   - 下肢训练：深蹲、硬拉、腿举\n");
            content.append("   - 核心训练：平板支撑、卷腹\n");
        }
        
        if (test.getCardioEndurance() < 80) {
            content.append("3. 心肺训练（每周2-3次）\n");
            content.append("   - 间歇训练：快跑1分钟+快走2分钟，重复10组\n");
            content.append("   - 稳态有氧：慢跑30分钟\n");
        }
        
        content.append("\n三、饮食建议\n");
        content.append("   - 蛋白质：每公斤体重1.6-2.2g\n");
        content.append("   - 碳水：训练前后补充\n");
        content.append("   - 脂肪：选择健康脂肪（坚果、橄榄油）\n");
        
        return content.toString();
    }

    public List<TrainingPlan> getMemberPlans(Long memberId) {
        return trainingPlanRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public byte[] exportHealthReport(Long memberId) throws Exception {
        Member member = memberRepository.findOne(memberId);
        if (member == null) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        List<PhysicalTest> tests = getMemberTests(memberId);
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("健康报告");
        
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("会员姓名");
        headerRow.createCell(1).setCellValue(member.getName());
        headerRow.createCell(2).setCellValue("会员号");
        headerRow.createCell(3).setCellValue(member.getMemberNo());
        
        Row titleRow = sheet.createRow(2);
        titleRow.createCell(0).setCellValue("测试时间");
        titleRow.createCell(1).setCellValue("BMI");
        titleRow.createCell(2).setCellValue("体脂率(%)");
        titleRow.createCell(3).setCellValue("肌肉量(kg)");
        titleRow.createCell(4).setCellValue("静息心率");
        titleRow.createCell(5).setCellValue("肺活量");
        titleRow.createCell(6).setCellValue("柔韧性");
        titleRow.createCell(7).setCellValue("心肺耐力");
        
        int rowNum = 3;
        for (PhysicalTest test : tests) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(test.getTestTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            row.createCell(1).setCellValue(test.getBmi());
            row.createCell(2).setCellValue(test.getBodyFatRate());
            row.createCell(3).setCellValue(test.getMuscleMass());
            row.createCell(4).setCellValue(test.getRestingHeartRate());
            row.createCell(5).setCellValue(test.getVitalCapacity());
            row.createCell(6).setCellValue(test.getFlexibility());
            row.createCell(7).setCellValue(test.getCardioEndurance());
        }
        
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
}
