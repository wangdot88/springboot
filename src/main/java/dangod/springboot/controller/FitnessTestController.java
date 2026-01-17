package dangod.springboot.controller;

import dangod.springboot.entity.FitnessTestRecord;
import dangod.springboot.entity.TrainingPlan;
import dangod.springboot.service.FitnessTestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("体测管理")
@RestController
@RequestMapping("/api/fitness")
public class FitnessTestController {

    @Autowired
    private FitnessTestService fitnessTestService;

    @ApiOperation("创建体测记录")
    @PostMapping("/test")
    public ResponseEntity<FitnessTestRecord> createFitnessTest(
            @RequestParam Long memberId,
            @RequestParam Long branchId,
            @RequestParam Long testerId,
            @RequestBody Map<String, BigDecimal> metrics) {
        FitnessTestRecord record = fitnessTestService.createFitnessTest(memberId, branchId, testerId, metrics);
        return ResponseEntity.ok(record);
    }

    @ApiOperation("获取会员体测历史")
    @GetMapping("/test/member/{memberId}")
    public ResponseEntity<List<FitnessTestRecord>> getMemberTestHistory(
            @PathVariable Long memberId) {
        List<FitnessTestRecord> records = fitnessTestService.getMemberTestHistory(memberId);
        return ResponseEntity.ok(records);
    }

    @ApiOperation("获取会员最新体测")
    @GetMapping("/test/member/{memberId}/latest")
    public ResponseEntity<FitnessTestRecord> getLatestTest(@PathVariable Long memberId) {
        FitnessTestRecord record = fitnessTestService.getLatestTest(memberId);
        return ResponseEntity.ok(record);
    }

    @ApiOperation("获取体测指标趋势数据")
    @GetMapping("/test/member/{memberId}/trend")
    public ResponseEntity<Map<String, List<BigDecimal>>> getMetricTrendData(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "6") int months) {
        Map<String, List<BigDecimal>> trendData = fitnessTestService.getMetricTrendData(memberId, months);
        return ResponseEntity.ok(trendData);
    }

    @ApiOperation("生成训练计划")
    @PostMapping("/training-plan")
    public ResponseEntity<TrainingPlan> generateTrainingPlan(
            @RequestParam Long memberId,
            @RequestParam String planName,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        // 简化处理，实际应该使用日期转换
        TrainingPlan plan = fitnessTestService.generateTrainingPlan(memberId, planName, new Date(), new Date());
        return ResponseEntity.ok(plan);
    }

    @ApiOperation("获取会员训练计划")
    @GetMapping("/training-plan/member/{memberId}")
    public ResponseEntity<List<TrainingPlan>> getMemberTrainingPlans(
            @PathVariable Long memberId) {
        List<TrainingPlan> plans = fitnessTestService.getMemberTrainingPlans(memberId);
        return ResponseEntity.ok(plans);
    }

    @ApiOperation("获取健康报告数据")
    @GetMapping("/health-report/member/{memberId}")
    public ResponseEntity<Map<String, Object>> getHealthReportData(
            @PathVariable Long memberId) {
        Map<String, Object> report = fitnessTestService.getHealthReportData(memberId);
        return ResponseEntity.ok(report);
    }

    @ApiOperation("导出健康报告")
    @GetMapping("/health-report/member/{memberId}/export")
    public ResponseEntity<String> exportHealthReport(@PathVariable Long memberId) {
        String reportPath = fitnessTestService.exportHealthReport(memberId);
        return ResponseEntity.ok(reportPath);
    }
}