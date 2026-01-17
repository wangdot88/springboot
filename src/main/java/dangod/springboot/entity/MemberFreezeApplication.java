package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "member_freeze_application", indexes = {
        @Index(name = "idx_member_id", columnList = "memberId"),
        @Index(name = "idx_status", columnList = "status")
})
public class MemberFreezeApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(length = 500)
    private String reason;

    @Column(name = "request_days", nullable = false)
    private Integer requestDays;

    @Column(name = "apply_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date applyTime;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approve_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date approveTime;

    @Column(columnDefinition = "TINYINT default 0")
    private Integer status;

    @Column(length = 200)
    private String remark;

    @PrePersist
    protected void onCreate() {
        applyTime = new Date();
    }
}
