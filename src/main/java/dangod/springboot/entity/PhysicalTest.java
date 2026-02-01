package dangod.springboot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "physical_test")
public class PhysicalTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(nullable = false)
    private String testType;
    
    @Column(nullable = false)
    private Integer score;
    
    @Column(nullable = false)
    private String grade;
    
    @Column
    private Integer run50m;
    
    @Column
    private Integer sitAndReach;
    
    @Column
    private Integer longJump;
    
    @Column
    private Integer pullUp;
    
    @Column
    private Integer sitUp;
    
    @Column
    private Integer run800m;
    
    @Column
    private Integer run1000m;
    
    @Column
    private LocalDateTime testDate;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    public PhysicalTest() {}
    
    public PhysicalTest(Student student, String testType, Integer score, String grade, LocalDateTime testDate) {
        this.student = student;
        this.testType = testType;
        this.score = score;
        this.grade = grade;
        this.testDate = testDate;
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
    
    public String getTestType() {
        return testType;
    }
    
    public void setTestType(String testType) {
        this.testType = testType;
    }
    
    public Integer getScore() {
        return score;
    }
    
    public void setScore(Integer score) {
        this.score = score;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public Integer getRun50m() {
        return run50m;
    }
    
    public void setRun50m(Integer run50m) {
        this.run50m = run50m;
    }
    
    public Integer getSitAndReach() {
        return sitAndReach;
    }
    
    public void setSitAndReach(Integer sitAndReach) {
        this.sitAndReach = sitAndReach;
    }
    
    public Integer getLongJump() {
        return longJump;
    }
    
    public void setLongJump(Integer longJump) {
        this.longJump = longJump;
    }
    
    public Integer getPullUp() {
        return pullUp;
    }
    
    public void setPullUp(Integer pullUp) {
        this.pullUp = pullUp;
    }
    
    public Integer getSitUp() {
        return sitUp;
    }
    
    public void setSitUp(Integer sitUp) {
        this.sitUp = sitUp;
    }
    
    public Integer getRun800m() {
        return run800m;
    }
    
    public void setRun800m(Integer run800m) {
        this.run800m = run800m;
    }
    
    public Integer getRun1000m() {
        return run1000m;
    }
    
    public void setRun1000m(Integer run1000m) {
        this.run1000m = run1000m;
    }
    
    public LocalDateTime getTestDate() {
        return testDate;
    }
    
    public void setTestDate(LocalDateTime testDate) {
        this.testDate = testDate;
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