package dangod.springboot.service.sports;

import dangod.springboot.core.common.BusinessException;
import dangod.springboot.entity.Clazz;
import dangod.springboot.entity.Student;
import dangod.springboot.entity.sports.RunningRecord;
import dangod.springboot.entity.sports.SportsDailyReport;
import dangod.springboot.repository.ClazzRepository;
import dangod.springboot.repository.StudentRepository;
import dangod.springboot.repository.sports.RunningRecordRepository;
import dangod.springboot.repository.sports.SportsDailyReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class RunningRecordService {

    @Autowired
    private RunningRecordRepository runningRecordRepository;

    @Autowired
    private SportsDailyReportRepository dailyReportRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClazzRepository clazzRepository;

    @Transactional
    public RunningRecord checkIn(Long studentId, Double distance, String location) {
        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (!studentOpt.isPresent()) {
            throw new BusinessException("学生不存在");
        }

        Student student = studentOpt.get();
        LocalDate today = LocalDate.now();

        List<RunningRecord> existingRecords = runningRecordRepository.findByStudentIdAndRecordDate(studentId, today);
        if (!existingRecords.isEmpty()) {
            throw new BusinessException("今日已打卡，请勿重复提交");
        }

        RunningRecord record = new RunningRecord();
        record.setStudent(student);
        record.setRecordDate(today);
        record.setStartTime(LocalTime.now());
        record.setDistance(distance);
        record.setLocation(location);
        record.setStatus("已完成");

        if (distance != null && distance > 0) {
            record.setDuration((int) (distance * 5 * 60));
            record.setAvgPace(5.0);
            record.setCalories((int) (distance * 60));
        }

        runningRecordRepository.save(record);
        generateDailyReport(today);

        return record;
    }

    @Transactional
    public void generateDailyReport(LocalDate date) {
        List<Clazz> classes = clazzRepository.findAll();
        for (Clazz clazz : classes) {
            List<RunningRecord> records = runningRecordRepository.findByClassIdAndRecordDate(clazz.getId(), date);
            Set<Long> checkedInStudents = new HashSet<>();
            double totalDistance = 0;

            for (RunningRecord record : records) {
                checkedInStudents.add(record.getStudent().getId());
                if (record.getDistance() != null) {
                    totalDistance += record.getDistance();
                }
            }

            SportsDailyReport report = dailyReportRepository.findByClazzIdAndReportDate(clazz.getId(), date);
            if (report == null) {
                report = new SportsDailyReport();
                report.setClazz(clazz);
                report.setReportDate(date);
            }

            int totalStudents = clazz.getStudentCount();
            int checkedIn = checkedInStudents.size();

            report.setTotalStudents(totalStudents);
            report.setCheckedInCount(checkedIn);
            report.setNotCheckedInCount(totalStudents - checkedIn);
            report.setTotalDistance(totalDistance);
            report.setAvgDistance(checkedIn > 0 ? totalDistance / checkedIn : 0);
            report.setCheckInRate(totalStudents > 0 ? (checkedIn * 100.0 / totalStudents) : 0);

            dailyReportRepository.save(report);
        }
    }

    public List<Map<String, Object>> getStudentRanking(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = runningRecordRepository.findDistanceRanking(startDate, endDate);
        List<Map<String, Object>> ranking = new ArrayList<>();

        int rank = 1;
        for (Object[] row : results) {
            Long studentId = (Long) row[0];
            Double totalDistance = (Double) row[1];

            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                Map<String, Object> item = new HashMap<>();
                item.put("rank", rank++);
                item.put("studentId", studentId);
                item.put("studentNo", student.getStudentNo());
                item.put("name", student.getName());
                item.put("className", student.getClazz() != null ? student.getClazz().getClassName() : "");
                item.put("totalDistance", totalDistance);
                ranking.add(item);
            }
        }

        return ranking;
    }

    public List<Map<String, Object>> getClassRanking(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = runningRecordRepository.findClassDistanceRanking(startDate, endDate);
        List<Map<String, Object>> ranking = new ArrayList<>();

        int rank = 1;
        for (Object[] row : results) {
            Long classId = (Long) row[0];
            Double totalDistance = (Double) row[1];

            Optional<Clazz> clazzOpt = clazzRepository.findById(classId);
            if (clazzOpt.isPresent()) {
                Clazz clazz = clazzOpt.get();
                Map<String, Object> item = new HashMap<>();
                item.put("rank", rank++);
                item.put("classId", classId);
                item.put("className", clazz.getClassName());
                item.put("grade", clazz.getGrade());
                item.put("totalDistance", totalDistance);
                item.put("avgDistance", clazz.getStudentCount() > 0 ? totalDistance / clazz.getStudentCount() : 0);
                ranking.add(item);
            }
        }

        return ranking;
    }

    public List<Student> getNotCheckedInStudents(LocalDate date, Long classId) {
        List<Student> allStudents;
        if (classId != null) {
            allStudents = studentRepository.findByClazzId(classId);
        } else {
            allStudents = studentRepository.findAll();
        }

        List<RunningRecord> records = runningRecordRepository.findByRecordDate(date);
        Set<Long> checkedInIds = new HashSet<>();
        for (RunningRecord record : records) {
            checkedInIds.add(record.getStudent().getId());
        }

        List<Student> notCheckedIn = new ArrayList<>();
        for (Student student : allStudents) {
            if (!checkedInIds.contains(student.getId())) {
                notCheckedIn.add(student);
            }
        }

        return notCheckedIn;
    }

    public List<SportsDailyReport> getDailyReports(LocalDate startDate, LocalDate endDate) {
        return dailyReportRepository.findByReportDateBetween(startDate, endDate);
    }

    public byte[] exportDailyReport(LocalDate date) throws IOException {
        List<SportsDailyReport> reports = dailyReportRepository.findByReportDate(date);

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("运动报告-" + date);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"班级", "年级", "总人数", "已打卡", "未打卡", "打卡率(%)", "总里程(km)", "平均里程(km)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (SportsDailyReport report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getClazz().getClassName());
                row.createCell(1).setCellValue(report.getClazz().getGrade());
                row.createCell(2).setCellValue(report.getTotalStudents());
                row.createCell(3).setCellValue(report.getCheckedInCount());
                row.createCell(4).setCellValue(report.getNotCheckedInCount());
                row.createCell(5).setCellValue(String.format("%.2f", report.getCheckInRate()));
                row.createCell(6).setCellValue(String.format("%.2f", report.getTotalDistance()));
                row.createCell(7).setCellValue(String.format("%.2f", report.getAvgDistance()));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public List<RunningRecord> getStudentRecords(Long studentId, LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return runningRecordRepository.findByDateRange(startDate, endDate);
        }
        return runningRecordRepository.findByStudentId(studentId);
    }

    public void sendReminder(LocalDate date) {
        List<Clazz> classes = clazzRepository.findAll();
        for (Clazz clazz : classes) {
            List<Student> notCheckedIn = getNotCheckedInStudents(date, clazz.getId());
            if (!notCheckedIn.isEmpty()) {
                log.info("提醒班级{}：以下{}名学生今日未完成运动打卡：{}",
                        clazz.getClassName(),
                        notCheckedIn.size(),
                        notCheckedIn.stream().map(Student::getName).reduce((a, b) -> a + ", " + b).orElse(""));
            }
        }
    }
}
