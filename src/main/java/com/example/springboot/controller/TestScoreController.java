package com.example.springboot.controller;

import com.example.springboot.entity.Student;
import com.example.springboot.service.TestScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test-scores")
@CrossOrigin(origins = "*")
public class TestScoreController {
    
    @Autowired
    private TestScoreService testScoreService;
    
    @PostMapping("/import")
    public ResponseEntity<String> importTestScores(@RequestParam("file") MultipartFile file) {
        try {
            testScoreService.importTestScores(file);
            return ResponseEntity.ok("体测成绩导入成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("导入失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/report/{className}")
    public ResponseEntity<Map<String, Object>> getClassReport(@PathVariable String className) {
        return ResponseEntity.ok(testScoreService.getClassQualificationReport(className));
    }
    
    @GetMapping("/weak-students")
    public ResponseEntity<List<Student>> getWeakStudents() {
        return ResponseEntity.ok(testScoreService.getWeakStudents());
    }
}
