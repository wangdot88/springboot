package dangod.springboot.task;

import dangod.springboot.entity.Member;
import dangod.springboot.repository.MemberRepository;
import dangod.springboot.service.EmailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class MemberExpireReminderJob implements Job {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;

    @Value("${gym.member.expire-remind-days:7}")
    private int expireRemindDays;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDate = now.plusDays(expireRemindDays);
        
        List<Member> expiringMembers = memberRepository.findExpiringMembers(now, expireDate);
        
        for (Member member : expiringMembers) {
            try {
                int daysUntilExpire = (int) java.time.temporal.ChronoUnit.DAYS.between(now, member.getExpireDate());
                String subject = "【健身房】您的会员卡即将到期提醒";
                String content = String.format(
                    "亲爱的%s会员：\n\n" +
                    "您的会员卡将于%d天后到期（%s）。\n" +
                    "当前会员等级：%s\n" +
                    "为了不影响您的正常使用，请及时续费。\n" +
                    "如有任何疑问，请联系客服。\n\n" +
                    "祝您健身愉快！\n" +
                    "健身房管理团队",
                    member.getName(),
                    daysUntilExpire,
                    member.getExpireDate().toLocalDate().toString(),
                    member.getCardLevel().getName()
                );
                
                if (member.getEmail() != null && !member.getEmail().isEmpty()) {
                    emailService.sendSimpleEmail(member.getEmail(), subject, content);
                }
                
                System.out.println("发送到期提醒给会员：" + member.getName() + ", 电话：" + member.getPhone());
                
            } catch (Exception e) {
                System.err.println("发送邮件失败：" + member.getEmail() + ", 错误：" + e.getMessage());
            }
        }
    }
}
