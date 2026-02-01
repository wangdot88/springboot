package dangod.springboot.service;

import dangod.springboot.entity.*;
import dangod.springboot.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {
    
    @Autowired
    private PhysicalTestRepository physicalTestRepository;
    
    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;
    
    @Autowired
    private EquipmentBorrowRepository equipmentBorrowRepository;
    
    @Autowired
    private EquipmentRepository equipmentRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public byte[] exportPhysicalTestReport(String className, String testType) throws IOException {
        List<PhysicalTest> tests = physicalTestRepository.findByClassAndTestType(className, testType);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(className + " - " + testType + "体测报告");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "性别", "总分", "等级", "50米跑", "坐位体前屈", "立定跳远", "引体向上", "仰卧起坐", "800米跑", "1000米跑", "测试日期"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (int i = 0; i < tests.size(); i++) {
                Row row = sheet.createRow(i + 1);
                PhysicalTest test = tests.get(i);
                Student student = test.getStudent();
                
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getClassName());
                row.createCell(3).setCellValue(student.getGender());
                row.createCell(4).setCellValue(test.getScore());
                row.createCell(5).setCellValue(test.getGrade());
                row.createCell(6).setCellValue(test.getRun50m() != null ? test.getRun50m() : 0);
                row.createCell(7).setCellValue(test.getSitAndReach() != null ? test.getSitAndReach() : 0);
                row.createCell(8).setCellValue(test.getLongJump() != null ? test.getLongJump() : 0);
                row.createCell(9).setCellValue(test.getPullUp() != null ? test.getPullUp() : 0);
                row.createCell(10).setCellValue(test.getSitUp() != null ? test.getSitUp() : 0);
                row.createCell(11).setCellValue(test.getRun800m() != null ? test.getRun800m() : 0);
                row.createCell(12).setCellValue(test.getRun1000m() != null ? test.getRun1000m() : 0);
                row.createCell(13).setCellValue(test.getTestDate() != null ? test.getTestDate().format(dateFormatter) : "");
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public byte[] exportDailyExerciseReport(LocalDate date) throws IOException {
        LocalDateTime startOfDay = LocalDateTime.of(date, java.time.LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, java.time.LocalTime.MAX);
        
        List<ExerciseRecord> records = exerciseRecordRepository.findByDateRange(startOfDay, endOfDay);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(date + " - 每日运动报告");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "运动类型", "距离(米)", "时长(分钟)", "消耗卡路里", "运动地点", "运动时间"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (int i = 0; i < records.size(); i++) {
                Row row = sheet.createRow(i + 1);
                ExerciseRecord record = records.get(i);
                Student student = record.getStudent();
                
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getClassName());
                row.createCell(3).setCellValue(record.getExerciseType());
                row.createCell(4).setCellValue(record.getDistance() != null ? record.getDistance() : 0);
                row.createCell(5).setCellValue(record.getDuration() != null ? record.getDuration() : 0);
                row.createCell(6).setCellValue(record.getCalories() != null ? record.getCalories() : 0);
                row.createCell(7).setCellValue(record.getLocation() != null ? record.getLocation() : "");
                row.createCell(8).setCellValue(record.getExerciseDate() != null ? record.getExerciseDate().format(dateFormatter) : "");
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public byte[] exportEquipmentAnnualReport(int year) throws IOException {
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59);
        
        List<EquipmentBorrow> borrows = equipmentBorrowRepository.findByBorrowDateRange(startOfYear, endOfYear);
        List<Equipment> equipment = equipmentRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(year + "年 - 器材使用报告");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"器材编码", "器材名称", "类别", "品牌", "型号", "总数量", "当前可用", "借用次数", "总借用天数", "维修次数"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            for (int i = 0; i < equipment.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Equipment equip = equipment.get(i);
                
                row.createCell(0).setCellValue(equip.getEquipmentCode());
                row.createCell(1).setCellValue(equip.getName());
                row.createCell(2).setCellValue(equip.getCategory());
                row.createCell(3).setCellValue(equip.getBrand());
                row.createCell(4).setCellValue(equip.getModel());
                row.createCell(5).setCellValue(equip.getTotalQuantity());
                row.createCell(6).setCellValue(equip.getAvailableQuantity());
                
                int borrowCount = 0;
                long totalBorrowDays = 0;
                
                for (EquipmentBorrow borrow : borrows) {
                    if (borrow.getEquipment().getEquipmentCode().equals(equip.getEquipmentCode())) {
                        borrowCount++;
                        if (borrow.getActualReturnDate() != null) {
                            totalBorrowDays += java.time.Duration.between(borrow.getBorrowDate(), borrow.getActualReturnDate()).toDays();
                        }
                    }
                }
                
                row.createCell(7).setCellValue(borrowCount);
                row.createCell(8).setCellValue(totalBorrowDays);
                row.createCell(9).setCellValue(equip.getRepairQuantity());
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    public byte[] exportWeakStudentsReport() throws IOException {
        List<Student> weakStudents = studentRepository.findByIsWeakPhysical(true);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("体弱学生报告");
            
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "性别", "年龄", "专业", "电话", "邮箱"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            for (int i = 0; i < weakStudents.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Student student = weakStudents.get(i);
                
                row.createCell(0).setCellValue(student.getStudentId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getClassName());
                row.createCell(3).setCellValue(student.getGender());
                row.createCell(4).setCellValue(student.getAge());
                row.createCell(5).setCellValue(student.getMajor());
                row.createCell(6).setCellValue(student.getPhone());
                row.createCell(7).setCellValue(student.getEmail() != null ? student.getEmail() : "");
            }
            
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
}