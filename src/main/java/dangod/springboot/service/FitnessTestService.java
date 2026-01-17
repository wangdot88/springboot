package dangod.springboot.service;

import dangod.springboot.dto.ClassReportDTO;
import dangod.springboot.dto.FitnessTestImportDTO;
import dangod.springboot.entity.FitnessTest;
import dangod.springboot.entity.Student;
import dangod.springboot.repository.FitnessTestRepository;
import dangod.springboot.repository.StudentRepository;
import dangod.springboot.util.FitnessTestCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FitnessTestService {

    @Autowired
    private FitnessTestRepository fitnessTestRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public void importFitnessTests(List<FitnessTestImportDTO> dtos) {
        for (FitnessTestImportDTO dto : dtos) {
            Student student = studentRepository.findByStudentNo(dto.getStudentNo());
            if (student == null) {
                continue;
            }

            FitnessTest fitnessTest = new FitnessTest();
            fitnessTest.setStudent(student);
            fitnessTest.setYear(dto.getYear());
            fitnessTest.setSemester(dto.getSemester());
            fitnessTest.setRun1000(dto.getRun1000());
            fitnessTest.setRun800(dto.getRun800());
            fitnessTest.setRun50(dto.getRun50());
            fitnessTest.setSitAndReach(dto.getSitAndReach());
            fitnessTest.setStandingLongJump(dto.getStandingLongJump());
            fitnessTest.setSitUp(dto.getSitUp());
            fitnessTest.setPullUp(dto.getPullUp());
            fitnessTest.setBmi(dto.getBmi());

            Double totalScore = FitnessTestCalculator.calculateTotalScore(
                dto.getRun1000(), dto.getRun800(), dto.getRun50(),
                dto.getSitAndReach(), dto.getStandingLongJump(),
                dto.getSitUp(), dto.getPullUp(), dto.getBmi()
            );

            String level = FitnessTestCalculator.calculateLevel(totalScore);
            Boolean isQualified = FitnessTestCalculator.isQualified(totalScore);

            fitnessTest.setTotalScore(totalScore);
            fitnessTest.setLevel(level);
            fitnessTest.setIsQualified(isQualified);
            fitnessTest.setCreateTime(new Date());
            fitnessTest.setUpdateTime(new Date());

            fitnessTestRepository.save(fitnessTest);

            if (!isQualified) {
                student.setIsWeak(true);
                studentRepository.save(student);
            }
        }
    }

    public List<FitnessTest> getFitnessTestsByYearAndSemester(Integer year, Integer semester) {
        return fitnessTestRepository.findByYearAndSemester(year, semester);
    }

    public List<FitnessTest> getFitnessTestsByClassId(Long classId, Integer year, Integer semester) {
        return fitnessTestRepository.findByClassIdAndYearAndSemester(classId, year, semester);
    }

    public List<Student> getWeakStudents() {
        return studentRepository.findByIsWeak(true);
    }

    public List<FitnessTest> getUnqualifiedStudents(Integer year, Integer semester) {
        return fitnessTestRepository.findUnqualifiedByYearAndSemester(year, semester);
    }

    public List<ClassReportDTO> generateClassReport(Integer year, Integer semester) {
        List<ClassReportDTO> reports = new ArrayList<>();
        List<Student> allStudents = studentRepository.findAll();
        
        for (Student student : allStudents) {
            if (student.getClassInfo() == null) continue;
            
            Long classId = student.getClassInfo().getId();
            ClassReportDTO existingReport = null;
            
            for (ClassReportDTO report : reports) {
                if (report.getClassId().equals(classId)) {
                    existingReport = report;
                    break;
                }
            }
            
            if (existingReport == null) {
                existingReport = new ClassReportDTO();
                existingReport.setClassId(classId);
                existingReport.setClassName(student.getClassInfo().getClassName());
                existingReport.setTotalStudents(studentRepository.countByClassId(classId).intValue());
                existingReport.setTestedStudents(0);
                existingReport.setQualifiedStudents(0);
                existingReport.setLevelA(0);
                existingReport.setLevelB(0);
                existingReport.setLevelC(0);
                existingReport.setLevelD(0);
                reports.add(existingReport);
            }
            
            List<FitnessTest> tests = fitnessTestRepository.findByClassIdAndYearAndSemester(classId, year, semester);
            existingReport.setTestedStudents(tests.size());
            
            for (FitnessTest test : tests) {
                if (test.getIsQualified()) {
                    existingReport.setQualifiedStudents(existingReport.getQualifiedStudents() + 1);
                }
                
                String level = test.getLevel();
                if ("A".equals(level)) {
                    existingReport.setLevelA(existingReport.getLevelA() + 1);
                } else if ("B".equals(level)) {
                    existingReport.setLevelB(existingReport.getLevelB() + 1);
                } else if ("C".equals(level)) {
                    existingReport.setLevelC(existingReport.getLevelC() + 1);
                } else if ("D".equals(level)) {
                    existingReport.setLevelD(existingReport.getLevelD() + 1);
                }
            }
            
            Double avgScore = fitnessTestRepository.getAverageScoreByClassId(classId, year, semester);
            existingReport.setAverageScore(avgScore);
            
            if (existingReport.getTestedStudents() > 0) {
                Double qualifiedRate = (existingReport.getQualifiedStudents() * 100.0) / existingReport.getTestedStudents();
                existingReport.setQualifiedRate(qualifiedRate);
            }
        }
        
        return reports;
    }

    public FitnessTest save(FitnessTest fitnessTest) {
        if (fitnessTest.getId() == null) {
            fitnessTest.setCreateTime(new Date());
        }
        fitnessTest.setUpdateTime(new Date());
        return fitnessTestRepository.save(fitnessTest);
    }

    public void delete(Long id) {
        fitnessTestRepository.delete(id);
    }
}
