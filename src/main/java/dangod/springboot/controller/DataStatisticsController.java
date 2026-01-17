package dangod.springboot.controller;

import dangod.springboot.service.DataStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api("数据统计")
@RestController
@RequestMapping("/api/statistics")
public class DataStatisticsController {

    @Autowired
    private DataStatisticsService dataStatisticsService;

    @ApiOperation("预测会员续费可能性")
    @GetMapping("/member/{memberId}/renewal-prediction")
    public ResponseEntity<Map<String, Object>> predictMemberRenewal(
            @PathVariable Long memberId) {
        Map<String, Object> prediction = dataStatisticsService.predictMemberRenewal(memberId);
        return ResponseEntity.ok(prediction);
    }

    @ApiOperation("获取课程热度榜")
    @GetMapping("/course/hot-rank")
    public ResponseEntity<List<Map<String, Object>>> getCourseHotRank(
            @RequestParam(defaultValue = "30") int days) {
        List<Map<String, Object>> hotRank = dataStatisticsService.getCourseHotRank(days);
        return ResponseEntity.ok(hotRank);
    }

    @ApiOperation("获取器材使用率统计")
    @GetMapping("/equipment/usage")
    public ResponseEntity<List<Map<String, Object>>> getEquipmentUsageStatistics(
            @RequestParam(required = false) Long branchId,
            @RequestParam(defaultValue = "30") int days) {
        List<Map<String, Object>> usageStatistics = dataStatisticsService.getEquipmentUsageStatistics(branchId, days);
        return ResponseEntity.ok(usageStatistics);
    }

    @ApiOperation("获取仪表板统计数据")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStatistics() {
        Map<String, Object> dashboard = dataStatisticsService.getDashboardStatistics();
        return ResponseEntity.ok(dashboard);
    }

    @ApiOperation("获取会员活跃度排名")
    @GetMapping("/member/activity-rank")
    public ResponseEntity<List<Map<String, Object>>> getMemberActivityRank(
            @RequestParam(defaultValue = "10") int topN) {
        List<Map<String, Object>> activityRank = dataStatisticsService.getMemberActivityRank(topN);
        return ResponseEntity.ok(activityRank);
    }

    @ApiOperation("获取门店统计数据")
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Map<String, Object>> getBranchStatistics(
            @PathVariable Long branchId) {
        Map<String, Object> branchStats = dataStatisticsService.getBranchStatistics(branchId);
        return ResponseEntity.ok(branchStats);
    }
}