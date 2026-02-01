package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_card_freeze_approval")
public class CardFreezeApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "card_id")
    private Long cardId;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "apply_reason")
    private String applyReason;
    
    @Column(name = "apply_time")
    private LocalDateTime applyTime;
    
    @Column(name = "applicant", length = 50)
    private String applicant;
    
    @Column(name = "approver", length = 50)
    private String approver;
    
    @Column(name = "approve_time")
    private LocalDateTime approveTime;
    
    @Column(name = "approve_result")
    private Integer approveResult;
    
    @Column(name = "reject_reason")
    private String rejectReason;
    
    @Column(name = "freeze_days")
    private Integer freezeDays;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
