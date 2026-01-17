package dangod.springboot.task;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SchedulerInitializer implements CommandLineRunner {

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(String... args) throws Exception {
        scheduleMemberExpireReminder();
        scheduleBodyTestAnomalyReminder();
        
        System.out.println("定时任务已初始化");
    }

    private void scheduleMemberExpireReminder() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(MemberExpireReminderJob.class)
                .withIdentity("memberExpireReminderJob", "reminderGroup")
                .build();
        
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("memberExpireReminderTrigger", "reminderGroup")
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(9, 0))
                .build();
        
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void scheduleBodyTestAnomalyReminder() throws SchedulerException {
        JobDetail jobDetail = JobBuilder.newJob(BodyTestAnomalyReminderJob.class)
                .withIdentity("bodyTestAnomalyReminderJob", "reminderGroup")
                .build();
        
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("bodyTestAnomalyReminderTrigger", "reminderGroup")
                .withSchedule(CronScheduleBuilder.weeklyOnDayAndHourAndMinute(DateBuilder.MONDAY, 10, 0))
                .build();
        
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
