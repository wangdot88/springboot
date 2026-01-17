package dangod.springboot.controller;

import dangod.springboot.common.Result;
import dangod.springboot.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/renewal-prediction/{memberId}")
    public Result<Map<String, Object>> predictMemberRenewal(@PathVariable Long memberId) {
        return Result.success(statisticsService.predictMemberRenewal(memberId));
    }

    @GetMapping("/course-popularity")
    public Result<List<Map<String, Object>>> getCoursePopularityRanking(
            @RequestParam(required = false) Long storeId,
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(statisticsService.getCoursePopularityRanking(storeId, limit));
    }

    @GetMapping("/equipment-usage/{equipmentId}")
    public Result<Map<String, Object>> getEquipmentUsageStatistics(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.success(statisticsService.getEquipmentUsageStatistics(equipmentId, startDate, endDate));
    }

    @GetMapping("/equipment-usage")
    public Result<List<Map<String, Object>>> getAllEquipmentUsageStatistics(
            @RequestParam(required = false) Long storeId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.success(statisticsService.getAllEquipmentUsageStatistics(storeId, startDate, endDate));
    }

    @GetMapping("/overall")
    public Result<Map<String, Object>> getOverallStatistics(
            @RequestParam(required = false) Long storeId) {
        return Result.success(statisticsService.getOverallStatistics(storeId));
    }

    @GetMapping("/member-distribution/level")
    public Result<Map<String, Object>> getMemberDistributionByLevel(
            @RequestParam(required = false) Long storeId) {
        return Result.success(statisticsService.getMemberDistributionByLevel(storeId));
    }

    @GetMapping("/member-distribution/status")
    public Result<Map<String, Object>> getMemberDistributionByStatus(
            @RequestParam(required = false) Long storeId) {
        return Result.success(statisticsService.getMemberDistributionByStatus(storeId));
    }
}
