package com.example.springboot.service;

import com.example.springboot.entity.ExerciseRecord;
import com.example.springboot.entity.Student;
import com.example.springboot.repository.ExerciseRecordRepository;
import com.example.springboot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ExerciseService {

    @Autowired
    private ExerciseRecordRepository exerciseRecordRepository;

    @Autowired
    private StudentRepository studentRepository;

    public List<Map<String, Object>> getStudentRankingByDate(LocalDate date) {
        List<Object[]> results = exerciseRecordRepository.findClassRankingByDate(date);
        List<Map<String, Object>> rankings = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> ranking = new HashMap<>();
            ranking.put("className", result[0]);
            ranking.put("studentName", result[1]);
            ranking.put("totalDistance", result[2]);
            rankings.add(ranking);
        }
        return rankings;
    }

    @Autowired
    private StudentRepository studentRepository;

    public ExerciseRecord addExerciseRecord(ExerciseRecord record) {
        return recordExercise(record);
    }

    public ExerciseRecord recordExercise(Long studentId, Double distance, Integer time, String type) {
        ExerciseRecord record = new ExerciseRecord();
        Optional<Student> studentOptional = studentRepository.findById(studentId);
        if (studentOptional.isPresent()) {
            record.setStudent(studentOptional.get());
            record.setRecordDate(LocalDate.now());
            record.setRunningDistance(distance);
            record.setRunningTime(time);
            record.setExerciseType(type);
            return exerciseRecordRepository.save(record);
        }
        throw new RuntimeException("Student not found");
    }

    public ExerciseRecord recordExercise(ExerciseRecord record) {
        Optional<Student> studentOptional = studentRepository.findById(record.getStudent().getId());
        if (studentOptional.isPresent()) {
            record.setStudent(studentOptional.get());
            return exerciseRecordRepository.save(record);
        }
        throw new RuntimeException("Student not found");
    }

    public List<ExerciseRecord> getRecordsByDate(LocalDate date) {
        return exerciseRecordRepository.findByRecordDate(date);
    }

    public List<ExerciseRecord> getRecordsByStudent(Long studentId, LocalDate startDate, LocalDate endDate) {
        return exerciseRecordRepository.findByStudentIdAndRecordDateBetween(studentId, startDate, endDate);
    }

    public List<Map<String, Object>> getClassRankingByDate(LocalDate date) {
        return getClassRankings(date);
    }

    public List<Map<String, Object>> getClassRankings(LocalDate date) {
        List<Object[]> results = exerciseRecordRepository.findClassTotalRankingByDate(date);
        List<Map<String, Object>> rankings = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> ranking = new HashMap<>();
            ranking.put("className", result[0]);
            ranking.put("totalDistance", result[1]);
            rankings.add(ranking);
        }
        rankings.sort((a, b) -> Double.compare((Double) b.get("totalDistance"), (Double) a.get("totalDistance")));
        return rankings;
    }

    public List<Student> getStudentsWithoutExercise(LocalDate date) {
        return studentRepository.findStudentsWithoutExercise(date);
    }

    public List<Map<String, Object>> generateDailyReport(LocalDate date) {
        return getDailyExerciseReport(date);
    }

    public List<Map<String, Object>> getDailyExerciseReport(LocalDate date) {
        return generateDailyReport(date);
    }

    public List<Map<String, Object>> generateDailyReport(LocalDate date) {
        List<ExerciseRecord> records = exerciseRecordRepository.findByRecordDate(date);
        List<Map<String, Object>> report = new ArrayList<>();
        for (ExerciseRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("studentNo", record.getStudent().getStudentNo());
            item.put("studentName", record.getStudent().getName());
            item.put("className", record.getStudent().getClassName());
            item.put("distance", record.getDistance());
            item.put("duration", record.getDuration());
            item.put("calories", record.getCalories());
            item.put("recordTime", record.getRecordTime());
            report.add(item);
        }
        return report;
    }
}
