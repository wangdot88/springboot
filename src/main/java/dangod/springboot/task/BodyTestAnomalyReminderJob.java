package dangod.springboot.task;

import dangod.springboot.entity.BodyTest;
import dangod.springboot.entity.Member;
import dangod.springboot.repository.BodyTestRepository;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.service.EmailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BodyTestAnomalyReminderJob implements Job {

    @Autowired
    private BodyTestRepository bodyTestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        
        List<BodyTest> recentBodyTests = bodyTestRepository.findByTestDateAfter(sevenDaysAgo);
        
        List<BodyTest> anomalyTests = recentBodyTests.stream()
                .filter(this::hasAnomaly)
                .collect(Collectors.toList());
        
        for (BodyTest bodyTest : anomalyTests) {
            try {
                Member member = bodyTest.getMember();
                String subject = "【健身房】体测指标异常提醒";
                String content = buildAnomalyContent(bodyTest, member);
                
                if (member.getEmail() != null && !member.getEmail().isEmpty()) {
                    emailService.sendSimpleEmail(member.getEmail(), subject, content);
                }
                
                System.out.println("发送体测异常提醒给会员：" + member.getName() + ", 电话：" + member.getPhone());
                
            } catch (Exception e) {
                System.err.println("发送体测异常提醒邮件失败：" + e.getMessage());
            }
        }
    }

    private boolean hasAnomaly(BodyTest bodyTest) {
        if (bodyTest.getBmi() != null) {
            if (bodyTest.getBmi() < 18.5 || bodyTest.getBmi() > 24) {
                return true;
            }
        }
        
        if (bodyTest.getBodyFatRate() != null) {
            if (bodyTest.getBodyFatRate() < 10 || bodyTest.getBodyFatRate() > 20) {
                return true;
            }
        }
        
        if (bodyTest.getVisceralFat() != null) {
            if (bodyTest.getVisceralFat() < 3 || bodyTest.getVisceralFat() > 10) {
                return true;
            }
        }
        
        if (bodyTest.getSystolicBloodPressure() != null && bodyTest.getDiastolicBloodPressure() != null) {
            if (bodyTest.getSystolicBloodPressure() > 140 || bodyTest.getDiastolicBloodPressure() > 90) {
                return true;
            }
        }
        
        if (bodyTest.getHeartRate() != null) {
            if (bodyTest.getHeartRate() < 60 || bodyTest.getHeartRate() > 100) {
                return true;
            }
        }
        
        return false;
    }

    private String buildAnomalyContent(BodyTest bodyTest, Member member) {
        StringBuilder content = new StringBuilder();
        content.append("亲爱的").append(member.getName()).append("会员：\n\n");
        content.append("您的体测报告显示存在以下异常指标：\n\n");
        
        if (bodyTest.getBmi() != null && (bodyTest.getBmi() < 18.5 || bodyTest.getBmi() > 24)) {
            content.append("• BMI：").append(bodyTest.getBmi()).append(" (正常范围：18.5-24)\n");
        }
        
        if (bodyTest.getBodyFatRate() != null && (bodyTest.getBodyFatRate() < 10 || bodyTest.getBodyFatRate() > 20)) {
            content.append("• 体脂率：").append(bodyTest.getBodyFatRate()).append("% (正常范围：10%-20%)\n");
        }
        
        if (bodyTest.getVisceralFat() != null && (bodyTest.getVisceralFat() < 3 || bodyTest.getVisceralFat() > 10)) {
            content.append("• 内脏脂肪等级：").append(bodyTest.getVisceralFat()).append(" (正常范围：3-10)\n");
        }
        
        if (bodyTest.getSystolicBloodPressure() != null && bodyTest.getDiastolicBloodPressure() != null) {
            if (bodyTest.getSystolicBloodPressure() > 140 || bodyTest.getDiastolicBloodPressure() > 90) {
                content.append("• 血压：").append(bodyTest.getSystolicBloodPressure()).append("/").append(bodyTest.getDiastolicBloodPressure())
                    .append("mmHg (正常范围：≤140/90mmHg)\n");
            }
        }
        
        if (bodyTest.getHeartRate() != null && (bodyTest.getHeartRate() < 60 || bodyTest.getHeartRate() > 100)) {
            content.append("• 心率：").append(bodyTest.getHeartRate()).append("次/分钟 (正常范围：60-100)\n");
        }
        
        content.append("\n建议您咨询专业教练，制定针对性的训练计划。\n");
        content.append("如有任何疑问，请联系客服。\n\n");
        content.append("祝您健身愉快！\n");
        content.append("健身房管理团队");
        
        return content.toString();
    }
}
