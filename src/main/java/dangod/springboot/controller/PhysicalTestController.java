package dangod.springboot.controller;

import dangod.springboot.core.common.Result;
import dangod.springboot.entity.PhysicalTest;
import dangod.springboot.entity.TrainingPlan;
import dangod.springboot.service.PhysicalTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Api(tags = "体测管理")
@RestController
@RequestMapping("/api/physical")
public class PhysicalTestController {

    @Autowired
    private PhysicalTestService physicalTestService;

    @ApiOperation("创建体测记录")
    @PostMapping("/tests")
    public Result<PhysicalTest> createTest(@RequestBody PhysicalTest test) {
        return Result.success(physicalTestService.createTest(test));
    }

    @ApiOperation("更新体测记录")
    @PutMapping("/tests/{id}")
    public Result<PhysicalTest> updateTest(@PathVariable Long id, @RequestBody PhysicalTest test) {
        test.setId(id);
        return Result.success(physicalTestService.updateTest(test));
    }

    @ApiOperation("删除体测记录")
    @DeleteMapping("/tests/{id}")
    public Result<Void> deleteTest(@PathVariable Long id) {
        physicalTestService.deleteTest(id);
        return Result.success(null);
    }

    @ApiOperation("获取体测详情")
    @GetMapping("/tests/{id}")
    public Result<PhysicalTest> getTestById(@PathVariable Long id) {
        return Result.success(physicalTestService.getTestById(id));
    }

    @ApiOperation("获取会员体测记录")
    @GetMapping("/tests/member/{memberId}")
    public Result<List<PhysicalTest>> getMemberTests(@PathVariable Long memberId) {
        return Result.success(physicalTestService.getMemberTests(memberId));
    }

    @ApiOperation("按日期范围获取体测记录")
    @GetMapping("/tests/member/{memberId}/range")
    public Result<List<PhysicalTest>> getMemberTestsByDateRange(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end) {
        return Result.success(physicalTestService.getMemberTestsByDateRange(memberId, start, end));
    }

    @ApiOperation("获取体测变化曲线图数据")
    @GetMapping("/tests/{memberId}/chart")
    public Result<Map<String, List<?>>> getTestChartData(@PathVariable Long memberId) {
        return Result.success(physicalTestService.getTestChartData(memberId));
    }

    @ApiOperation("生成训练计划")
    @PostMapping("/tests/{testId}/plan")
    public Result<TrainingPlan> generateTrainingPlan(@PathVariable Long testId) {
        return Result.success(physicalTestService.generateTrainingPlan(testId));
    }

    @ApiOperation("获取会员训练计划")
    @GetMapping("/plans/member/{memberId}")
    public Result<List<TrainingPlan>> getMemberPlans(@PathVariable Long memberId) {
        return Result.success(physicalTestService.getMemberPlans(memberId));
    }

    @ApiOperation("导出健康报告")
    @GetMapping("/tests/{memberId}/export")
    public ResponseEntity<byte[]> exportHealthReport(@PathVariable Long memberId) {
        try {
            byte[] report = physicalTestService.exportHealthReport(memberId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "health_report.xlsx");
            return ResponseEntity.ok().headers(headers).body(report);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
