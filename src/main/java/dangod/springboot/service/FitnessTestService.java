package dangod.springboot.service;

import dangod.springboot.model.FitnessTest;
import dangod.springboot.model.TrainingPlan;
import dangod.springboot.dto.FitnessTestDto;
import dangod.springboot.dto.TrainingPlanDto;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FitnessTestService {
    
    FitnessTest createFitnessTest(FitnessTestDto testDto);
    
    FitnessTest getFitnessTestById(Long testId);
    
    List<FitnessTest> getFitnessTestsByMember(Long memberId);
    
    List<FitnessTest> getFitnessTestsForMemberOrdered(Long memberId);
    
    List<FitnessTest> getFitnessTestsBetween(LocalDate startDate, LocalDate endDate);
    
    List<FitnessTest> getFitnessTestsByDate(LocalDate date);
    
    List<FitnessTest> getAbnormalWeightTests(Double minWeight, Double maxWeight);
    
    List<FitnessTest> getAbnormalBodyFatTests(Double minBfp, Double maxBfp);
    
    List<FitnessTest> getAbnormalHeartRateTests(Integer minHr, Integer maxHr);
    
    List<FitnessTest> getAbnormalBloodPressureTests(Integer maxSystolic, Integer maxDiastolic);
    
    List<FitnessTest> getAbnormalBMITests(Double minBmi, Double maxBmi);
    
    FitnessTest getLatestTestForMember(Long memberId);
    
    List<FitnessTest> getAllAbnormalTests();
    
    void sendAbnormalTestAlerts();
    
    TrainingPlan createTrainingPlan(TrainingPlanDto planDto);
    
    TrainingPlan getTrainingPlanById(Long planId);
    
    List<TrainingPlan> getTrainingPlansByMember(Long memberId);
    
    List<TrainingPlan> getActivePlansForMember(Long memberId);
    
    List<TrainingPlan> getTrainingPlansByTrainer(Long trainerId);
    
    List<TrainingPlan> getPlansForDate(LocalDate date);
    
    List<TrainingPlan> getExpiredActivePlans();
    
    TrainingPlan getLatestActivePlanForMember(Long memberId);
    
    TrainingPlan updateTrainingPlan(Long planId, TrainingPlanDto planDto);
    
    TrainingPlan deactivateTrainingPlan(Long planId);
    
    TrainingPlan generateTrainingPlanFromLatestTest(Long memberId);
    
    ModelAndView exportHealthReport(Long memberId, LocalDate startDate, LocalDate endDate);
    
    Map<String, Object> getFitnessTestChartData(Long memberId, LocalDate startDate, LocalDate endDate);
    
    void deleteFitnessTest(Long testId);
    
    void deleteTrainingPlan(Long planId);
}