package dangod.springboot.controller.statistics;

import dangod.springboot.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class GeneralStatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/overall")
    public ResponseEntity<Map<String, Object>> getOverallStatistics() {
        Map<String, Object> stats = statisticsService.getOverallStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<Map<String, Object>> getStoreStatistics(@PathVariable String storeId) {
        Map<String, Object> stats = statisticsService.getStoreStatistics(storeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Map<String, Object>> getMemberStatistics(@PathVariable Long memberId) {
        try {
            Map<String, Object> stats = statisticsService.getMemberStatistics(memberId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<Map<String, Object>> getTrainerStatistics(@PathVariable Long trainerId) {
        Map<String, Object> stats = statisticsService.getTrainerStatistics(trainerId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getCourseStatistics(@PathVariable Long courseId) {
        try {
            Map<String, Object> stats = statisticsService.getCourseStatistics(courseId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}