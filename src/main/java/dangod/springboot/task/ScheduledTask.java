package dangod.springboot.task;

import dangod.springboot.entity.EquipmentBorrow;
import dangod.springboot.entity.ExerciseRecord;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.EquipmentBorrowRepository;
import dangod.springboot.repository.EquipmentRepository;
import dangod.springboot.repository.ExerciseRecordRepository;
import dangod.springboot.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private EquipmentBorrowRepository equipmentBorrowRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Scheduled(cron = "0 0 20 * * ?")
    public void checkOverdueBorrows() {
        logger.info("开始检查超时未归还的器材...");
        List<EquipmentBorrow> overdueBorrows = equipmentBorrowRepository.findOverdueBorrows(new Date());
        
        for (EquipmentBorrow borrow : overdueBorrows) {
            if (!borrow.getIsOverdue()) {
                borrow.setIsOverdue(true);
                equipmentBorrowRepository.save(borrow);
                logger.warn("器材超时提醒：学生 {} 借用的器材 {} 已超时", 
                    borrow.getStudent().getName(), borrow.getEquipment().getName());
            }
        }
        
        logger.info("器材超时检查完成，共发现 {} 条超时记录", overdueBorrows.size());
    }

    @Scheduled(cron = "0 0 21 * * ?")
    public void checkInactiveStudents() {
        logger.info("开始检查未运动的学生...");
        Date today = new Date();
        
        List<Student> allStudents = studentRepository.findAll();
        int inactiveCount = 0;
        
        for (Student student : allStudents) {
            if (student.getClassInfo() == null) continue;
            
            List<ExerciseRecord> records = exerciseRecordRepository.findByStudentIdAndRecordDate(student.getId(), today);
            if (records.isEmpty()) {
                inactiveCount++;
                logger.info("未运动提醒：学生 {} ({}) 今日未运动", student.getName(), student.getStudentNo());
            }
        }
        
        logger.info("未运动检查完成，共有 {} 名学生今日未运动", inactiveCount);
    }

    @Scheduled(cron = "0 0 22 * * ?")
    public void generateDailyExerciseReport() {
        logger.info("开始生成每日运动报告...");
        Date today = new Date();
        
        List<Object[]> rankings = exerciseRecordRepository.findDailyRanking(today);
        logger.info("今日运动排行榜前3名：");
        
        for (int i = 0; i < Math.min(3, rankings.size()); i++) {
            Object[] row = rankings.get(i);
            Long studentId = (Long) row[0];
            Double totalDistance = (Double) row[1];
            
            Student student = studentRepository.findOne(studentId);
            if (student != null) {
                logger.info("第{}名：{} ({}) - 距离：{} 米", 
                    i + 1, student.getName(), student.getStudentNo(), totalDistance);
            }
        }
        
        logger.info("每日运动报告生成完成");
    }

    @Scheduled(cron = "0 0 23 * * ?")
    public void checkLowStockEquipment() {
        logger.info("开始检查库存不足的器材...");
        
        List<Student> allStudents = studentRepository.findAll();
        int inactiveCount = 0;
        
        for (Student student : allStudents) {
            if (student.getClassInfo() == null) continue;
            
            List<ExerciseRecord> records = exerciseRecordRepository.findByStudentIdAndRecordDate(student.getId(), new Date());
            if (records.isEmpty()) {
                inactiveCount++;
            }
        }
        
        logger.info("未运动检查完成，共有 {} 名学生今日未运动", inactiveCount);
    }

    @Scheduled(cron = "0 0 9 * * MON")
    public void generateWeeklyExerciseReport() {
        logger.info("开始生成每周运动报告...");
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        Date endDate = calendar.getTime();
        
        List<Object[]> rankings = exerciseRecordRepository.findPeriodRanking(startDate, endDate);
        logger.info("本周运动排行榜前5名：");
        
        for (int i = 0; i < Math.min(5, rankings.size()); i++) {
            Object[] row = rankings.get(i);
            Long studentId = (Long) row[0];
            Double totalDistance = (Double) row[1];
            
            Student student = studentRepository.findOne(studentId);
            if (student != null) {
                logger.info("第{}名：{} ({}) - 总距离：{} 米", 
                    i + 1, student.getName(), student.getStudentNo(), totalDistance);
            }
        }
        
        logger.info("每周运动报告生成完成");
    }
}
