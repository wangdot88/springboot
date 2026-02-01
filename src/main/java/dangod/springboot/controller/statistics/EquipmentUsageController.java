package dangod.springboot.controller.statistics;

import dangod.springboot.model.EquipmentUsage;
import dangod.springboot.dto.EquipmentUsageDto;
import dangod.springboot.dto.RenewalPredictionDto;
import dangod.springboot.dto.CoursePopularityDto;
import dangod.springboot.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment-usage")
public class EquipmentUsageController {

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping
    public ResponseEntity<EquipmentUsage> recordEquipmentUsage(@Valid @RequestBody EquipmentUsageDto usageDto) {
        try {
            EquipmentUsage usage = statisticsService.recordEquipmentUsage(usageDto);
            return ResponseEntity.ok(usage);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<EquipmentUsage>> getEquipmentUsagesByEquipment(@PathVariable String equipmentId) {
        List<EquipmentUsage> usages = statisticsService.getEquipmentUsagesByEquipment(equipmentId);
        return ResponseEntity.ok(usages);
    }

    @GetMapping("/between")
    public ResponseEntity<List<EquipmentUsage>> getEquipmentUsagesBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<EquipmentUsage> usages = statisticsService.getEquipmentUsagesBetween(startTime, endTime);
        return ResponseEntity.ok(usages);
    }

    @GetMapping("/store/{storeId}/between")
    public ResponseEntity<List<EquipmentUsage>> getEquipmentUsagesBetweenByStore(
            @PathVariable String storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<EquipmentUsage> usages = statisticsService.getEquipmentUsagesBetweenByStore(startTime, endTime, storeId);
        return ResponseEntity.ok(usages);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getEquipmentUsageStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Map<String, Object> stats = statisticsService.getEquipmentUsageStats(startTime, endTime);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/store/{storeId}/stats")
    public ResponseEntity<Map<String, Object>> getEquipmentUsageStatsByStore(
            @PathVariable String storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Map<String, Object> stats = statisticsService.getEquipmentUsageStatsByStore(startTime, endTime, storeId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/equipment/{equipmentId}/stats")
    public ResponseEntity<Map<String, Object>> getSpecificEquipmentUsageStats(
            @PathVariable String equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Map<String, Object> stats = statisticsService.getSpecificEquipmentUsageStats(equipmentId, startTime, endTime);
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{usageId}")
    public ResponseEntity<Void> deleteEquipmentUsage(@PathVariable Long usageId) {
        try {
            statisticsService.deleteEquipmentUsage(usageId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}