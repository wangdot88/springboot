package dangod.springboot.controller.fitness;

import dangod.springboot.model.FitnessTest;
import dangod.springboot.model.TrainingPlan;
import dangod.springboot.dto.FitnessTestDto;
import dangod.springboot.dto.TrainingPlanDto;
import dangod.springboot.service.FitnessTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fitness-tests")
public class FitnessTestController {

    @Autowired
    private FitnessTestService fitnessTestService;

    @PostMapping
    public ResponseEntity<FitnessTest> createFitnessTest(@Valid @RequestBody FitnessTestDto testDto) {
        try {
            FitnessTest test = fitnessTestService.createFitnessTest(testDto);
            return ResponseEntity.ok(test);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{testId}")
    public ResponseEntity<FitnessTest> getFitnessTestById(@PathVariable Long testId) {
        try {
            FitnessTest test = fitnessTestService.getFitnessTestById(testId);
            return ResponseEntity.ok(test);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<FitnessTest>> getFitnessTestsByMember(@PathVariable Long memberId) {
        List<FitnessTest> tests = fitnessTestService.getFitnessTestsByMember(memberId);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/member/{memberId}/ordered")
    public ResponseEntity<List<FitnessTest>> getFitnessTestsForMemberOrdered(@PathVariable Long memberId) {
        List<FitnessTest> tests = fitnessTestService.getFitnessTestsForMemberOrdered(memberId);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/between")
    public ResponseEntity<List<FitnessTest>> getFitnessTestsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<FitnessTest> tests = fitnessTestService.getFitnessTestsBetween(startDate, endDate);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<FitnessTest>> getFitnessTestsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<FitnessTest> tests = fitnessTestService.getFitnessTestsByDate(date);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/abnormal/weight")
    public ResponseEntity<List<FitnessTest>> getAbnormalWeightTests(
            @RequestParam(defaultValue = "40.0") Double minWeight,
            @RequestParam(defaultValue = "150.0") Double maxWeight) {
        List<FitnessTest> tests = fitnessTestService.getAbnormalWeightTests(minWeight, maxWeight);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/abnormal/body-fat")
    public ResponseEntity<List<FitnessTest>> getAbnormalBodyFatTests(
            @RequestParam(defaultValue = "5.0") Double minBfp,
            @RequestParam(defaultValue = "50.0") Double maxBfp) {
        List<FitnessTest> tests = fitnessTestService.getAbnormalBodyFatTests(minBfp, maxBfp);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/abnormal/heart-rate")
    public ResponseEntity<List<FitnessTest>> getAbnormalHeartRateTests(
            @RequestParam(defaultValue = "40") Integer minHr,
            @RequestParam(defaultValue = "120") Integer maxHr) {
        List<FitnessTest> tests = fitnessTestService.getAbnormalHeartRateTests(minHr, maxHr);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/abnormal/blood-pressure")
    public ResponseEntity<List<FitnessTest>> getAbnormalBloodPressureTests(
            @RequestParam(defaultValue = "140") Integer maxSystolic,
            @RequestParam(defaultValue = "90") Integer maxDiastolic) {
        List<FitnessTest> tests = fitnessTestService.getAbnormalBloodPressureTests(maxSystolic, maxDiastolic);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/abnormal/bmi")
    public ResponseEntity<List<FitnessTest>> getAbnormalBMITests(
            @RequestParam(defaultValue = "16.0") Double minBmi,
            @RequestParam(defaultValue = "30.0") Double maxBmi) {
        List<FitnessTest> tests = fitnessTestService.getAbnormalBMITests(minBmi, maxBmi);
        return ResponseEntity.ok(tests);
    }

    @GetMapping("/member/{memberId}/latest")
    public ResponseEntity<FitnessTest> getLatestTestForMember(@PathVariable Long memberId) {
        FitnessTest test = fitnessTestService.getLatestTestForMember(memberId);
        if (test != null) {
            return ResponseEntity.ok(test);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/abnormal/all")
    public ResponseEntity<List<FitnessTest>> getAllAbnormalTests() {
        List<FitnessTest> tests = fitnessTestService.getAllAbnormalTests();
        return ResponseEntity.ok(tests);
    }

    @PostMapping("/send-alerts")
    public ResponseEntity<Void> sendAbnormalTestAlerts() {
        fitnessTestService.sendAbnormalTestAlerts();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member/{memberId}/chart-data")
    public ResponseEntity<Map<String, Object>> getFitnessTestChartData(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> chartData = fitnessTestService.getFitnessTestChartData(memberId, startDate, endDate);
        return ResponseEntity.ok(chartData);
    }

    @DeleteMapping("/{testId}")
    public ResponseEntity<Void> deleteFitnessTest(@PathVariable Long testId) {
        try {
            fitnessTestService.deleteFitnessTest(testId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}