package dangod.springboot.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "test_score")
public class TestScore implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "test_year", nullable = false)
    private Integer testYear;

    @Column(name = "test_date")
    @Temporal(TemporalType.DATE)
    private Date testDate;

    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;

    @Column(name = "vital_capacity")
    private BigDecimal vitalCapacity;

    @Column(name = "sit_and_reach")
    private BigDecimal sitAndReach;

    @Column(name = "standing_long_jump")
    private BigDecimal standingLongJump;

    @Column(name = "50m_run")
    private BigDecimal _50mRun;

    @Column(name = "1000m_run")
    private BigDecimal _1000mRun;

    @Column(name = "800m_run")
    private BigDecimal _800mRun;

    @Column(name = "pull_up")
    private Integer pullUp;

    @Column(name = "sit_up")
    private Integer sitUp;

    @Column(name = "total_score")
    private BigDecimal totalScore;

    private String level;

    @Column(name = "is_pass")
    private Boolean isPass = false;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime = new Date();

    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime = new Date();

    @Transient
    private String studentName;
    @Transient
    private String studentNo;
    @Transient
    private String className;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getTestYear() {
        return testYear;
    }

    public void setTestYear(Integer testYear) {
        this.testYear = testYear;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }

    public BigDecimal getVitalCapacity() {
        return vitalCapacity;
    }

    public void setVitalCapacity(BigDecimal vitalCapacity) {
        this.vitalCapacity = vitalCapacity;
    }

    public BigDecimal getSitAndReach() {
        return sitAndReach;
    }

    public void setSitAndReach(BigDecimal sitAndReach) {
        this.sitAndReach = sitAndReach;
    }

    public BigDecimal getStandingLongJump() {
        return standingLongJump;
    }

    public void setStandingLongJump(BigDecimal standingLongJump) {
        this.standingLongJump = standingLongJump;
    }

    public BigDecimal get_50mRun() {
        return _50mRun;
    }

    public void set_50mRun(BigDecimal _50mRun) {
        this._50mRun = _50mRun;
    }

    public BigDecimal get_1000mRun() {
        return _1000mRun;
    }

    public void set_1000mRun(BigDecimal _1000mRun) {
        this._1000mRun = _1000mRun;
    }

    public BigDecimal get_800mRun() {
        return _800mRun;
    }

    public void set_800mRun(BigDecimal _800mRun) {
        this._800mRun = _800mRun;
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

    public BigDecimal getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore) {
        this.totalScore = totalScore;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Boolean getIsPass() {
        return isPass;
    }

    public void setIsPass(Boolean isPass) {
        this.isPass = isPass;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
