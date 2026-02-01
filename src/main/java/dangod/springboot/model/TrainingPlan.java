package dangod.springboot.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "training_plans")
public class TrainingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fitness_test_id")
    private FitnessTest fitnessTest;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer durationWeeks;

    @Column(nullable = false)
    private String goal;

    @Column(nullable = false)
    private Integer difficultyLevel;

    @Column(nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "trainingPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TrainingSession> sessions = new ArrayList<>();

    public TrainingPlan() {
    }

    public TrainingPlan(String name, String description, Member member, Trainer trainer, 
                        FitnessTest fitnessTest, LocalDate startDate, LocalDate endDate, 
                        String goal, Integer difficultyLevel) {
        this.name = name;
        this.description = description;
        this.member = member;
        this.trainer = trainer;
        this.fitnessTest = fitnessTest;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.difficultyLevel = difficultyLevel;
        this.durationWeeks = (int) java.time.temporal.ChronoUnit.WEEKS.between(startDate, endDate);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public FitnessTest getFitnessTest() {
        return fitnessTest;
    }

    public void setFitnessTest(FitnessTest fitnessTest) {
        this.fitnessTest = fitnessTest;
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

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(Integer difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<TrainingSession> getSessions() {
        return sessions;
    }

    public void setSessions(List<TrainingSession> sessions) {
        this.sessions = sessions;
    }
}