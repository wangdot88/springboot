package dangod.springboot.controller;

import dangod.springboot.common.Result;
import dangod.springboot.entity.BodyMeasurement;
import dangod.springboot.entity.TrainingPlan;
import dangod.springboot.service.BodyMeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/body-measurement")
public class BodyMeasurementController {

    @Autowired
    private BodyMeasurementService bodyMeasurementService;

    @PostMapping
    public Result<BodyMeasurement> createMeasurement(@RequestBody BodyMeasurement measurement) {
        return Result.success(bodyMeasurementService.createMeasurement(measurement));
    }

    @PutMapping("/{id}")
    public Result<BodyMeasurement> updateMeasurement(@PathVariable Long id, @RequestBody BodyMeasurement measurement) {
        return Result.success(bodyMeasurementService.updateMeasurement(id, measurement));
    }

    @GetMapping("/{id}")
    public Result<BodyMeasurement> getMeasurement(@PathVariable Long id) {
        return Result.success(bodyMeasurementService.getMeasurementById(id));
    }

    @GetMapping("/member/{memberId}")
    public Result<List<BodyMeasurement>> getMeasurementsByMember(@PathVariable Long memberId) {
        return Result.success(bodyMeasurementService.getMeasurementsByMember(memberId));
    }

    @GetMapping("/member/{memberId}/range")
    public Result<List<BodyMeasurement>> getMeasurementsByDateRange(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return Result.success(bodyMeasurementService.getMeasurementsByDateRange(memberId, startDate, endDate));
    }

    @GetMapping("/chart/{memberId}")
    public Result<Map<String, List<Object>>> getChartData(
            @PathVariable Long memberId,
            @RequestParam String indicator) {
        return Result.success(bodyMeasurementService.getMeasurementChartData(memberId, indicator));
    }

    @GetMapping("/abnormal")
    public Result<List<BodyMeasurement>> getAbnormalMeasurements() {
        return Result.success(bodyMeasurementService.getAbnormalMeasurements());
    }

    @GetMapping("/abnormal/{memberId}")
    public Result<List<BodyMeasurement>> getAbnormalMeasurementsByMember(@PathVariable Long memberId) {
        return Result.success(bodyMeasurementService.getAbnormalMeasurementsByMember(memberId));
    }

    @PostMapping("/training-plan/{memberId}")
    public Result<TrainingPlan> generateTrainingPlan(
            @PathVariable Long memberId,
            @RequestParam String goals) {
        return Result.success(bodyMeasurementService.generateTrainingPlan(memberId, goals));
    }

    @GetMapping("/training-plan/{memberId}/active")
    public Result<TrainingPlan> getActiveTrainingPlan(@PathVariable Long memberId) {
        return Result.success(bodyMeasurementService.getActiveTrainingPlan(memberId));
    }

    @GetMapping("/training-plan/{memberId}")
    public Result<List<TrainingPlan>> getTrainingPlans(@PathVariable Long memberId) {
        return Result.success(bodyMeasurementService.getTrainingPlans(memberId));
    }

    @PutMapping("/training-plan/{id}")
    public Result<TrainingPlan> updateTrainingPlan(@PathVariable Long id, @RequestBody TrainingPlan plan) {
        return Result.success(bodyMeasurementService.updateTrainingPlan(id, plan));
    }

    @GetMapping("/report/{memberId}")
    public Result<String> exportHealthReport(@PathVariable Long memberId) {
        return Result.success(bodyMeasurementService.exportHealthReport(memberId));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMeasurement(@PathVariable Long id) {
        bodyMeasurementService.deleteMeasurement(id);
        return Result.success();
    }
}
