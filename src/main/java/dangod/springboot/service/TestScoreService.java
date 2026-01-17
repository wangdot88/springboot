package dangod.springboot.service;

import dangod.springboot.model.entity.Student;
import dangod.springboot.model.entity.TestScore;
import dangod.springboot.repository.StudentRepository;
import dangod.springboot.repository.TestScoreRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

@Service
public class TestScoreService {
    @Autowired
    private TestScoreRepository testScoreRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Transactional
    public int importTestScores(MultipartFile file, Integer testYear) throws IOException {
        Map<String, Object> result = new HashMap<>();
        List<String> successList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            try {
                String studentNo = getCellValue(row.getCell(0));
                if (studentNo == null || studentNo.trim().isEmpty()) {
                    errorList.add("第" + (i + 1) + "行：学号为空");
                    continue;
                }
                
                Optional<Student> studentOpt = studentRepository.findByStudentNo(studentNo);
                if (!studentOpt.isPresent()) {
                    errorList.add("第" + (i + 1) + "行：学号" + studentNo + "不存在");
                    continue;
                }
                
                Student student = studentOpt.get();
                TestScore testScore = testScoreRepository.findByStudentIdAndTestYear(student.getId(), testYear)
                        .orElse(new TestScore());
                
                testScore.setStudentId(student.getId());
                testScore.setTestYear(testYear);
                
                String testDateStr = getCellValue(row.getCell(1));
                if (testDateStr != null && !testDateStr.trim().isEmpty()) {
                    testScore.setTestDate(sdf.parse(testDateStr));
                }
                
                testScore.setHeight(parseBigDecimal(getCellValue(row.getCell(2))));
                testScore.setWeight(parseBigDecimal(getCellValue(row.getCell(3))));
                testScore.setVitalCapacity(parseBigDecimal(getCellValue(row.getCell(4))));
                testScore.setSitAndReach(parseBigDecimal(getCellValue(row.getCell(5))));
                testScore.setStandingLongJump(parseBigDecimal(getCellValue(row.getCell(6))));
                testScore.set_50mRun(parseBigDecimal(getCellValue(row.getCell(7))));
                
                if ("MALE".equals(student.getGender())) {
                    testScore.set_1000mRun(parseBigDecimal(getCellValue(row.getCell(8))));
                    testScore.setPullUp(parseInteger(getCellValue(row.getCell(9))));
                } else {
                    testScore.set_800mRun(parseBigDecimal(getCellValue(row.getCell(8))));
                    testScore.setSitUp(parseInteger(getCellValue(row.getCell(9))));
                }
                
                calculateBMITestScore(testScore, student);
                calculateTotalScoreAndLevel(testScore, student);
                
                testScoreRepository.save(testScore);
                successList.add(studentNo + " - " + student.getName());
                
            } catch (Exception e) {
                errorList.add("第" + (i + 1) + "行：" + e.getMessage());
            }
        }
        
        workbook.close();
        
        result.put("successCount", successList.size());
        result.put("errorCount", errorList.size());
        result.put("successList", successList);
        result.put("errorList", errorList);
        
