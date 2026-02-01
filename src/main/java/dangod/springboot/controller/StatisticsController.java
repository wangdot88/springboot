package dangod.springboot.controller;

import dangod.springboot.core.common.Result;
import dangod.springboot.entity.RenewalPrediction;
import dangod.springboot.service.StatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "数据统计")
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation("获取会员续费预测列表")
    @GetMapping("/renewal-predictions")
    public Result<List<RenewalPrediction>> getRenewalPredictions(
            @RequestParam(defaultValue = "1") int status) {
        return Result.success(statisticsService.getRenewalPredictions(status));
    }

    @ApiOperation("手动触发续费预测任务")
    @PostMapping("/renewal-predictions/trigger")
    public Result<Void> triggerRenewalPrediction() {
        statisticsService.dailyRenewalPrediction();
        return Result.success(null);
    }

    @ApiOperation("获取实时课程热度榜")
    @GetMapping("/courses/hot-ranking")
    public Result<List<Map<String, Object>>> getCourseHotRanking(@RequestParam Long gymId) {
        return Result.success(statisticsService.getCourseHotRanking(gymId));
    }

    @ApiOperation("获取器材使用率统计")
    @GetMapping("/equipment/usage")
    public Result<Map<String, Object>> getEquipmentUsageStatistics(@RequestParam Long gymId) {
        return Result.success(statisticsService.getEquipmentUsageStatistics(gymId));
    }

    @ApiOperation("获取整体统计数据")
    @GetMapping("/overall")
    public Result<Map<String, Object>> getOverallStatistics(@RequestParam Long gymId) {
        return Result.success(statisticsService.getOverallStatistics(gymId));
    }
}
