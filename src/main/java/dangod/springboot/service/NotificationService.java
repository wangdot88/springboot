package dangod.springboot.service;

import dangod.springboot.entity.Equipment;
import dangod.springboot.entity.EquipmentBorrow;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.EquipmentBorrowRepository;
import dangod.springboot.repository.EquipmentRepository;
import dangod.springboot.repository.ExerciseRecordRepository;
import dangod.springboot.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private EquipmentBorrowRepository equipmentBorrowRepository;
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkOverdueEquipmentBorrows() {
        logger.info("开始检查逾期器材借用记录");
        
        List<EquipmentBorrow> overdueBorrows = equipmentBorrowRepository.findOverdueBorrows(LocalDateTime.now());
        
        if (!overdueBorrows.isEmpty()) {
            logger.warn("发现 {} 条逾期借用记录", overdueBorrows.size());
            
            for (EquipmentBorrow borrow : overdueBorrows) {
                logger.warn("逾期借用: 学生 {} 借用器材 {} 已逾期 {} 天", 
                        borrow.getStudent().getName(),
                        borrow.getEquipment().getName(),
                        java.time.Duration.between(borrow.getExpectedReturnDate(), LocalDateTime.now()).toDays());
            }
        } else {
            logger.info("没有逾期借用记录");
        }
    }
    
    @Scheduled(cron = "0 0 10 * * ?")
    public void checkLowStockEquipment() {
        logger.info("开始检查库存不足器材");
        
        List<Equipment> lowStockEquipment = equipmentRepository.findLowStockEquipment();
        
        if (!lowStockEquipment.isEmpty()) {
            logger.warn("发现 {} 种库存不足器材", lowStockEquipment.size());
            
            for (Equipment equipment : lowStockEquipment) {
                logger.warn("库存不足: 器材 {} 当前库存 {} 低于预警值 {}", 
                        equipment.getName(),
                        equipment.getAvailableQuantity(),
                        equipment.getMinStockAlert());
            }
        } else {
            logger.info("没有库存不足的器材");
        }
    }
    
    @Scheduled(cron = "0 0 20 * * ?")
    public void checkStudentsWithoutExercise() {
        logger.info("开始检查今日未运动学生");
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = LocalDateTime.of(today, java.time.LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(today, java.time.LocalTime.MAX);
        
        List<String> exercisedStudentIds = exerciseRecordRepository.findStudentsWhoExercisedOnDate(startOfDay);
        List<Student> allStudents = studentRepository.findAll();
        
        List<Student> nonExercisedStudents = allStudents.stream()
                .filter(student -> !exercisedStudentIds.contains(student.getStudentId()))
                .collect(java.util.stream.Collectors.toList());
        
        if (!nonExercisedStudents.isEmpty()) {
            logger.warn("今日有 {} 名学生未运动", nonExercisedStudents.size());
            
            for (Student student : nonExercisedStudents) {
                logger.warn("未运动学生: {} ({})", student.getName(), student.getClassName());
            }
        } else {
            logger.info("所有学生今日已完成运动");
        }
    }
    
    @Scheduled(cron = "0 0 9 ? * MON")
    public void checkWeeklyExerciseStatus() {
        logger.info("开始检查上周运动情况");
        
        LocalDate lastWeekStart = LocalDate.now().minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDateTime startOfWeek = LocalDateTime.of(lastWeekStart, java.time.LocalTime.MIN);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).with(java.time.LocalTime.MAX);
        
        List<String> exercisedStudentIds = exerciseRecordRepository.findStudentsWhoExercisedInDateRange(startOfWeek, endOfWeek);
        List<Student> allStudents = studentRepository.findAll();
        
        List<Student> nonExercisedStudents = allStudents.stream()
                .filter(student -> !exercisedStudentIds.contains(student.getStudentId()))
                .collect(java.util.stream.Collectors.toList());
        
        if (!nonExercisedStudents.isEmpty()) {
            logger.warn("上周有 {} 名学生未运动", nonExercisedStudents.size());
            
            for (Student student : nonExercisedStudents) {
                logger.warn("上周未运动学生: {} ({})", student.getName(), student.getClassName());
            }
        } else {
            logger.info("所有学生上周都有运动记录");
        }
    }
    
    @Scheduled(cron = "0 0 8 1 * ?")
    public void generateMonthlyReport() {
        logger.info("开始生成月度报告");
        
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();
        
        logger.info("生成 {} 年 {} 月的运动报告", year, month);
        
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        
        LocalDateTime startOfMonth = LocalDateTime.of(firstDay, java.time.LocalTime.MIN);
        LocalDateTime endOfMonth = LocalDateTime.of(lastDay, java.time.LocalTime.MAX);
        
        long totalExerciseRecords = exerciseRecordRepository.findByDateRange(startOfMonth, endOfMonth).size();
        long distinctStudents = exerciseRecordRepository.countDistinctStudentsByDate(startOfMonth);
        
        logger.info("{} 年 {} 月共有 {} 条运动记录，涉及 {} 名学生", year, month, totalExerciseRecords, distinctStudents);
    }
}