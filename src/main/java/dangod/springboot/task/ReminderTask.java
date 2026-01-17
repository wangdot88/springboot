package dangod.springboot.task;

import dangod.springboot.entity.BodyMeasurement;
import dangod.springboot.entity.Member;
import dangod.springboot.repository.BodyMeasurementRepository;
import dangod.springboot.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ReminderTask {

    private static final Logger logger = LoggerFactory.getLogger(ReminderTask.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BodyMeasurementRepository bodyMeasurementRepository;

    @Scheduled(cron = "0 0 9 * * ?")
    public void checkCardExpiration() {
        logger.info("开始检查会员卡到期情况");
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        Date sevenDaysLater = calendar.getTime();
        
        List<Member> expiringMembers = memberRepository.findByCardExpirationDateBeforeAndStatus(
                sevenDaysLater, Member.MemberStatus.ACTIVE);
        
        for (Member member : expiringMembers) {
            if (member.getCardEndTime() != null) {
                long daysUntilExpiration = calculateDaysUntilExpiration(member.getCardEndTime());
                
                if (daysUntilExpiration <= 7 && daysUntilExpiration > 0) {
                    sendExpirationReminder(member, daysUntilExpiration);
                }
            }
        }
        
        logger.info("会员卡到期检查完成，共处理 {} 个即将到期的会员", expiringMembers.size());
    }

    private long calculateDaysUntilExpiration(Date expirationDate) {
        long diff = expirationDate.getTime() - new Date().getTime();
        return diff / (1000 * 60 * 60 * 24);
    }

    private void sendExpirationReminder(Member member, long daysUntilExpiration) {
        logger.info("发送会员卡到期提醒 - 会员ID: {}, 姓名: {}, 剩余天数: {}", 
                member.getId(), member.getName(), daysUntilExpiration);
        
        String message = String.format(
                "尊敬的会员 %s，您的会员卡将在 %d 天后到期，请及时续费以享受会员权益。",
                member.getName(), daysUntilExpiration
        );
        
        sendNotification(member, "会员卡到期提醒", message);
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void checkAbnormalIndicators() {
        logger.info("开始检查体测异常指标");
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();
        
        List<BodyMeasurement> recentMeasurements = bodyMeasurementRepository.findByCreateTimeAfter(yesterday);
        
        for (BodyMeasurement measurement : recentMeasurements) {
            if (hasAbnormalIndicators(measurement)) {
                Member member = memberRepository.findOne(measurement.getMemberId());
                if (member != null) {
                    sendAbnormalIndicatorReminder(member, measurement);
                }
            }
        }
        
        logger.info("体测异常指标检查完成，共处理 {} 条体测记录", recentMeasurements.size());
    }

    private boolean hasAbnormalIndicators(BodyMeasurement measurement) {
        boolean hasAbnormal = false;
        
        if (measurement.getBmi() != null) {
            if (measurement.getBmi() < 18.5 || measurement.getBmi() > 28) {
                hasAbnormal = true;
            }
        }
        
        if (measurement.getBodyFat() != null) {
            if (measurement.getBodyFat() > 30) {
                hasAbnormal = true;
            }
        }
        
        if (measurement.getHeartRate() != null) {
            if (measurement.getHeartRate() < 60 || measurement.getHeartRate() > 100) {
                hasAbnormal = true;
            }
        }
        
        if (measurement.getBloodPressureSystolic() != null && measurement.getBloodPressureDiastolic() != null) {
            if (measurement.getBloodPressureSystolic() > 140 || measurement.getBloodPressureDiastolic() > 90) {
                hasAbnormal = true;
            }
        }
        
        return hasAbnormal;
    }

    private void sendAbnormalIndicatorReminder(Member member, BodyMeasurement measurement) {
        logger.info("发送体测异常指标提醒 - 会员ID: {}, 姓名: {}", member.getId(), member.getName());
        
        StringBuilder message = new StringBuilder();
        message.append("尊敬的会员 ").append(member.getName()).append("，您的最新体测数据中发现以下异常指标：\n\n");
        
        if (measurement.getBmi() != null) {
            if (measurement.getBmi() < 18.5) {
                message.append("- BMI指数偏低（").append(measurement.getBmi()).append("），建议适当增加营养摄入\n");
            } else if (measurement.getBmi() > 28) {
                message.append("- BMI指数偏高（").append(measurement.getBmi()).append("），建议控制饮食并加强运动\n");
            }
        }
        
        if (measurement.getBodyFat() != null && measurement.getBodyFat() > 30) {
            message.append("- 体脂率偏高（").append(measurement.getBodyFat()).append("%），建议增加有氧运动\n");
        }
        
        if (measurement.getHeartRate() != null) {
            if (measurement.getHeartRate() < 60) {
                message.append("- 静息心率偏低（").append(measurement.getHeartRate()).append("次/分），建议咨询专业医生\n");
            } else if (measurement.getHeartRate() > 100) {
                message.append("- 静息心率偏高（").append(measurement.getHeartRate()).append("次/分），建议咨询专业医生\n");
            }
        }
        
        if (measurement.getBloodPressureSystolic() != null && measurement.getBloodPressureDiastolic() != null) {
            if (measurement.getBloodPressureSystolic() > 140 || measurement.getBloodPressureDiastolic() > 90) {
                message.append("- 血压偏高（").append(measurement.getBloodPressureSystolic()).append("/")
                        .append(measurement.getBloodPressureDiastolic()).append("mmHg），建议咨询专业医生\n");
            }
        }
        
        message.append("\n建议您根据专业教练的指导进行针对性的训练。");
        
        sendNotification(member, "体测异常指标提醒", message.toString());
    }

    @Scheduled(cron = "0 0 8 * * MON")
    public void sendWeeklyCourseReminder() {
        logger.info("开始发送每周课程提醒");
        
        List<Member> activeMembers = memberRepository.findByStatus(Member.MemberStatus.ACTIVE);
        
        for (Member member : activeMembers) {
            sendWeeklyReminder(member);
        }
        
        logger.info("每周课程提醒发送完成，共发送 {} 条提醒", activeMembers.size());
    }

    private void sendWeeklyReminder(Member member) {
        logger.info("发送每周课程提醒 - 会员ID: {}, 姓名: {}", member.getId(), member.getName());
        
        String message = String.format(
                "尊敬的会员 %s，新的一周开始了！记得查看本周课程安排，积极参与健身活动，保持健康的生活方式。",
                member.getName()
        );
        
        sendNotification(member, "每周课程提醒", message);
    }

    private void sendNotification(Member member, String title, String content) {
        logger.info("发送通知 - 会员ID: {}, 标题: {}, 内容: {}", 
                member.getId(), title, content);
        
        logger.info("通知已发送至会员 {} (手机: {})", 
                member.getName(), member.getPhone());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void dailyMaintenance() {
        logger.info("开始执行每日维护任务");
        
        logger.info("每日维护任务执行完成");
    }
}
