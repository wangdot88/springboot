package com.example.springboot.controller;

import com.example.springboot.entity.ExerciseRecord;
import com.example.springboot.entity.Student;
import com.example.springboot.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercise")
@CrossOrigin(origins = "*")
public class ExerciseController {
    
    @Autowired
    private ExerciseService exerciseService;
    
    @PostMapping("/record")
    public ResponseEntity<ExerciseRecord> recordExercise(
            @RequestParam Long studentId,
            @RequestParam Double distance,
            @RequestParam(required = false) Integer time,
            @RequestParam(required = false, defaultValue = "running") String type) {
        ExerciseRecord record = exerciseService.recordExercise(studentId, distance, time, type);
        if (record != null) {
            return ResponseEntity.ok(record);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/ranking/students")
    public ResponseEntity<List<Map<String, Object>>> getStudentRanking(
            @RequestParam(required = false) String date) {
        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(exerciseService.getStudentRankingByDate(queryDate));
    }
    
    @GetMapping("/ranking/classes")
    public ResponseEntity<List<Map<String, Object>>> getClassRanking(
            @RequestParam(required = false) String date) {
        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(exerciseService.getClassRankingByDate(queryDate));
    }
    
    @GetMapping("/not-exercised")
    public ResponseEntity<List<Student>> getStudentsWithoutExercise(
            @RequestParam(required = false) String date) {
        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(exerciseService.getStudentsWithoutExercise(queryDate));
    }
    
    @GetMapping("/report")
    public ResponseEntity<List<Map<String, Object>>> getDailyReport(
            @RequestParam(required = false) String date) {
        LocalDate queryDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        return ResponseEntity.ok(exerciseService.getDailyExerciseReport(queryDate));
    }
}
