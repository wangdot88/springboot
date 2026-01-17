package dangod.springboot.controller;

import dangod.springboot.common.Result;
import dangod.springboot.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@Api(tags = "数据统计")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/member/renewal-prediction")
    @ApiOperation("会员续费预测")
    public Result<List<Map<String, Object>>> predictMemberRenewal() {
        List<Map<String, Object>> predictions = statisticsService.predictMemberRenewal();
        return Result.success(predictions);
    }

    @GetMapping("/course/hot-rank/{days}/{limit}")
    @ApiOperation("课程热度榜")
    public Result<List<Map<String, Object>>> getCourseHotRank(@PathVariable int days, @PathVariable int limit) {
        List<Map<String, Object>> hotRank = statisticsService.getCourseHotRank(days, limit);
        return Result.success(hotRank);
    }

    @GetMapping("/equipment/usage-rate/{days}")
    @ApiOperation("器材使用率统计")
    public Result<List<Map<String, Object>>> getEquipmentUsageRate(@PathVariable int days) {
        List<Map<String, Object>> usageStats = statisticsService.getEquipmentUsageRate(days);
        return Result.success(usageStats);
    }

    @GetMapping("/dashboard/summary")
    @ApiOperation("仪表盘摘要")
    public Result<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = statisticsService.getDashboardSummary();
        return Result.success(summary);
    }

    @GetMapping("/member/activity-trend/{days}")
    @ApiOperation("会员活跃度趋势")
    public Result<List<Map<String, Object>>> getMemberActivityTrend(@PathVariable int days) {
        List<Map<String, Object>> trend = statisticsService.getMemberActivityTrend(days);
        return Result.success(trend);
    }
}
