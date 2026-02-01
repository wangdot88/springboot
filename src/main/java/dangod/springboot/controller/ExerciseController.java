package dangod.springboot.controller;

import dangod.springboot.entity.ExerciseRecord;
import dangod.springboot.entity.Student;
import dangod.springboot.service.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exercise")
public class ExerciseController {
    
    @Autowired
    private ExerciseService exerciseService;
    
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(@RequestParam String studentId, 
                                    @RequestParam String exerciseType, 
                                    @RequestParam(required = false) Double distance, 
                                    @RequestParam(required = false) Integer duration, 
                                    @RequestParam(required = false) String location) {
        try {
            ExerciseRecord record = exerciseService.recordExercise(studentId, exerciseType, distance, duration, location);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("打卡失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/class-ranking")
    public ResponseEntity<?> getClassRanking(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            List<Map<String, Object>> ranking = exerciseService.getClassRanking(date);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取班级排行榜失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/student-ranking")
    public ResponseEntity<?> getStudentRanking(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            List<Map<String, Object>> ranking = exerciseService.getStudentRanking(date);
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取学生排行榜失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/non-exercised-students")
    public ResponseEntity<?> getNonExercisedStudents(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            List<Student> students = exerciseService.getStudentsWhoDidNotExercise(date);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取未运动学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/non-exercised-week")
    public ResponseEntity<?> getNonExercisedStudentsInWeek(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate) {
        try {
            List<Student> students = exerciseService.getStudentsWhoDidNotExerciseInWeek(startDate);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取一周未运动学生失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/daily-report")
    public ResponseEntity<?> getDailyExerciseReport(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            Map<String, Object> report = exerciseService.generateDailyExerciseReport(date);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("生成每日运动报告失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/student-history/{studentId}")
    public ResponseEntity<?> getStudentExerciseHistory(@PathVariable String studentId, 
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                     @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<ExerciseRecord> records = exerciseService.getStudentExerciseHistory(studentId, startDate, endDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取学生运动历史失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/class-history/{className}")
    public ResponseEntity<?> getClassExerciseHistory(@PathVariable String className, 
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            List<ExerciseRecord> records = exerciseService.getClassExerciseHistory(className, startDate, endDate);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取班级运动历史失败: " + e.getMessage());
        }
    }
}