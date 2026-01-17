package dangod.springboot.service;

import dangod.springboot.model.entity.RunningRecord;
import dangod.springboot.model.entity.Student;
import dangod.springboot.repository.RunningRecordRepository;
import dangod.springboot.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RunningService {
    @Autowired
    private RunningRecordRepository runningRecordRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Transactional
    public RunningRecord addRunningRecord(RunningRecord record) {
        Optional<Student> studentOpt = studentRepository.findById(record.getStudentId());
        if (!studentOpt.isPresent()) {
            throw new RuntimeException("学生不存在");
        }
        
        Student student = studentOpt.get();
        if (student.getIsWeak()) {
            throw new RuntimeException("体弱学生不强制要求跑步打卡");
        }
        
        Optional<RunningRecord> existing = runningRecordRepository.findByStudentIdAndRecordDate(
                record.getStudentId(), record.getRecordDate());
        if (existing.isPresent()) {
            throw new RuntimeException("今日已打卡，无需重复打卡");
        }
        
        if (record.getDuration() != null && record.getDistance() != null && record.getDistance().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal speed = record.getDistance().multiply(new BigDecimal(60))
                    .divide(new BigDecimal(record.getDuration()), 2, RoundingMode.HALF_UP);
            record.setSpeed(speed);
        }
        
        return runningRecordRepository.save(record);
    }
    
    public List<Map<String, Object>> getDailyRanking(Date recordDate) {
        List<Object[]> results = runningRecordRepository.findDailyRanking(recordDate);
        List<Map<String, Object>> ranking = new ArrayList<>();
        
        int rank = 1;
        for (Object[] result : results) {
            Long studentId = ((Number) result[0]).longValue();
            Double totalDistance = (Double) result[1];
            
            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                Map<String, Object> item = new HashMap<>();
                item.put("rank", rank++);
                item.put("studentId", studentId);
                item.put("studentName", student.getName());
                item.put("studentNo", student.getStudentNo());
                item.put("className", student.getClassName());
                item.put("totalDistance", totalDistance);
                ranking.add(item);
            }
        }
        
        return ranking;
    }
    
    public List<Map<String, Object>> getClassRanking(Date recordDate, String className) {
        List<RunningRecord> records = runningRecordRepository.findByClassNameAndRecordDate(className, recordDate);
        
        return records.stream()
                .sorted((a, b) -> b.getDistance().compareTo(a.getDistance()))
                .map(record -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("studentId", record.getStudentId());
                    item.put("studentName", record.getStudentName());
                    item.put("studentNo", record.getStudentNo());
                    item.put("distance", record.getDistance());
                    item.put("duration", record.getDuration());
                    item.put("speed", record.getSpeed());
                    return item;
                })
                .collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getStudentsNotExercised(Date recordDate) {
        List<Object[]> results = runningRecordRepository.findStudentsNotExercised(recordDate);
        List<Map<String, Object>> list = new ArrayList<>();
        
        for (Object[] result : results) {
            Long studentId = ((Number) result[0]).longValue();
            String studentName = (String) result[1];
            String className = (String) result[2];
            
            Map<String, Object> item = new HashMap<>();
            item.put("studentId", studentId);
            item.put("studentName", studentName);
            item.put("className", className);
            item.put("reminder", "今日未完成运动打卡，请尽快完成");
            list.add(item);
        }
        
        return list;
    }
    
    public Map<String, Object> getStudentWeeklyStats(Long studentId, Date startDate, Date endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        List<RunningRecord> records = runningRecordRepository.findByStudentIdAndRecordDateBetween(studentId, startDate, endDate);
        Double totalDistance = runningRecordRepository.findTotalDistanceByStudentAndDateRange(studentId, startDate, endDate);
        
        stats.put("studentId", studentId);
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        studentOpt.ifPresent(s -> {
            stats.put("studentName", s.getName());
            stats.put("studentNo", s.getStudentNo());
        });
        
        stats.put("totalRunningDays", records.size());
        stats.put("totalDistance", totalDistance != null ? totalDistance : 0);
        stats.put("avgDistance", records.size() > 0 ? (totalDistance / records.size()) : 0);
        stats.put("records", records);
        
        return stats;
    }
    
    public List<RunningRecord> getDailyRecords(Date recordDate) {
        List<RunningRecord> records = runningRecordRepository.findByRecordDate(recordDate);
        records.forEach(r -> {
            studentRepository.findById(r.getStudentId()).ifPresent(s -> {
                r.setStudentName(s.getName());
                r.setStudentNo(s.getStudentNo());
                r.setClassName(s.getClassName());
            });
        });
        return records;
    }
    
    public List<RunningRecord> getStudentRecords(Long studentId) {
        return runningRecordRepository.findByStudentId(studentId);
    }
    
    public byte[] exportDailyReport(Date recordDate) throws IOException {
        List<RunningRecord> records = getDailyRecords(recordDate);
        List<Map<String, Object>> notExercised = getStudentsNotExercised(recordDate);
        
        Workbook workbook = new XSSFWorkbook();
        
        Sheet summarySheet = workbook.createSheet("汇总统计");
        createSummarySheet(summarySheet, records, notExercised, recordDate);
        
        Sheet detailSheet = workbook.createSheet("打卡详情");
        createDetailSheet(detailSheet, records);
        
        Sheet reminderSheet = workbook.createSheet("未打卡提醒");
        createReminderSheet(reminderSheet, notExercised);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos.toByteArray();
    }
    
    private void createSummarySheet(Sheet sheet, List<RunningRecord> records, List<Map<String, Object>> notExercised, Date date) {
        String[] headers = {"日期", "总打卡人数", "总跑步距离(km)", "平均距离(km)", "未打卡人数", "打卡完成率"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        double totalDistance = records.stream()
                .filter(r -> r.getDistance() != null)
                .mapToDouble(r -> r.getDistance().doubleValue())
                .sum();
        double avgDistance = records.size() > 0 ? totalDistance / records.size() : 0;
        int totalStudents = studentRepository.findAll().size() - studentRepository.findByIsWeak(true).size();
        double completionRate = totalStudents > 0 ? (double) records.size() / totalStudents * 100 : 0;
        
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue(sdf.format(date));
        dataRow.createCell(1).setCellValue(records.size());
        dataRow.createCell(2).setCellValue(String.format("%.2f", totalDistance));
        dataRow.createCell(3).setCellValue(String.format("%.2f", avgDistance));
        dataRow.createCell(4).setCellValue(notExercised.size());
        dataRow.createCell(5).setCellValue(String.format("%.2f%%", completionRate));
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createDetailSheet(Sheet sheet, List<RunningRecord> records) {
        String[] headers = {"学号", "姓名", "班级", "距离(km)", "时长(分钟)", "速度(km/h)", "步数", "地点"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        int rowNum = 1;
        for (RunningRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getStudentNo());
            row.createCell(1).setCellValue(record.getStudentName());
            row.createCell(2).setCellValue(record.getClassName());
            row.createCell(3).setCellValue(record.getDistance() != null ? record.getDistance().toString() : "");
            row.createCell(4).setCellValue(record.getDuration() != null ? record.getDuration().toString() : "");
            row.createCell(5).setCellValue(record.getSpeed() != null ? record.getSpeed().toString() : "");
            row.createCell(6).setCellValue(record.getSteps() != null ? record.getSteps().toString() : "");
            row.createCell(7).setCellValue(record.getLocation() != null ? record.getLocation() : "");
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
    
    private void createReminderSheet(Sheet sheet, List<Map<String, Object>> notExercised) {
        String[] headers = {"学号", "姓名", "班级", "提醒内容"};
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        int rowNum = 1;
        for (Map<String, Object> item : notExercised) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) item.get("studentNo"));
            row.createCell(1).setCellValue((String) item.get("studentName"));
            row.createCell(2).setCellValue((String) item.get("className"));
            row.createCell(3).setCellValue((String) item.get("reminder"));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
