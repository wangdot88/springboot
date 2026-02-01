package dangod.springboot.controller;

import dangod.springboot.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/physical-test")
    public ResponseEntity<?> exportPhysicalTestReport(@RequestParam String className, 
                                                      @RequestParam String testType) {
        try {
            byte[] reportData = reportService.exportPhysicalTestReport(className, testType);
            
            String filename = className + "_" + testType + "_体测报告.xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportData.length);
            
            return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("导出体测报告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/daily-exercise")
    public ResponseEntity<?> exportDailyExerciseReport(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            byte[] reportData = reportService.exportDailyExerciseReport(date);
            
            String filename = date + "_每日运动报告.xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportData.length);
            
            return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("导出每日运动报告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/equipment-annual")
    public ResponseEntity<?> exportEquipmentAnnualReport(@RequestParam int year) {
        try {
            byte[] reportData = reportService.exportEquipmentAnnualReport(year);
            
            String filename = year + "_器材使用报告.xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportData.length);
            
            return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("导出器材年度报告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/weak-students")
    public ResponseEntity<?> exportWeakStudentsReport() {
        try {
            byte[] reportData = reportService.exportWeakStudentsReport();
            
            String filename = "体弱学生报告.xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(reportData.length);
            
            return new ResponseEntity<>(reportData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("导出体弱学生报告失败: " + e.getMessage());
        }
    }
}