package dangod.springboot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tb_fitness_test")
public class FitnessTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer semester;

    @Column
    private Double run1000;

    @Column
    private Double run800;

    @Column
    private Double run50;

    @Column
    private Integer sitAndReach;

    @Column
    private Integer standingLongJump;

    @Column
    private Integer sitUp;

    @Column
    private Integer pullUp;

    @Column
    private Double bmi;

    @Column
    private Double totalScore;

    @Column(length = 10)
    private String level;

    @Column
    private Boolean isQualified;

    @Column
    private Date createTime;

    @Column
    private Date updateTime;

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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Double getRun1000() {
        return run1000;
    }

    public void setRun1000(Double run1000) {
        this.run1000 = run1000;
    }

    public Double getRun800() {
        return run800;
    }

    public void setRun800(Double run800) {
        this.run800 = run800;
    }

    public Double getRun50() {
        return run50;
    }

    public void setRun50(Double run50) {
        this.run50 = run50;
    }

    public Integer getSitAndReach() {
        return sitAndReach;
    }

    public void setSitAndReach(Integer sitAndReach) {
        this.sitAndReach = sitAndReach;
    }

    public Integer getStandingLongJump() {
        return standingLongJump;
    }

    public void setStandingLongJump(Integer standingLongJump) {
        this.standingLongJump = standingLongJump;
    }

    public Integer getSitUp() {
        return sitUp;
    }

    public void setSitUp(Integer sitUp) {
        this.sitUp = sitUp;
    }

    public Integer getPullUp() {
        return pullUp;
    }

    public void setPullUp(Integer pullUp) {
        this.pullUp = pullUp;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getIsQualified() {
        return isQualified;
    }

    public void setIsQualified(Boolean isQualified) {
        this.isQualified = isQualified;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
