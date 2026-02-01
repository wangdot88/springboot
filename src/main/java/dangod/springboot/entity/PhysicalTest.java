package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_physical_test")
public class PhysicalTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "test_date")
    private LocalDate testDate;
    
    @Column(name = "height", precision = 5, scale = 2)
    private java.math.BigDecimal height;
    
    @Column(name = "weight", precision = 5, scale = 2)
    private java.math.BigDecimal weight;
    
    @Column(name = "bmi", precision = 4, scale = 2)
    private java.math.BigDecimal bmi;
    
    @Column(name = "body_fat_rate", precision = 4, scale = 2)
    private java.math.BigDecimal bodyFatRate;
    
    @Column(name = "muscle_mass", precision = 5, scale = 2)
    private java.math.BigDecimal muscleMass;
    
    @Column(name = "water_rate", precision = 4, scale = 2)
    private java.math.BigDecimal waterRate;
    
    @Column(name = "bone_density", precision = 4, scale = 2)
    private java.math.BigDecimal boneDensity;
    
    @Column(name = "basal_metabolism")
    private Integer basalMetabolism;
    
    @Column(name = "heart_rate")
    private Integer heartRate;
    
    @Column(name = "blood_pressure_high")
    private Integer bloodPressureHigh;
    
    @Column(name = "blood_pressure_low")
    private Integer bloodPressureLow;
    
    @Column(name = "vital_capacity")
    private Integer vitalCapacity;
    
    @Column(name = "grip_strength", precision = 5, scale = 2)
    private java.math.BigDecimal gripStrength;
    
    @Column(name = "sit_reach", precision = 5, scale = 2)
    private java.math.BigDecimal sitReach;
    
    @Column(name = "push_ups")
    private Integer pushUps;
    
    @Column(name = "sit_ups")
    private Integer sitUps;
    
    @Column(name = "run_1000m")
    private Integer run1000m;
    
    @Column(name = "flexibility_score")
    private Integer flexibilityScore;
    
    @Column(name = "strength_score")
    private Integer strengthScore;
    
    @Column(name = "endurance_score")
    private Integer enduranceScore;
    
    @Column(name = "overall_score")
    private Integer overallScore;
    
    @Column(name = "tester", length = 50)
    private String tester;
    
    @Column(name = "gym_id")
    private Long gymId;
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
