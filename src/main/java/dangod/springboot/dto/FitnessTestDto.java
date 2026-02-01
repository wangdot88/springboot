package dangod.springboot.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.time.LocalDate;

public class FitnessTestDto {
    
    @NotNull(message = "会员ID不能为空")
    private Long memberId;
    
    @NotNull(message = "测试日期不能为空")
    private LocalDate testDate;
    
    @NotNull(message = "体重不能为空")
    @Positive(message = "体重必须大于0")
    private Double weight;
    
    @NotNull(message = "身高不能为空")
    @Positive(message = "身高必须大于0")
    private Double height;
    
    @Min(value = 0, message = "体脂率不能小于0")
    @Max(value = 100, message = "体脂率不能大于100")
    private Double bodyFatPercentage;
    
    @Min(value = 0, message = "肌肉量不能小于0")
    private Double muscleMass;
    
    @Min(value = 0, message = "水分含量不能小于0")
    private Double waterContent;
    
    @Min(value = 0, message = "基础代谢率不能小于0")
    private Integer basalMetabolicRate;
    
    @Min(value = 0, message = "心率不能小于0")
    private Integer heartRate;
    
    @Min(value = 0, message = "收缩压不能小于0")
    private Integer bloodPressureSystolic;
    
    @Min(value = 0, message = "舒张压不能小于0")
    private Integer bloodPressureDiastolic;
    
    @Min(value = 0, message = "肺活量不能小于0")
    private Integer lungCapacity;
    
    @Min(value = 0, message = "柔韧性不能小于0")
    private Integer flexibility;
    
    private String notes;
    
    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getBodyFatPercentage() {
        return bodyFatPercentage;
    }

    public void setBodyFatPercentage(Double bodyFatPercentage) {
        this.bodyFatPercentage = bodyFatPercentage;
    }

    public Double getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(Double muscleMass) {
        this.muscleMass = muscleMass;
    }

    public Double getWaterContent() {
        return waterContent;
    }

    public void setWaterContent(Double waterContent) {
        this.waterContent = waterContent;
    }

    public Integer getBasalMetabolicRate() {
        return basalMetabolicRate;
    }

    public void setBasalMetabolicRate(Integer basalMetabolicRate) {
        this.basalMetabolicRate = basalMetabolicRate;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
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

    public Integer getLungCapacity() {
        return lungCapacity;
    }

    public void setLungCapacity(Integer lungCapacity) {
        this.lungCapacity = lungCapacity;
    }

    public Integer getFlexibility() {
        return flexibility;
    }

    public void setFlexibility(Integer flexibility) {
        this.flexibility = flexibility;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}