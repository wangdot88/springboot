package dangod.springboot.controller.sports;

import dangod.springboot.core.common.Result;
import dangod.springboot.entity.Student;
import dangod.springboot.entity.sports.RunningRecord;
import dangod.springboot.entity.sports.SportsDailyReport;
import dangod.springboot.service.sports.RunningRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sports")
public class RunningRecordController {

    @Autowired
    private RunningRecordService runningRecordService;

    @PostMapping("/checkin")
    public Result<RunningRecord> checkIn(
            @RequestParam("studentId") Long studentId,
            @RequestParam("distance") Double distance,
            @RequestParam(value = "location", required = false) String location) {
        RunningRecord record = runningRecordService.checkIn(studentId, distance, location);
        return Result.success("打卡成功", record);
    }

    @GetMapping("/ranking/student")
    public Result<List<Map<String, Object>>> getStudentRanking(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> ranking = runningRecordService.getStudentRanking(startDate, endDate);
        return Result.success(ranking);
    }

    @GetMapping("/ranking/class")
    public Result<List<Map<String, Object>>> getClassRanking(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<Map<String, Object>> ranking = runningRecordService.getClassRanking(startDate, endDate);
        return Result.success(ranking);
    }

    @GetMapping("/not-checked-in")
    public Result<List<Student>> getNotCheckedInStudents(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(value = "classId", required = false) Long classId) {
        List<Student> students = runningRecordService.getNotCheckedInStudents(date, classId);
        return Result.success(students);
    }

    @GetMapping("/daily-reports")
    public Result<List<SportsDailyReport>> getDailyReports(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<SportsDailyReport> reports = runningRecordService.getDailyReports(startDate, endDate);
        return Result.success(reports);
    }

    @GetMapping("/export/daily")
    public ResponseEntity<byte[]> exportDailyReport(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) throws IOException {
        byte[] data = runningRecordService.exportDailyReport(date);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "sports_report_" + date + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<RunningRecord>> getStudentRecords(
            @PathVariable Long studentId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        List<RunningRecord> records = runningRecordService.getStudentRecords(studentId, startDate, endDate);
        return Result.success(records);
    }

    @PostMapping("/generate-report")
    public Result<Void> generateDailyReport(
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        runningRecordService.generateDailyReport(date);
        return Result.success("报告生成成功", null);
    }

    @PostMapping("/send-reminder")
    public Result<Void> sendReminder(
            @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        runningRecordService.sendReminder(date);
        return Result.success("提醒已发送", null);
    }
}
