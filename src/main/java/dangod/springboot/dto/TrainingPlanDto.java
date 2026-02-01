package dangod.springboot.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class TrainingPlanDto {
    
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    
    @NotNull(message = "教练ID不能为空")
    private Long trainerId;
    
    @NotBlank(message = "计划名称不能为空")
    private String planName;
    
    @NotBlank(message = "计划描述不能为空")
    private String description;
    
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;
    
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;
    
    @NotNull(message = "每周训练天数不能为空")
    @Positive(message = "每周训练天数必须大于0")
    private Integer weeklyFrequency;
    
    @NotNull(message = "每次训练时长(分钟)不能为空")
    @Positive(message = "每次训练时长必须大于0")
    private Integer sessionDuration;
    
    @NotNull(message = "主要目标不能为空")
    private String primaryGoal;
    
    private String secondaryGoals;
    
    private String exerciseDetails;
    
    private String nutritionGuidelines;
    
    private String notes;
    
    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(Integer weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    public Integer getSessionDuration() {
        return sessionDuration;
    }

    public void setSessionDuration(Integer sessionDuration) {
        this.sessionDuration = sessionDuration;
    }

    public String getPrimaryGoal() {
        return primaryGoal;
    }

    public void setPrimaryGoal(String primaryGoal) {
        this.primaryGoal = primaryGoal;
    }

    public String getSecondaryGoals() {
        return secondaryGoals;
    }

    public void setSecondaryGoals(String secondaryGoals) {
        this.secondaryGoals = secondaryGoals;
    }

    public String getExerciseDetails() {
        return exerciseDetails;
    }

    public void setExerciseDetails(String exerciseDetails) {
        this.exerciseDetails = exerciseDetails;
    }

    public String getNutritionGuidelines() {
        return nutritionGuidelines;
    }

    public void setNutritionGuidelines(String nutritionGuidelines) {
        this.nutritionGuidelines = nutritionGuidelines;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}