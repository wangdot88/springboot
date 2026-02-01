package dangod.springboot.service.fitness;

import dangod.springboot.core.common.BusinessException;
import dangod.springboot.entity.Clazz;
import dangod.springboot.entity.Student;
import dangod.springboot.entity.fitness.FitnessTest;
import dangod.springboot.entity.fitness.FitnessTestReport;
import dangod.springboot.repository.ClazzRepository;
import dangod.springboot.repository.StudentRepository;
import dangod.springboot.repository.fitness.FitnessTestReportRepository;
import dangod.springboot.repository.fitness.FitnessTestRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class FitnessTestService {

    @Autowired
    private FitnessTestRepository fitnessTestRepository;

    @Autowired
    private FitnessTestReportRepository reportRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClazzRepository clazzRepository;

    private static final Map<String, Integer> GRADE_SCORES = new HashMap<>();
    static {
        GRADE_SCORES.put("A", 20);
        GRADE_SCORES.put("B", 15);
        GRADE_SCORES.put("C", 10);
        GRADE_SCORES.put("D", 5);
    }

    @Transactional
    public Map<String, Object> importFitnessTestData(MultipartFile file, String semester) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        int successCount = 0;
        int failCount = 0;
        List<String> errorMessages = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String studentNo = getCellValue(row.getCell(0));
                    if (studentNo == null || studentNo.trim().isEmpty()) {
                        continue;
                    }

                    Optional<Student> studentOpt = studentRepository.findByStudentNo(studentNo);
                    if (!studentOpt.isPresent()) {
                        failCount++;
                        errorMessages.add("第" + (i + 1) + "行：学号 " + studentNo + " 不存在");
                        continue;
                    }

                    Student student = studentOpt.get();
                    FitnessTest test = fitnessTestRepository.findByStudentIdAndSemester(student.getId(), semester);
                    if (test == null) {
                        test = new FitnessTest();
                        test.setStudent(student);
                        test.setSemester(semester);
                    }

                    test.setTestDate(LocalDate.now());
                    test.setHeight(parseDouble(getCellValue(row.getCell(2))));
                    test.setWeight(parseDouble(getCellValue(row.getCell(3))));
                    test.setBmi(calculateBMI(test.getHeight(), test.getWeight()));
                    test.setVitalCapacity(parseInteger(getCellValue(row.getCell(4))));
                    test.setFiftyMeter(parseDouble(getCellValue(row.getCell(5))));
                    test.setJump(parseInteger(getCellValue(row.getCell(6))));
                    test.setSitReach(parseDouble(getCellValue(row.getCell(7))));
                    test.setEnduranceRun(parseInteger(getCellValue(row.getCell(8))));
                    test.setPullUp(parseInteger(getCellValue(row.getCell(9))));
                    test.setSitUp(parseInteger(getCellValue(row.getCell(10))));

                    calculateGrades(test, student.getGender());

                    fitnessTestRepository.save(test);
                    successCount++;

                } catch (Exception e) {
                    failCount++;
                    errorMessages.add("第" + (i + 1) + "行：" + e.getMessage());
                    log.error("导入体测数据失败", e);
                }
            }

        } catch (IOException e) {
            throw new BusinessException("读取Excel文件失败：" + e.getMessage());
        }

        generateClassReports(semester);

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("errorMessages", errorMessages);
        return result;
    }

    private void calculateGrades(FitnessTest test, String gender) {
        test.setVitalCapacityGrade(calculateVitalCapacityGrade(test.getVitalCapacity(), gender));
        test.setFiftyMeterGrade(calculateFiftyMeterGrade(test.getFiftyMeter(), gender));
        test.setJumpGrade(calculateJumpGrade(test.getJump(), gender));
        test.setSitReachGrade(calculateSitReachGrade(test.getSitReach(), gender));
        test.setEnduranceRunGrade(calculateEnduranceRunGrade(test.getEnduranceRun(), gender));

        if ("男".equals(gender)) {
            test.setPullUpGrade(calculatePullUpGrade(test.getPullUp()));
        } else {
            test.setSitUpGrade(calculateSitUpGrade(test.getSitUp()));
        }

        int totalScore = calculateTotalScore(test);
        test.setTotalScore(totalScore);
        test.setTotalGrade(calculateTotalGrade(totalScore));
        test.setIsPassed(totalScore >= 60);
        test.setIsWeakStudent(totalScore < 60);

        if (test.getIsWeakStudent()) {
            Student student = test.getStudent();
            student.setIsWeak(true);
            studentRepository.save(student);
        }
    }

    private String calculateVitalCapacityGrade(Integer value, String gender) {
        if (value == null) return "D";
        if ("男".equals(gender)) {
            if (value >= 3700) return "A";
            if (value >= 3100) return "B";
            if (value >= 2500) return "C";
        } else {
            if (value >= 2900) return "A";
            if (value >= 2400) return "B";
            if (value >= 1900) return "C";
        }
        return "D";
    }

    private String calculateFiftyMeterGrade(Double value, String gender) {
        if (value == null) return "D";
        if ("男".equals(gender)) {
            if (value <= 7.1) return "A";
            if (value <= 7.8) return "B";
            if (value <= 9.0) return "C";
        } else {
            if (value <= 7.9) return "A";
            if (value <= 8.6) return "B";
            if (value <= 10.0) return "C";
        }
        return "D";
    }

    private String calculateJumpGrade(Integer value, String gender) {
        if (value == null) return "D";
        if ("男".equals(gender)) {
            if (value >= 230) return "A";
            if (value >= 205) return "B";
            if (value >= 180) return "C";
        } else {
            if (value >= 195) return "A";
            if (value >= 175) return "B";
            if (value >= 155) return "C";
        }
        return "D";
    }

    private String calculateSitReachGrade(Double value, String gender) {
        if (value == null) return "D";
        if ("男".equals(gender)) {
            if (value >= 17.7) return "A";
            if (value >= 12.3) return "B";
            if (value >= 3.7) return "C";
        } else {
            if (value >= 22.2) return "A";
            if (value >= 16.7) return "B";
            if (value >= 8.9) return "C";
        }
        return "D";
    }

    private String calculateEnduranceRunGrade(Integer value, String gender) {
        if (value == null) return "D";
        if ("男".equals(gender)) {
            if (value <= 230) return "A";
            if (value <= 275) return "B";
            if (value <= 340) return "C";
        } else {
            if (value <= 210) return "A";
            if (value <= 240) return "B";
            if (value <= 290) return "C";
        }
        return "D";
    }

    private String calculatePullUpGrade(Integer value) {
        if (value == null) return "D";
        if (value >= 16) return "A";
        if (value >= 11) return "B";
        if (value >= 5) return "C";
        return "D";
    }

    private String calculateSitUpGrade(Integer value) {
        if (value == null) return "D";
        if (value >= 52) return "A";
        if (value >= 42) return "B";
        if (value >= 26) return "C";
        return "D";
    }

    private int calculateTotalScore(FitnessTest test) {
        int score = 0;
        score += getScoreByGrade(test.getVitalCapacityGrade());
        score += getScoreByGrade(test.getFiftyMeterGrade());
        score += getScoreByGrade(test.getJumpGrade());
        score += getScoreByGrade(test.getSitReachGrade());
        score += getScoreByGrade(test.getEnduranceRunGrade());
        score += getScoreByGrade(test.getPullUpGrade());
        score += getScoreByGrade(test.getSitUpGrade());
        return score;
    }

    private int getScoreByGrade(String grade) {
        return GRADE_SCORES.getOrDefault(grade, 0);
    }

    private String calculateTotalGrade(int score) {
        if (score >= 80) return "A";
        if (score >= 60) return "B";
        if (score >= 40) return "C";
        return "D";
    }

    private double calculateBMI(Double height, Double weight) {
        if (height == null || weight == null || height <= 0) return 0;
        return weight / ((height / 100) * (height / 100));
    }

    @Transactional
    public void generateClassReports(String semester) {
        List<Clazz> classes = clazzRepository.findAll();
        for (Clazz clazz : classes) {
            List<FitnessTest> tests = fitnessTestRepository.findByClassIdAndSemester(clazz.getId(), semester);
            if (tests.isEmpty()) continue;

            FitnessTestReport report = reportRepository.findByClazzIdAndSemester(clazz.getId(), semester);
            if (report == null) {
                report = new FitnessTestReport();
                report.setClazz(clazz);
                report.setSemester(semester);
            }

            int total = tests.size();
            int passed = (int) tests.stream().filter(FitnessTest::getIsPassed).count();
            int gradeA = (int) tests.stream().filter(t -> "A".equals(t.getTotalGrade())).count();
            int gradeB = (int) tests.stream().filter(t -> "B".equals(t.getTotalGrade())).count();
            int gradeC = (int) tests.stream().filter(t -> "C".equals(t.getTotalGrade())).count();
            int gradeD = (int) tests.stream().filter(t -> "D".equals(t.getTotalGrade())).count();
            int weak = (int) tests.stream().filter(FitnessTest::getIsWeakStudent).count();
            double avgScore = tests.stream().mapToInt(FitnessTest::getTotalScore).average().orElse(0);

            report.setTotalStudents(clazz.getStudentCount());
            report.setTestedStudents(total);
            report.setPassedStudents(passed);
            report.setFailedStudents(total - passed);
            report.setPassRate(total > 0 ? (passed * 100.0 / total) : 0);
            report.setGradeACount(gradeA);
            report.setGradeBCount(gradeB);
            report.setGradeCCount(gradeC);
            report.setGradeDCount(gradeD);
            report.setWeakStudentsCount(weak);
            report.setAvgTotalScore(avgScore);

            reportRepository.save(report);
        }
    }

    public List<FitnessTestReport> getClassReports(String semester) {
        return reportRepository.findBySemester(semester);
    }

    public List<FitnessTest> getWeakStudents(String semester) {
        return fitnessTestRepository.findWeakStudents(semester);
    }

    public List<FitnessTest> getStudentTests(Long studentId) {
        return fitnessTestRepository.findByStudentId(studentId);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
