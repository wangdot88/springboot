package dangod.springboot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_test", indexes = {
    @Index(name = "idx_member_date", columnList = "member_id, test_date")
})
public class BodyTest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime testDate;

    @Column(precision = 3, scale = 1)
    private BigDecimal height;

    @Column(precision = 4, scale = 1)
    private BigDecimal weight;

    @Column(precision = 3, scale = 1)
    private BigDecimal bmi;

    @Column(precision = 3, scale = 1)
    private BigDecimal bodyFatRate;

    @Column(precision = 3, scale = 1)
    private BigDecimal muscleMass;

    @Column(precision = 3, scale = 1)
    private BigDecimal fatMass;

    @Column(precision = 3, scale = 1)
    private BigDecimal visceralFat;

    @Column(precision = 3, scale = 1)
    private BigDecimal boneMass;

    @Column(precision = 3, scale = 1)
    private BigDecimal waterRate;

    @Column(precision = 3, scale = 1)
    private BigDecimal basalMetabolism;

    @Column(precision = 3, scale = 1)
    private BigDecimal muscleRate;

    @Column(precision = 3, scale = 1)
    private BigDecimal proteinRate;

    @Column(precision = 3, scale = 1)
    private BigDecimal leftArmFat;

    @Column(precision = 3, scale = 1)
    private BigDecimal rightArmFat;

    @Column(precision = 3, scale = 1)
    private BigDecimal leftLegFat;

    @Column(precision = 3, scale = 1)
    private BigDecimal rightLegFat;

    @Column(precision = 3, scale = 1)
    private BigDecimal trunkFat;

    @Column(precision = 3, scale = 1)
    private BigDecimal leftArmMuscle;

    @Column(precision = 3, scale = 1)
    private BigDecimal rightArmMuscle;

    @Column(precision = 3, scale = 1)
    private BigDecimal leftLegMuscle;

    @Column(precision = 3, scale = 1)
    private BigDecimal rightLegMuscle;

    @Column(precision = 3, scale = 1)
    private BigDecimal trunkMuscle;

    @Column(precision = 3, scale = 1)
    private BigDecimal metabolicAge;

    @Column(precision = 3, scale = 1)
    private BigDecimal visceralFatLevel;

    @Column(length = 50)
    private String healthStatus;

    @Column(length = 500)
    private String abnormalItems;

    @Column(length = 1000)
    private String suggestions;

    @Column(length = 50)
    private String createBy;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(length = 50)
    private String updateBy;

    private LocalDateTime updateTime;

    public BodyTest() {
        this.testDate = LocalDateTime.now();
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

    public LocalDateTime getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDateTime testDate) {
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

    public BigDecimal getBodyFatRate() {
        return bodyFatRate;
    }

    public void setBodyFatRate(BigDecimal bodyFatRate) {
        this.bodyFatRate = bodyFatRate;
    }

    public BigDecimal getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(BigDecimal muscleMass) {
        this.muscleMass = muscleMass;
    }

    public BigDecimal getFatMass() {
        return fatMass;
    }

    public void setFatMass(BigDecimal fatMass) {
        this.fatMass = fatMass;
    }

    public BigDecimal getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(BigDecimal visceralFat) {
        this.visceralFat = visceralFat;
    }

    public BigDecimal getBoneMass() {
        return boneMass;
    }

    public void setBoneMass(BigDecimal boneMass) {
        this.boneMass = boneMass;
    }

    public BigDecimal getWaterRate() {
        return waterRate;
    }

    public void setWaterRate(BigDecimal waterRate) {
        this.waterRate = waterRate;
    }

    public BigDecimal getBasalMetabolism() {
        return basalMetabolism;
    }

    public void setBasalMetabolism(BigDecimal basalMetabolism) {
        this.basalMetabolism = basalMetabolism;
    }

    public BigDecimal getMuscleRate() {
        return muscleRate;
    }

    public void setMuscleRate(BigDecimal muscleRate) {
        this.muscleRate = muscleRate;
    }

    public BigDecimal getProteinRate() {
        return proteinRate;
    }

    public void setProteinRate(BigDecimal proteinRate) {
        this.proteinRate = proteinRate;
    }

    public BigDecimal getLeftArmFat() {
        return leftArmFat;
    }

    public void setLeftArmFat(BigDecimal leftArmFat) {
        this.leftArmFat = leftArmFat;
    }

    public BigDecimal getRightArmFat() {
        return rightArmFat;
    }

    public void setRightArmFat(BigDecimal rightArmFat) {
        this.rightArmFat = rightArmFat;
    }

    public BigDecimal getLeftLegFat() {
        return leftLegFat;
    }

    public void setLeftLegFat(BigDecimal leftLegFat) {
        this.leftLegFat = leftLegFat;
    }

    public BigDecimal getRightLegFat() {
        return rightLegFat;
    }

    public void setRightLegFat(BigDecimal rightLegFat) {
        this.rightLegFat = rightLegFat;
    }

    public BigDecimal getTrunkFat() {
        return trunkFat;
    }

    public void setTrunkFat(BigDecimal trunkFat) {
        this.trunkFat = trunkFat;
    }

    public BigDecimal getLeftArmMuscle() {
        return leftArmMuscle;
    }

    public void setLeftArmMuscle(BigDecimal leftArmMuscle) {
        this.leftArmMuscle = leftArmMuscle;
    }

    public BigDecimal getRightArmMuscle() {
        return rightArmMuscle;
    }

    public void setRightArmMuscle(BigDecimal rightArmMuscle) {
        this.rightArmMuscle = rightArmMuscle;
    }

    public BigDecimal getLeftLegMuscle() {
        return leftLegMuscle;
    }

    public void setLeftLegMuscle(BigDecimal leftLegMuscle) {
        this.leftLegMuscle = leftLegMuscle;
    }

    public BigDecimal getRightLegMuscle() {
        return rightLegMuscle;
    }

    public void setRightLegMuscle(BigDecimal rightLegMuscle) {
        this.rightLegMuscle = rightLegMuscle;
    }

    public BigDecimal getTrunkMuscle() {
        return trunkMuscle;
    }

    public void setTrunkMuscle(BigDecimal trunkMuscle) {
        this.trunkMuscle = trunkMuscle;
    }

    public BigDecimal getMetabolicAge() {
        return metabolicAge;
    }

    public void setMetabolicAge(BigDecimal metabolicAge) {
        this.metabolicAge = metabolicAge;
    }

    public BigDecimal getVisceralFatLevel() {
        return visceralFatLevel;
    }

    public void setVisceralFatLevel(BigDecimal visceralFatLevel) {
        this.visceralFatLevel = visceralFatLevel;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getAbnormalItems() {
        return abnormalItems;
    }

    public void setAbnormalItems(String abnormalItems) {
        this.abnormalItems = abnormalItems;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
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
