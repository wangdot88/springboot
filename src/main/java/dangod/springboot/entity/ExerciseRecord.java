package dangod.springboot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercise_record")
public class ExerciseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(nullable = false)
    private String exerciseType;
    
    @Column(nullable = false)
    private Double distance;
    
    @Column(nullable = false)
    private Integer duration;
    
    @Column
    private Double calories;
    
    @Column
    private String location;
    
    @Column
    private LocalDateTime exerciseDate;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    public ExerciseRecord() {}
    
    public ExerciseRecord(Student student, String exerciseType, Double distance, Integer duration, LocalDateTime exerciseDate) {
        this.student = student;
        this.exerciseType = exerciseType;
        this.distance = distance;
        this.duration = duration;
        this.exerciseDate = exerciseDate;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public String getExerciseType() {
        return exerciseType;
    }
    
    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }
    
    public Double getDistance() {
        return distance;
    }
    
    public void setDistance(Double distance) {
        this.distance = distance;
    }
    
    public Integer getDuration() {
        return duration;
    }
    
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
    
    public Double getCalories() {
        return calories;
    }
    
    public void setCalories(Double calories) {
        this.calories = calories;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public LocalDateTime getExerciseDate() {
        return exerciseDate;
    }
    
    public void setExerciseDate(LocalDateTime exerciseDate) {
        this.exerciseDate = exerciseDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}