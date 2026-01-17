package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "fitness_test_detail", indexes = {
        @Index(name = "idx_record_id", columnList = "recordId"),
        @Index(name = "idx_metric_code", columnList = "metricCode")
})
public class FitnessTestDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_id", nullable = false)
    private Long recordId;

    @Column(name = "metric_code", nullable = false, length = 50)
    private String metricCode;

    @Column(name = "metric_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal metricValue;

    @Column(name = "is_abnormal", nullable = false)
    private Integer isAbnormal = 0;

    @Column(name = "suggestion", length = 500)
    private String suggestion;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}