package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_renewal_prediction")
public class RenewalPrediction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "card_id")
    private Long cardId;
    
    @Column(name = "probability", precision = 5, scale = 4)
    private BigDecimal probability;
    
    @Column(name = "prediction_model", length = 50)
    private String predictionModel;
    
    @Column(name = "features", columnDefinition = "TEXT")
    private String features;
    
    @Column(name = "recommend_level_id")
    private Long recommendLevelId;
    
    @Column(name = "recommend_reason")
    private String recommendReason;
    
    @Column(name = "prediction_date")
    private LocalDate predictionDate;
    
    @Column(name = "actual_renewal")
    private Integer actualRenewal;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
