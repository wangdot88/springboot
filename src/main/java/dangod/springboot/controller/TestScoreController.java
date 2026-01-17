package dangod.springboot.controller;

import dangod.springboot.core.common.Result;
import dangod.springboot.service.TestScoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Api(tags = "体测管理")
@RestController
@RequestMapping("/api/test-score")
public class TestScoreController {

    @Autowired
    private TestScoreService testScoreService;

    @ApiOperation("导入体测成绩Excel")
    @PostMapping("/import")
    public Result importTestScores(
            @ApiParam(value = "Excel文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "年份", required = true) @RequestParam("year") Integer year) {
        try {
            int count = testScoreService.importTestScores(file, year);
            return Result.success("成功导入" + count + "条体测成绩数据");
        } catch (Exception e) {
            return Result.error("导入失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取体测成绩列表")
    @GetMapping("/list")
    public Result getTestScoreList(
            @ApiParam("年份") @RequestParam(required = false) Integer year,
            @ApiParam("班级") @RequestParam(required = false) String className,
            @ApiParam("等级") @RequestParam(required = false) String level,
            @ApiParam("页码") @RequestParam(defaultValue = "0") int page,
            @ApiParam("每页数量") @RequestParam(defaultValue = "10") int size) {
        return Result.success(testScoreService.getTestScoreList(year, className, level, page, size));
    }

    @ApiOperation("获取班级体测合格率报告")
    @GetMapping("/class-qualified-rate")
    public Result getClassQualifiedRateReport(
            @ApiParam("年份") @RequestParam(required = false) Integer year,
            @ApiParam("院系") @RequestParam(required = false) String department) {
        List<Map<String, Object>> report = testScoreService.getClassQualifiedRateReport(year, department);
        return Result.success(report);
    }

    @ApiOperation("获取体弱学生列表")
    @GetMapping("/weak-students")
    public Result getWeakStudents() {
        return Result.success(testScoreService.getWeakStudents());
    }

    @ApiOperation("导出体测成绩报告")
    @GetMapping("/export")
    public void exportTestScoreReport(
            @ApiParam("年份") @RequestParam(required = false) Integer year,
            @ApiParam("班级") @RequestParam(required = false) String className,
            HttpServletResponse response) throws IOException {
        testScoreService.exportTestScoreReport(year, className, response);
    }
}
