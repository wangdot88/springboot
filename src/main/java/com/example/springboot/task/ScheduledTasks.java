package com.example.springboot.task;

import com.example.springboot.entity.EquipmentBorrow;
import com.example.springboot.entity.Student;
import com.example.springboot.repository.EquipmentBorrowRepository;
import com.example.springboot.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EquipmentBorrowRepository equipmentBorrowRepository;
    
    @Scheduled(cron = "0 0 20 * * ?")
    public void checkAndRemindInactiveStudents() {
        logger.info("开始执行未运动学生提醒任务...");
        
        List<Student> inactiveStudents = studentRepository.findStudentsWithoutExercise(LocalDate.now());
        
        if (!inactiveStudents.isEmpty()) {
            logger.info("今日未运动学生名单:");
            for (Student student : inactiveStudents) {
                logger.info("学号: {}, 姓名: {}, 班级: {}", 
                        student.getStudentNo(), student.getName(), student.getClassName());
            }
            
            sendReminder(inactiveStudents, "今日运动提醒", "同学你好，今天还没有运动记录哦，记得坚持锻炼！");
        } else {
            logger.info("今日所有学生都已完成运动打卡");
        }
    }
    
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkAndRemindOverdueEquipments() {
        logger.info("开始执行器材超时提醒任务...");
        
        List<EquipmentBorrow> overdueBorrows = equipmentBorrowRepository.findOverdueBorrows(LocalDateTime.now());
        
        if (!overdueBorrows.isEmpty()) {
            logger.info("超时未归还的器材记录:");
            for (EquipmentBorrow borrow : overdueBorrows) {
                if (!borrow.getIsOverdue()) {
                    borrow.setIsOverdue(true);
                    equipmentBorrowRepository.save(borrow);
                }
                
                logger.info("借用人: {}, 器材: {}, 应归还日期: {}", 
                        borrow.getStudent().getName(),
                        borrow.getEquipment().getName(),
                        borrow.getExpectedReturnTime());
            }
            
            for (EquipmentBorrow borrow : overdueBorrows) {
                sendSingleReminder(borrow.getStudent(), 
                        "器材归还提醒", 
                        "同学你好，你借用的\"" + borrow.getEquipment().getName() + "\"已超时，请尽快归还！");
            }
        } else {
            logger.info("没有超时未归还的器材");
        }
    }
    
    private void sendReminder(List<Student> students, String title, String message) {
        for (Student student : students) {
            sendSingleReminder(student, title, message);
        }
    }
    
    private void sendSingleReminder(Student student, String title, String message) {
        logger.info("发送提醒给 {} ({}): {}", student.getName(), student.getPhone(), message);
    }
}
