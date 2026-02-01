package dangod.springboot.controller.fitness;

import dangod.springboot.model.TrainingPlan;
import dangod.springboot.dto.TrainingPlanDto;
import dangod.springboot.service.FitnessTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/training-plans")
public class TrainingPlanController {

    @Autowired
    private FitnessTestService fitnessTestService;

    @PostMapping
    public ResponseEntity<TrainingPlan> createTrainingPlan(@Valid @RequestBody TrainingPlanDto planDto) {
        try {
            TrainingPlan plan = fitnessTestService.createTrainingPlan(planDto);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{planId}")
    public ResponseEntity<TrainingPlan> getTrainingPlanById(@PathVariable Long planId) {
        try {
            TrainingPlan plan = fitnessTestService.getTrainingPlanById(planId);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<TrainingPlan>> getTrainingPlansByMember(@PathVariable Long memberId) {
        List<TrainingPlan> plans = fitnessTestService.getTrainingPlansByMember(memberId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<TrainingPlan>> getActivePlansForMember(@PathVariable Long memberId) {
        List<TrainingPlan> plans = fitnessTestService.getActivePlansForMember(memberId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<TrainingPlan>> getTrainingPlansByTrainer(@PathVariable Long trainerId) {
        List<TrainingPlan> plans = fitnessTestService.getTrainingPlansByTrainer(trainerId);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<TrainingPlan>> getPlansForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TrainingPlan> plans = fitnessTestService.getPlansForDate(date);
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/expired-active")
    public ResponseEntity<List<TrainingPlan>> getExpiredActivePlans() {
        List<TrainingPlan> plans = fitnessTestService.getExpiredActivePlans();
        return ResponseEntity.ok(plans);
    }

    @GetMapping("/member/{memberId}/latest-active")
    public ResponseEntity<TrainingPlan> getLatestActivePlanForMember(@PathVariable Long memberId) {
        TrainingPlan plan = fitnessTestService.getLatestActivePlanForMember(memberId);
        if (plan != null) {
            return ResponseEntity.ok(plan);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{planId}")
    public ResponseEntity<TrainingPlan> updateTrainingPlan(@PathVariable Long planId, 
                                                          @Valid @RequestBody TrainingPlanDto planDto) {
        try {
            TrainingPlan plan = fitnessTestService.updateTrainingPlan(planId, planDto);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{planId}/deactivate")
    public ResponseEntity<TrainingPlan> deactivateTrainingPlan(@PathVariable Long planId) {
        try {
            TrainingPlan plan = fitnessTestService.deactivateTrainingPlan(planId);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/member/{memberId}/generate-from-test")
    public ResponseEntity<TrainingPlan> generateTrainingPlanFromLatestTest(@PathVariable Long memberId) {
        try {
            TrainingPlan plan = fitnessTestService.generateTrainingPlanFromLatestTest(memberId);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/member/{memberId}/health-report")
    public ModelAndView exportHealthReport(
            @PathVariable Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            return fitnessTestService.exportHealthReport(memberId, startDate, endDate);
        } catch (RuntimeException e) {
            // 返回错误页面
            ModelAndView modelAndView = new ModelAndView("error");
            modelAndView.addObject("errorMessage", e.getMessage());
            return modelAndView;
        }
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteTrainingPlan(@PathVariable Long planId) {
        try {
            fitnessTestService.deleteTrainingPlan(planId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}