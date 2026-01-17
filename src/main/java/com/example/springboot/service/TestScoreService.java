package com.example.springboot.service;

import com.example.springboot.entity.Student;
import com.example.springboot.entity.TestScore;
import com.example.springboot.repository.StudentRepository;
import com.example.springboot.repository.TestScoreRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
public class TestScoreService {
    
    @Autowired
    private TestScoreRepository testScoreRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public void importTestScores(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String studentNo = getCellValue(row.getCell(0));
                Optional<Student> studentOpt = studentRepository.findByStudentNo(studentNo);
                
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    TestScore testScore = new TestScore();
                    testScore.setStudent(student);
                    testScore.setSitUp(getCellIntValue(row.getCell(2)));
                    testScore.setPushUp(getCellIntValue(row.getCell(3)));
                    testScore.setRun1000m(getCellDoubleValue(row.getCell(4)));
                    testScore.setRun800m(getCellDoubleValue(row.getCell(5)));
                    testScore.setVitalCapacity(getCellDoubleValue(row.getCell(6)));
                    testScore.setHeight(getCellDoubleValue(row.getCell(7)));
                    testScore.setWeight(getCellDoubleValue(row.getCell(8)));
                    testScore.setEyesightLeft(getCellDoubleValue(row.getCell(9)));
                    testScore.setEyesightRight(getCellDoubleValue(row.getCell(10)));
                    testScore.setTestDate(LocalDate.now());
                    
                    int totalScore = calculateTotalScore(testScore);
                    testScore.setTotalScore(totalScore);
                    testScore.setGrade(calculateGrade(totalScore));
                    
                    testScoreRepository.save(testScore);
                    
                    if (testScore.getGrade().equals("D")) {
                        student.setIsWeak(true);
                        studentRepository.save(student);
                    }
                }
            }
        }
    }
    
    private int calculateTotalScore(TestScore score) {
        int total = 0;
        if (score.getSitUp() != null) total += Math.min(score.getSitUp() * 2, 20);
        if (score.getPushUp() != null) total += Math.min(score.getPushUp() * 2, 20);
        if (score.getRun1000m() != null) total += calculateRunScore(score.getRun1000m(), true);
        if (score.getRun800m() != null) total += calculateRunScore(score.getRun800m(), false);
        if (score.getVitalCapacity() != null) total += Math.min((int)(score.getVitalCapacity() / 100), 20);
        if (score.getHeight() != null && score.getWeight() != null) total += calculateBMI(score.getHeight(), score.getWeight());
        return total;
    }
    
    private int calculateRunScore(double minutes, boolean isMale) {
        if (isMale) {
            if (minutes <= 4.0) return 20;
            else if (minutes <= 4.5) return 18;
            else if (minutes <= 5.0) return 15;
            else if (minutes <= 5.5) return 12;
            else return 10;
        } else {
            if (minutes <= 4.5) return 20;
            else if (minutes <= 5.0) return 18;
            else if (minutes <= 5.5) return 15;
            else if (minutes <= 6.0) return 12;
            else return 10;
        }
    }
    
    private int calculateBMI(double height, double weight) {
        double bmi = weight / ((height / 100) * (height / 100));
        if (bmi >= 18.5 && bmi <= 23.9) return 10;
        return 8;
    }
    
    private String calculateGrade(int totalScore) {
        if (totalScore >= 90) return "A";
        else if (totalScore >= 80) return "B";
        else if (totalScore >= 60) return "C";
        else return "D";
    }
    
    public Map<String, Object> getClassQualificationReport(String className) {
        List<TestScore> scores = testScoreRepository.findByStudentClassName(className);
        int total = scores.size();
        int qualified = (int) scores.stream().filter(s -> !s.getGrade().equals("D")).count();
        
        Map<String, Object> report = new HashMap<>();
        report.put("className", className);
        report.put("totalStudents", total);
        report.put("qualifiedStudents", qualified);
        report.put("qualificationRate", total > 0 ? (double) qualified / total * 100 : 0);
        report.put("gradeDistribution", getGradeDistribution(scores));
        
        return report;
    }
    
    private Map<String, Integer> getGradeDistribution(List<TestScore> scores) {
        Map<String, Integer> distribution = new HashMap<>();
        distribution.put("A", (int) scores.stream().filter(s -> s.getGrade().equals("A")).count());
        distribution.put("B", (int) scores.stream().filter(s -> s.getGrade().equals("B")).count());
        distribution.put("C", (int) scores.stream().filter(s -> s.getGrade().equals("C")).count());
        distribution.put("D", (int) scores.stream().filter(s -> s.getGrade().equals("D")).count());
        return distribution;
    }
    
    public List<Student> getWeakStudents() {
        return studentRepository.findByIsWeak(true);
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING: return cell.getStringCellValue();
            case Cell.CELL_TYPE_NUMERIC: return String.valueOf((int) cell.getNumericCellValue());
            default: return "";
        }
    }
    
    private Integer getCellIntValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return (int) cell.getNumericCellValue();
        }
        return null;
    }
    
    private Double getCellDoubleValue(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return cell.getNumericCellValue();
        }
        return null;
    }
}
