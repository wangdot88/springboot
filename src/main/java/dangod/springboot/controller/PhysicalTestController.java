package dangod.springboot.controller;

import dangod.springboot.entity.PhysicalTest;
import dangod.springboot.entity.Student;
import dangod.springboot.service.PhysicalTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/physical-test")
public class PhysicalTestController {
    
    @Autowired
    private PhysicalTestService physicalTestService;
    
    @PostMapping("/import")
    public ResponseEntity<?> importPhysicalTest(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件不能为空");
            }
            
            List<PhysicalTest> physicalTests = physicalTestService.importFromExcel(file);
            return ResponseEntity.ok(physicalTests);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("导入失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/class-report/{className}")
    public ResponseEntity<?> generateClassReport(@PathVariable String className, @RequestParam String testType) {
        try {
            Map<String, Object> report = physicalTestService.generateClassReport(className, testType);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("生成报告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/weak-students")
    public ResponseEntity<?> getWeakStudents() {
        try {
            List<Student> weakStudents = physicalTestService.getWeakStudents();
            return ResponseEntity.ok(weakStudents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取体弱学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/weak-students/{className}")
    public ResponseEntity<?> getWeakStudentsByClass(@PathVariable String className) {
        try {
            List<Student> weakStudents = physicalTestService.getWeakStudentsByClass(className);
            return ResponseEntity.ok(weakStudents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取班级体弱学生失败: " + e.getMessage());
        }
    }
}