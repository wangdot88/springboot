package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "fitness_metric", indexes = {
        @Index(name = "idx_metric_code", columnList = "metricCode")
})
public class FitnessMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "metric_code", unique = true, nullable = false, length = 50)
    private String metricCode;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "normal_min_male", precision = 10, scale = 2)
    private BigDecimal normalMinMale;

    @Column(name = "normal_max_male", precision = 10, scale = 2)
    private BigDecimal normalMaxMale;

    @Column(name = "normal_min_female", precision = 10, scale = 2)
    private BigDecimal normalMinFemale;

    @Column(name = "normal_max_female", precision = 10, scale = 2)
    private BigDecimal normalMaxFemale;

    @Column(name = "warning_threshold", precision = 3, scale = 2)
    private BigDecimal warningThreshold;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}