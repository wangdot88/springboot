package dangod.springboot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_plan")
public class TrainingPlan implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "body_test_id", nullable = false)
    private BodyTest bodyTest;

    @Column(nullable = false, length = 100)
    private String planName;

    @Column(nullable = false, length = 50)
    private String goal;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private Integer durationWeeks;

    @Column(nullable = false)
    private Integer trainingDaysPerWeek;

    @Column(length = 2000)
    private String exercisePlan;

    @Column(length = 1000)
    private String nutritionSuggestion;

    @Column(length = 1000)
    private String restSuggestion;

    @Column(length = 500)
    private String progressTracking;

    @Column(length = 50)
    private String status;

    @Column(length = 50)
    private String createBy;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(length = 50)
    private String updateBy;

    private LocalDateTime updateTime;

    public TrainingPlan() {
        this.status = "ACTIVE";
        this.startDate = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public BodyTest getBodyTest() {
        return bodyTest;
    }

    public void setBodyTest(BodyTest bodyTest) {
        this.bodyTest = bodyTest;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public Integer getTrainingDaysPerWeek() {
        return trainingDaysPerWeek;
    }

    public void setTrainingDaysPerWeek(Integer trainingDaysPerWeek) {
        this.trainingDaysPerWeek = trainingDaysPerWeek;
    }

    public String getExercisePlan() {
        return exercisePlan;
    }

    public void setExercisePlan(String exercisePlan) {
        this.exercisePlan = exercisePlan;
    }

    public String getNutritionSuggestion() {
        return nutritionSuggestion;
    }

    public void setNutritionSuggestion(String nutritionSuggestion) {
        this.nutritionSuggestion = nutritionSuggestion;
    }

    public String getRestSuggestion() {
        return restSuggestion;
    }

    public void setRestSuggestion(String restSuggestion) {
        this.restSuggestion = restSuggestion;
    }

    public String getProgressTracking() {
        return progressTracking;
    }

    public void setProgressTracking(String progressTracking) {
        this.progressTracking = progressTracking;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
