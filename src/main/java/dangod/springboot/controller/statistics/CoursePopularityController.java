package dangod.springboot.controller.statistics;

import dangod.springboot.dto.CoursePopularityDto;
import dangod.springboot.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/course-popularity")
public class CoursePopularityController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<List<CoursePopularityDto>> getCoursePopularityRanking() {
        List<CoursePopularityDto> popularity = statisticsService.getCoursePopularityRanking();
        return ResponseEntity.ok(popularity);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<CoursePopularityDto>> getCoursePopularityRankingByStore(@PathVariable String storeId) {
        List<CoursePopularityDto> popularity = statisticsService.getCoursePopularityRankingByStore(storeId);
        return ResponseEntity.ok(popularity);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<CoursePopularityDto>> getCoursePopularityRankingByCategory(@PathVariable String category) {
        List<CoursePopularityDto> popularity = statisticsService.getCoursePopularityRankingByCategory(category);
        return ResponseEntity.ok(popularity);
    }

    @GetMapping("/time-range")
    public ResponseEntity<List<CoursePopularityDto>> getCoursePopularityRankingByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<CoursePopularityDto> popularity = statisticsService.getCoursePopularityRankingByTimeRange(startTime, endTime);
        return ResponseEntity.ok(popularity);
    }
}