        return successList.size();
    }
    
    private void calculateBMITestScore(TestScore testScore, Student student) {
        if (testScore.getHeight() != null && testScore.getWeight() != null) {
            BigDecimal heightInM = testScore.getHeight().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            BigDecimal bmi = testScore.getWeight().divide(heightInM.multiply(heightInM), 2, RoundingMode.HALF_UP);
            testScore.setBmi(bmi);
            
            if (student.getAge() != null && student.getAge() < 18 && bmi.compareTo(new BigDecimal(18)) < 0) {
                student.setIsWeak(true);
                studentRepository.save(student);
            }
        }
    }
    
    private void calculateTotalScoreAndLevel(TestScore testScore, Student student) {
        BigDecimal totalScore = BigDecimal.ZERO;
        
        if (testScore.getHeight() != null && testScore.getWeight() != null) {
            totalScore = totalScore.add(calculateBMIScore(testScore.getBmi(), student.getGender()));
        }
        
        if (testScore.getVitalCapacity() != null) {
            totalScore = totalScore.add(calculateVitalCapacityScore(testScore.getVitalCapacity(), student.getGender(), student.getAge()));
        }
        
        if (testScore.getSitAndReach() != null) {
            totalScore = totalScore.add(calculateSitAndReachScore(testScore.getSitAndReach(), student.getGender()));
        }
        
        if (testScore.getStandingLongJump() != null) {
            totalScore = totalScore.add(calculateStandingLongJumpScore(testScore.getStandingLongJump(), student.getGender()));
        }
        
        if (testScore.get_50mRun() != null) {
            totalScore = totalScore.add(calculate50mRunScore(testScore.get_50mRun(), student.getGender()));
        }
        
        if ("MALE".equals(student.getGender())) {
            if (testScore.get_1000mRun() != null) {
                totalScore = totalScore.add(calculate1000mRunScore(testScore.get_1000mRun()));
            }
            if (testScore.getPullUp() != null) {
                totalScore = totalScore.add(calculatePullUpScore(testScore.getPullUp()));
            }
        } else {
            if (testScore.get_800mRun() != null) {
                totalScore = totalScore.add(calculate800mRunScore(testScore.get_800mRun()));
            }
            if (testScore.getSitUp() != null) {
                totalScore = totalScore.add(calculateSitUpScore(testScore.getSitUp()));
            }
        }
        
        testScore.setTotalScore(totalScore.setScale(1, RoundingMode.HALF_UP));
        testScore.setLevel(calculateLevel(totalScore));
        testScore.setIsPass(totalScore.compareTo(new BigDecimal(60)) >= 0);
    }
    
    private BigDecimal calculateBMIScore(BigDecimal bmi, String gender) {
        if ("MALE".equals(gender)) {
            if (bmi.compareTo(new BigDecimal(22)) <= 0) return new BigDecimal(15);
            if (bmi.compareTo(new BigDecimal(24)) <= 0) return new BigDecimal(12);
            if (bmi.compareTo(new BigDecimal(28)) <= 0) return new BigDecimal(8);
            return new BigDecimal(5);
        } else {
            if (bmi.compareTo(new BigDecimal(20)) <= 0) return new BigDecimal(15);
            if (bmi.compareTo(new BigDecimal(22)) <= 0) return new BigDecimal(12);
            if (bmi.compareTo(new BigDecimal(26)) <= 0) return new BigDecimal(8);
            return new BigDecimal(5);
        }
    }
    
    private BigDecimal calculateVitalCapacityScore(BigDecimal vitalCapacity, String gender, Integer age) {
        BigDecimal base = "MALE".equals(gender) ? new BigDecimal(4000) : new BigDecimal(3000);
        BigDecimal ratio = vitalCapacity.divide(base, 2, RoundingMode.HALF_UP);
        if (ratio.compareTo(new BigDecimal(1.3)) >= 0) return new BigDecimal(15);
        if (ratio.compareTo(new BigDecimal(1.1)) >= 0) return new BigDecimal(12);
        if (ratio.compareTo(new BigDecimal(0.9)) >= 0) return new BigDecimal(10);
        if (ratio.compareTo(new BigDecimal(0.7)) >= 0) return new BigDecimal(7);
        return new BigDecimal(4);
    }
    
    private BigDecimal calculateSitAndReachScore(BigDecimal cm, String gender) {
        if ("MALE".equals(gender)) {
            if (cm.compareTo(new BigDecimal(25)) >= 0) return new BigDecimal(10);
            if (cm.compareTo(new BigDecimal(20)) >= 0) return new BigDecimal(8);
            if (cm.compareTo(new BigDecimal(15)) >= 0) return new BigDecimal(6);
            if (cm.compareTo(new BigDecimal(10)) >= 0) return new BigDecimal(4);
            return new BigDecimal(2);
        } else {
            if (cm.compareTo(new BigDecimal(20)) >= 0) return new BigDecimal(10);
            if (cm.compareTo(new BigDecimal(15)) >= 0) return new BigDecimal(8);
            if (cm.compareTo(new BigDecimal(10)) >= 0) return new BigDecimal(6);
            if (cm.compareTo(new BigDecimal(5)) >= 0) return new BigDecimal(4);
            return new BigDecimal(2);
        }
    }
    
    private BigDecimal calculateStandingLongJumpScore(BigDecimal meters, String gender) {
        if ("MALE".equals(gender)) {
            if (meters.compareTo(new BigDecimal(2.6)) >= 0) return new BigDecimal(10);
            if (meters.compareTo(new BigDecimal(2.4)) >= 0) return new BigDecimal(8);
            if (meters.compareTo(new BigDecimal(2.2)) >= 0) return new BigDecimal(6);
            if (meters.compareTo(new BigDecimal(2.0)) >= 0) return new BigDecimal(4);
            return new BigDecimal(2);
        } else {
            if (meters.compareTo(new BigDecimal(2.0)) >= 0) return new BigDecimal(10);
            if (meters.compareTo(new BigDecimal(1.8)) >= 0) return new BigDecimal(8);
            if (meters.compareTo(new BigDecimal(1.6)) >= 0) return new BigDecimal(6);
            if (meters.compareTo(new BigDecimal(1.4)) >= 0) return new BigDecimal(4);
            return new BigDecimal(2);
        }
    }
    
    private BigDecimal calculate50mRunScore(BigDecimal seconds, String gender) {
        if ("MALE".equals(gender)) {
            if (seconds.compareTo(new BigDecimal(7.5)) <= 0) return new BigDecimal(10);
            if (seconds.compareTo(new BigDecimal(8.5)) <= 0) return new BigDecimal(8);
            if (seconds.compareTo(new BigDecimal(9.5)) <= 0) return new BigDecimal(6);
            if (seconds.compareTo(new BigDecimal(10.5)) <= 0) return new BigDecimal(4);
            return new BigDecimal(2);
        } else {
            if (seconds.compareTo(new BigDecimal(8.5)) <= 0) return new BigDecimal(10);
            if (seconds.compareTo(new BigDecimal(9.5)) <= 0) return new BigDecimal(8);
            if (seconds.compareTo(new BigDecimal(10.5)) <= 0) return new BigDecimal(6);
            if (seconds.compareTo(new BigDecimal(11.5)) <= 0) return new BigDecimal(4);
            return new BigDecimal(2);
        }
    }
    
    private BigDecimal calculate1000mRunScore(BigDecimal seconds) {
        if (seconds.compareTo(new BigDecimal(210)) <= 0) return new BigDecimal(20);
        if (seconds.compareTo(new BigDecimal(240)) <= 0) return new BigDecimal(16);
        if (seconds.compareTo(new BigDecimal(270)) <= 0) return new BigDecimal(12);
        if (seconds.compareTo(new BigDecimal(300)) <= 0) return new BigDecimal(8);
        return new BigDecimal(4);
    }
    
    private BigDecimal calculate800mRunScore(BigDecimal seconds) {
        if (seconds.compareTo(new BigDecimal(210)) <= 0) return new BigDecimal(20);
        if (seconds.compareTo(new BigDecimal(240)) <= 0) return new BigDecimal(16);
        if (seconds.compareTo(new BigDecimal(270)) <= 0) return new BigDecimal(12);
        if (seconds.compareTo(new BigDecimal(300)) <= 0) return new BigDecimal(8);
        return new BigDecimal(4);
    }
    
    private BigDecimal calculatePullUpScore(Integer count) {
        if (count >= 15) return new BigDecimal(20);
        if (count >= 12) return new BigDecimal(16);
        if (count >= 9) return new BigDecimal(12);
        if (count >= 6) return new BigDecimal(8);
        if (count >= 3) return new BigDecimal(4);
        return new BigDecimal(0);
    }
    
    private BigDecimal calculateSitUpScore(Integer count) {
        if (count >= 50) return new BigDecimal(20);
        if (count >= 40) return new BigDecimal(16);
        if (count >= 30) return new BigDecimal(12);
        if (count >= 20) return new BigDecimal(8);
        if (count >= 10) return new BigDecimal(4);
        return new BigDecimal(0);
    }
    
    private String calculateLevel(BigDecimal totalScore) {
        if (totalScore.compareTo(new BigDecimal(90)) >= 0) return "A";
        if (totalScore.compareTo(new BigDecimal(80)) >= 0) return "B";
        if (totalScore.compareTo(new BigDecimal(70)) >= 0) return "C";
        if (totalScore.compareTo(new BigDecimal(60)) >= 0) return "D";
        return "D";
    }
    
    public List<TestScore> getTestScoreList(Integer year, String className, String level, int page, int size) {
        PageRequest pageRequest = new PageRequest(page, size);
        Page<TestScore> scorePage;
        
        if (year != null && className != null && level != null) {
            scorePage = testScoreRepository.findByTestYearAndClassNameAndLevel(year, className, level, pageRequest);
        } else if (year != null && className != null) {
            scorePage = testScoreRepository.findByTestYearAndClassName(year, className, pageRequest);
        } else if (year != null && level != null) {
            scorePage = testScoreRepository.findByTestYearAndLevel(year, level, pageRequest);
        } else if (year != null) {
            scorePage = testScoreRepository.findByTestYear(year, pageRequest);
        } else {
            scorePage = testScoreRepository.findAll(pageRequest);
        }
        
        return scorePage.getContent();
    }
    
    public List<Map<String, Object>> getClassQualifiedRateReport(Integer testYear, String department) {
        List<String> classNames = studentRepository.findAllClassNames();
        List<Map<String, Object>> report = new ArrayList<>();
        
        for (String className : classNames) {
            List<TestScore> scores = testScoreRepository.findByClassNameAndTestYear(className, testYear);
            if (scores.isEmpty()) continue;
            
            long passed = scores.stream().filter(ts -> ts.getIsPass()).count();
            double rate = (double) passed / scores.size() * 100;
            
            Map<String, Object> classReport = new HashMap<>();
            classReport.put("className", className);
            classReport.put("totalStudents", scores.size());
            classReport.put("passedCount", passed);
            classReport.put("failedCount", scores.size() - passed);
            classReport.put("qualifiedRate", String.format("%.2f%%", rate));
            
            long levelA = scores.stream().filter(ts -> "A".equals(ts.getLevel())).count();
            long levelB = scores.stream().filter(ts -> "B".equals(ts.getLevel())).count();
            long levelC = scores.stream().filter(ts -> "C".equals(ts.getLevel())).count();
            long levelD = scores.stream().filter(ts -> "D".equals(ts.getLevel())).count();
            
            classReport.put("levelACount", levelA);
            classReport.put("levelBCount", levelB);
            classReport.put("levelCCount", levelC);
            classReport.put("levelDCount", levelD);
            
            report.add(classReport);
        }
        
        return report;
    }
    
    public List<Student> getWeakStudents() {
        return studentRepository.findByIsWeak(true);
    }
    
    public List<TestScore> getTestScoresByYear(Integer testYear) {
        List<TestScore> scores = testScoreRepository.findByTestYear(testYear);
        scores.forEach(ts -> {
            Student student = studentRepository.findOne(ts.getStudentId());
            if (student != null) {
                ts.setStudentName(student.getName());
                ts.setStudentNo(student.getStudentNo());
                ts.setClassName(student.getClassName());
            }
        });
        return scores;
    }
    
    public TestScore getTestScoreByStudentAndYear(Long studentId, Integer testYear) {
        Optional<TestScore> scoreOpt = testScoreRepository.findByStudentIdAndTestYear(studentId, testYear);
        if (scoreOpt.isPresent()) {
            TestScore ts = scoreOpt.get();
            Student student = studentRepository.findOne(ts.getStudentId());
            if (student != null) {
                ts.setStudentName(student.getName());
                ts.setStudentNo(student.getStudentNo());
                ts.setClassName(student.getClassName());
            }
            return ts;
        }
        return null;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public void exportTestScoreReport(Integer testYear, String className, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> classReports = getClassQualifiedRateReport(testYear, null);
        
        if (className != null && !className.trim().isEmpty()) {
            classReports = classReports.stream()
                    .filter(r -> className.equals(r.get("className")))
                    .collect(Collectors.toList());
        }
        
        Workbook workbook = new XSSFWorkbook();
        String sheetName = className != null ? "体测成绩 - " + className : "体测合格率报告 - " + testYear;
        Sheet sheet = workbook.createSheet(sheetName);
        
        String[] headers = {"班级", "总人数", "合格人数", "不合格人数", "合格率", "A级人数", "B级人数", "C级人数", "D级人数"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        int rowNum = 1;
        for (Map<String, Object> classReport : classReports) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) classReport.get("className"));
            row.createCell(1).setCellValue((Long) classReport.get("totalStudents"));
            row.createCell(2).setCellValue((Long) classReport.get("passedCount"));
            row.createCell(3).setCellValue((Long) classReport.get("failedCount"));
            row.createCell(4).setCellValue((String) classReport.get("qualifiedRate"));
            row.createCell(5).setCellValue((Long) classReport.get("levelACount"));
            row.createCell(6).setCellValue((Long) classReport.get("levelBCount"));
            row.createCell(7).setCellValue((Long) classReport.get("levelCCount"));
            row.createCell(8).setCellValue((Long) classReport.get("levelDCount"));
        }
        
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=test-score-report-" + System.currentTimeMillis() + ".xlsx");
        
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
