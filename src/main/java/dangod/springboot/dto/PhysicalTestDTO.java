package dangod.springboot.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class PhysicalTestDTO {
    private Long memberId;
    private LocalDate testDate;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal bmi;
    private BigDecimal bodyFatRate;
    private BigDecimal muscleMass;
    private BigDecimal waterRate;
    private BigDecimal boneDensity;
    private Integer basalMetabolism;
    private Integer heartRate;
    private Integer bloodPressureHigh;
    private Integer bloodPressureLow;
    private Integer vitalCapacity;
    private BigDecimal gripStrength;
    private BigDecimal sitReach;
    private Integer pushUps;
    private Integer sitUps;
    private Integer run1000m;
    private Long gymId;
    private String remark;
}
