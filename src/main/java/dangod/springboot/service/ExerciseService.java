package dangod.springboot.service;

import dangod.springboot.entity.ExerciseRecord;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.ExerciseRecordRepository;
import dangod.springboot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExerciseService {
    
    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public ExerciseRecord recordExercise(String studentId, String exerciseType, Double distance, Integer duration, String location) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (!studentOpt.isPresent()) {
            throw new RuntimeException("学生不存在");
        }
        
        Student student = studentOpt.get();
        
        ExerciseRecord record = new ExerciseRecord();
        record.setStudent(student);
        record.setExerciseType(exerciseType);
        record.setDistance(distance);
        record.setDuration(duration);
        record.setLocation(location);
        record.setExerciseDate(LocalDateTime.now());
        
        Double calories = calculateCalories(exerciseType, distance, duration, student.getGender(), student.getAge());
        record.setCalories(calories);
        
        return exerciseRecordRepository.save(record);
    }
    
    private Double calculateCalories(String exerciseType, Double distance, Integer duration, String gender, Integer age) {
        Double calories = 0.0;
        
        if ("跑步".equals(exerciseType)) {
            if (distance != null) {
                calories = distance * 0.8;
            } else if (duration != null) {
                calories = duration * 0.1;
            }
        } else if ("步行".equals(exerciseType)) {
            if (distance != null) {
                calories = distance * 0.4;
            } else if (duration != null) {
                calories = duration * 0.05;
            }
        } else if ("骑行".equals(exerciseType)) {
            if (distance != null) {
                calories = distance * 0.3;
            } else if (duration != null) {
                calories = duration * 0.08;
            }
        } else if ("游泳".equals(exerciseType)) {
            if (duration != null) {
                calories = duration * 0.15;
            }
        }
        
        if ("男".equals(gender)) {
            calories *= 1.2;
        }
        
        if (age != null && age > 30) {
            calories *= 0.9;
        }
        
        return calories;
    }
    
    public List<Map<String, Object>> getClassRanking(LocalDate date) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        
        List<Object[]> results = exerciseRecordRepository.countStudentsByClassForDate(startOfDay);
        
        return results.stream().map(result -> {
            Map<String, Object> item = new HashMap<>();
            item.put("className", result[0]);
            item.put("studentCount", result[1]);
            return item;
        }).collect(Collectors.toList());
    }
    
    public List<Map<String, Object>> getStudentRanking(LocalDate date) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        
        List<ExerciseRecord> records = exerciseRecordRepository.findByDateRange(startOfDay, endOfDay);
        
        Map<String, Map<String, Object>> studentStats = new HashMap<>();
        
        for (ExerciseRecord record : records) {
            String studentId = record.getStudent().getStudentId();
            String studentName = record.getStudent().getName();
            String className = record.getStudent().getClassName();
            
            if (!studentStats.containsKey(studentId)) {
                Map<String, Object> stats = new HashMap<>();
                stats.put("studentId", studentId);
                stats.put("studentName", studentName);
                stats.put("className", className);
                stats.put("totalDistance", 0.0);
                stats.put("totalDuration", 0);
                stats.put("totalCalories", 0.0);
                stats.put("exerciseCount", 0);
                studentStats.put(studentId, stats);
            }
            
            Map<String, Object> stats = studentStats.get(studentId);
            stats.put("totalDistance", (Double) stats.get("totalDistance") + (record.getDistance() != null ? record.getDistance() : 0));
            stats.put("totalDuration", (Integer) stats.get("totalDuration") + (record.getDuration() != null ? record.getDuration() : 0));
            stats.put("totalCalories", (Double) stats.get("totalCalories") + (record.getCalories() != null ? record.getCalories() : 0));
            stats.put("exerciseCount", (Integer) stats.get("exerciseCount") + 1);
        }
        
        return studentStats.values().stream()
                .sorted((a, b) -> Double.compare((Double) b.get("totalDistance"), (Double) a.get("totalDistance")))
                .collect(Collectors.toList());
    }
    
    public List<Student> getStudentsWhoDidNotExercise(LocalDate date) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        
        List<String> exercisedStudentIds = exerciseRecordRepository.findStudentsWhoExercisedOnDate(startOfDay);
        List<Student> allStudents = studentRepository.findAll();
        
        return allStudents.stream()
                .filter(student -> !exercisedStudentIds.contains(student.getStudentId()))
                .collect(Collectors.toList());
    }
    
    public List<Student> getStudentsWhoDidNotExerciseInWeek(LocalDate startDate) {
        LocalDateTime startOfWeek = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endOfWeek = startOfWeek.plusDays(6).with(LocalTime.MAX);
        
        List<String> exercisedStudentIds = exerciseRecordRepository.findStudentsWhoExercisedInDateRange(startOfWeek, endOfWeek);
        List<Student> allStudents = studentRepository.findAll();
        
        return allStudents.stream()
                .filter(student -> !exercisedStudentIds.contains(student.getStudentId()))
                .collect(Collectors.toList());
    }
    
    public Map<String, Object> generateDailyExerciseReport(LocalDate date) {
        LocalDateTime startOfDay = LocalDateTime.of(date, LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.of(date, LocalTime.MAX);
        
        List<ExerciseRecord> records = exerciseRecordRepository.findByDateRange(startOfDay, endOfDay);
        Long distinctStudentCount = exerciseRecordRepository.countDistinctStudentsByDate(startOfDay);
        List<Map<String, Object>> classRanking = getClassRanking(date);
        List<Map<String, Object>> studentRanking = getStudentRanking(date);
        List<Student> nonExercisedStudents = getStudentsWhoDidNotExercise(date);
        
        Map<String, Object> report = new HashMap<>();
        report.put("date", date);
        report.put("totalRecords", records.size());
        report.put("distinctStudentCount", distinctStudentCount);
        report.put("classRanking", classRanking);
        report.put("studentRanking", studentRanking);
        report.put("nonExercisedStudents", nonExercisedStudents);
        
        return report;
    }
    
    public List<ExerciseRecord> getStudentExerciseHistory(String studentId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        
        return exerciseRecordRepository.findByStudentIdAndDateRange(studentId, startDateTime, endDateTime);
    }
    
    public List<ExerciseRecord> getClassExerciseHistory(String className, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
        
        return exerciseRecordRepository.findByClassAndDateRange(className, startDateTime, endDateTime);
    }
}