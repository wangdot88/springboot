package dangod.springboot.controller.fitness;

import dangod.springboot.core.common.Result;
import dangod.springboot.entity.fitness.FitnessTest;
import dangod.springboot.entity.fitness.FitnessTestReport;
import dangod.springboot.service.fitness.FitnessTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fitness")
public class FitnessTestController {

    @Autowired
    private FitnessTestService fitnessTestService;

    @PostMapping("/import")
    public Result<Map<String, Object>> importFitnessTestData(
            @RequestParam("file") MultipartFile file,
            @RequestParam("semester") String semester) {
        try {
            Map<String, Object> result = fitnessTestService.importFitnessTestData(file, semester);
            return Result.success("导入成功", result);
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    @GetMapping("/reports")
    public Result<List<FitnessTestReport>> getReports(@RequestParam("semester") String semester) {
        List<FitnessTestReport> reports = fitnessTestService.getClassReports(semester);
        return Result.success(reports);
    }

    @GetMapping("/weak-students")
    public Result<List<FitnessTest>> getWeakStudents(@RequestParam("semester") String semester) {
        List<FitnessTest> weakStudents = fitnessTestService.getWeakStudents(semester);
        return Result.success(weakStudents);
    }

    @GetMapping("/student/{studentId}")
    public Result<List<FitnessTest>> getStudentTests(@PathVariable Long studentId) {
        List<FitnessTest> tests = fitnessTestService.getStudentTests(studentId);
        return Result.success(tests);
    }

    @PostMapping("/generate-reports")
    public Result<Void> generateReports(@RequestParam("semester") String semester) {
        fitnessTestService.generateClassReports(semester);
        return Result.success("报告生成成功", null);
    }
}
