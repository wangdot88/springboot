package dangod.springboot.service;

import dangod.springboot.entity.PhysicalTest;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.PhysicalTestRepository;
import dangod.springboot.repository.StudentRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PhysicalTestService {
    
    @Autowired
    private PhysicalTestRepository physicalTestRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    public String calculateGrade(Integer score) {
        if (score >= 90) {
            return "A";
        } else if (score >= 80) {
            return "B";
        } else if (score >= 60) {
            return "C";
        } else {
            return "D";
        }
    }
    
    public List<PhysicalTest> importFromExcel(MultipartFile file) throws Exception {
        List<PhysicalTest> physicalTests = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String studentId = getCellValueAsString(row.getCell(0));
                String testType = getCellValueAsString(row.getCell(1));
                Integer run50m = getCellValueAsInteger(row.getCell(2));
                Integer sitAndReach = getCellValueAsInteger(row.getCell(3));
                Integer longJump = getCellValueAsInteger(row.getCell(4));
                Integer pullUp = getCellValueAsInteger(row.getCell(5));
                Integer sitUp = getCellValueAsInteger(row.getCell(6));
                Integer run800m = getCellValueAsInteger(row.getCell(7));
                Integer run1000m = getCellValueAsInteger(row.getCell(8));
                
                Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
                if (!studentOpt.isPresent()) {
                    continue;
                }
                
                Student student = studentOpt.get();
                
                Integer score = calculateTotalScore(run50m, sitAndReach, longJump, pullUp, sitUp, run800m, run1000m, student.getGender());
                String grade = calculateGrade(score);
                
                PhysicalTest physicalTest = new PhysicalTest();
                physicalTest.setStudent(student);
                physicalTest.setTestType(testType);
                physicalTest.setScore(score);
                physicalTest.setGrade(grade);
                physicalTest.setRun50m(run50m);
                physicalTest.setSitAndReach(sitAndReach);
                physicalTest.setLongJump(longJump);
                physicalTest.setPullUp(pullUp);
                physicalTest.setSitUp(sitUp);
                physicalTest.setRun800m(run800m);
                physicalTest.setRun1000m(run1000m);
                physicalTest.setTestDate(LocalDateTime.now());
                
                physicalTests.add(physicalTest);
                
                if (grade.equals("D")) {
                    student.setIsWeakPhysical(true);
                    studentRepository.save(student);
                }
            }
        }
        
        return physicalTestRepository.saveAll(physicalTests);
    }
    
    private Integer calculateTotalScore(Integer run50m, Integer sitAndReach, Integer longJump, 
                                       Integer pullUp, Integer sitUp, Integer run800m, Integer run1000m, String gender) {
        Integer score = 0;
        
        if (run50m != null) {
            score += calculateRun50mScore(run50m);
        }
        
        if (sitAndReach != null) {
            score += calculateSitAndReachScore(sitAndReach);
        }
        
        if (longJump != null) {
            score += calculateLongJumpScore(longJump);
        }
        
        if ("男".equals(gender) && pullUp != null) {
            score += calculatePullUpScore(pullUp);
        } else if ("女".equals(gender) && sitUp != null) {
            score += calculateSitUpScore(sitUp);
        }
        
        if ("女".equals(gender) && run800m != null) {
            score += calculateRun800mScore(run800m);
        } else if ("男".equals(gender) && run1000m != null) {
            score += calculateRun1000mScore(run1000m);
        }
        
        return score;
    }
    
    private Integer calculateRun50mScore(Integer time) {
        if (time <= 6.7) return 20;
        else if (time <= 6.9) return 18;
        else if (time <= 7.1) return 16;
        else if (time <= 7.3) return 14;
        else if (time <= 7.5) return 12;
        else if (time <= 7.7) return 10;
        else if (time <= 7.9) return 8;
        else if (time <= 8.1) return 6;
        else if (time <= 8.3) return 4;
        else return 2;
    }
    
    private Integer calculateSitAndReachScore(Integer distance) {
        if (distance >= 23) return 20;
        else if (distance >= 21) return 18;
        else if (distance >= 19) return 16;
        else if (distance >= 17) return 14;
        else if (distance >= 15) return 12;
        else if (distance >= 13) return 10;
        else if (distance >= 11) return 8;
        else if (distance >= 9) return 6;
        else if (distance >= 7) return 4;
        else return 2;
    }
    
    private Integer calculateLongJumpScore(Integer distance) {
        if (distance >= 255) return 20;
        else if (distance >= 245) return 18;
        else if (distance >= 235) return 16;
        else if (distance >= 225) return 14;
        else if (distance >= 215) return 12;
        else if (distance >= 205) return 10;
        else if (distance >= 195) return 8;
        else if (distance >= 185) return 6;
        else if (distance >= 175) return 4;
        else return 2;
    }
    
    private Integer calculatePullUpScore(Integer count) {
        if (count >= 15) return 20;
        else if (count >= 13) return 18;
        else if (count >= 11) return 16;
        else if (count >= 9) return 14;
        else if (count >= 7) return 12;
        else if (count >= 5) return 10;
        else if (count >= 3) return 8;
        else if (count >= 1) return 6;
        else return 2;
    }
    
    private Integer calculateSitUpScore(Integer count) {
        if (count >= 52) return 20;
        else if (count >= 48) return 18;
        else if (count >= 44) return 16;
        else if (count >= 40) return 14;
        else if (count >= 36) return 12;
        else if (count >= 32) return 10;
        else if (count >= 28) return 8;
        else if (count >= 24) return 6;
        else if (count >= 20) return 4;
        else return 2;
    }
    
    private Integer calculateRun800mScore(Integer time) {
        if (time <= 210) return 20;
        else if (time <= 220) return 18;
        else if (time <= 230) return 16;
        else if (time <= 240) return 14;
        else if (time <= 250) return 12;
        else if (time <= 260) return 10;
        else if (time <= 270) return 8;
        else if (time <= 280) return 6;
        else if (time <= 290) return 4;
        else return 2;
    }
    
    private Integer calculateRun1000mScore(Integer time) {
        if (time <= 210) return 20;
        else if (time <= 225) return 18;
        else if (time <= 240) return 16;
        else if (time <= 255) return 14;
        else if (time <= 270) return 12;
        else if (time <= 285) return 10;
        else if (time <= 300) return 8;
        else if (time <= 315) return 6;
        else if (time <= 330) return 4;
        else return 2;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        
        CellType cellType = cell.getCellType();
        if (cellType == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cellType == CellType.NUMERIC) {
            return String.valueOf((int) cell.getNumericCellValue());
        } else if (cellType == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return "";
        }
    }
    
    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        
        CellType cellType = cell.getCellType();
        if (cellType == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cellType == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public Map<String, Object> generateClassReport(String className, String testType) {
        List<PhysicalTest> tests = physicalTestRepository.findByClassAndTestType(className, testType);
        
        Long totalStudents = physicalTestRepository.countTotalStudentsByClass(className, testType);
        Long qualifiedStudents = physicalTestRepository.countQualifiedStudentsByClass(className, testType);
        List<PhysicalTest> unqualifiedStudents = physicalTestRepository.findUnqualifiedStudentsByClass(className, testType);
        
        Double passRate = totalStudents > 0 ? (qualifiedStudents.doubleValue() / totalStudents.doubleValue()) * 100 : 0;
        
        Map<String, Object> report = new HashMap<>();
        report.put("className", className);
        report.put("testType", testType);
        report.put("totalStudents", totalStudents);
        report.put("qualifiedStudents", qualifiedStudents);
        report.put("unqualifiedStudents", unqualifiedStudents.size());
        report.put("passRate", passRate);
        report.put("tests", tests);
        
        return report;
    }
    
    public List<Student> getWeakStudents() {
        return studentRepository.findByIsWeakPhysical(true);
    }
    
    public List<Student> getWeakStudentsByClass(String className) {
        return studentRepository.findWeakStudentsByClass(className);
    }
}