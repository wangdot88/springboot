package dangod.springboot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "body_measurement")
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column
    private Double height;

    @Column
    private Double weight;

    @Column
    private Double bodyFat;

    @Column
    private Double muscleMass;

    @Column
    private Double bmi;

    @Column
    private Double visceralFat;

    @Column
    private Double basalMetabolicRate;

    @Column
    private Integer bloodPressureSystolic;

    @Column
    private Integer bloodPressureDiastolic;

    @Column
    private Integer heartRate;

    @Column
    private Double flexibility;

    @Column
    private Double endurance;

    @Column
    private Double strength;

    @Column
    private String healthScore;

    @Column
    private Boolean hasAbnormalIndicator;

    @Column
    private String abnormalIndicators;

    @Column
    private Date createTime;

    @Column
    private Date updateTime;

    public BodyMeasurement() {
        this.createTime = new Date();
        this.updateTime = new Date();
        this.hasAbnormalIndicator = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(Double bodyFat) {
        this.bodyFat = bodyFat;
    }

    public Double getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(Double muscleMass) {
        this.muscleMass = muscleMass;
    }

    public Double getBmi() {
        return bmi;
    }

    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }

    public Double getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(Double visceralFat) {
        this.visceralFat = visceralFat;
    }

    public Double getBasalMetabolicRate() {
        return basalMetabolicRate;
    }

    public void setBasalMetabolicRate(Double basalMetabolicRate) {
        this.basalMetabolicRate = basalMetabolicRate;
    }

    public Integer getBloodPressureSystolic() {
        return bloodPressureSystolic;
    }

    public void setBloodPressureSystolic(Integer bloodPressureSystolic) {
        this.bloodPressureSystolic = bloodPressureSystolic;
    }

    public Integer getBloodPressureDiastolic() {
        return bloodPressureDiastolic;
    }

    public void setBloodPressureDiastolic(Integer bloodPressureDiastolic) {
        this.bloodPressureDiastolic = bloodPressureDiastolic;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Double getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(Double flexibility) {
        this.flexibility = flexibility;
    }

    public Double getEndurance() {
        return endurance;
    }

    public void setEndurance(Double endurance) {
        this.endurance = endurance;
    }

    public Double getStrength() {
        return strength;
    }

    public void setStrength(Double strength) {
        this.strength = strength;
    }

    public String getHealthScore() {
        return healthScore;
    }

    public void setHealthScore(String healthScore) {
        this.healthScore = healthScore;
    }

    public Boolean getHasAbnormalIndicator() {
        return hasAbnormalIndicator;
    }

    public void setHasAbnormalIndicator(Boolean hasAbnormalIndicator) {
        this.hasAbnormalIndicator = hasAbnormalIndicator;
    }

    public String getAbnormalIndicators() {
        return abnormalIndicators;
    }

    public void setAbnormalIndicators(String abnormalIndicators) {
        this.abnormalIndicators = abnormalIndicators;
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
