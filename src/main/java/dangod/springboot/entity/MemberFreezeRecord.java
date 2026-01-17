package dangod.springboot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "member_freeze_record")
public class MemberFreezeRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime freezeStartDate;

    @Column(nullable = false)
    private LocalDateTime freezeEndDate;

    @Column(nullable = false)
    private Integer freezeDays;

    @Column(length = 200)
    private String reason;

    @Column(nullable = false)
    private Integer status;

    @Column(length = 200)
    private String approvalOpinion;

    @Column(length = 50)
    private String approvalBy;

    private LocalDateTime approvalTime;

    @Column(length = 50)
    private String createBy;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(length = 50)
    private String updateBy;

    private LocalDateTime updateTime;

    public MemberFreezeRecord() {
        this.status = 0;
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

    public LocalDateTime getFreezeStartDate() {
        return freezeStartDate;
    }

    public void setFreezeStartDate(LocalDateTime freezeStartDate) {
        this.freezeStartDate = freezeStartDate;
    }

    public LocalDateTime getFreezeEndDate() {
        return freezeEndDate;
    }

    public void setFreezeEndDate(LocalDateTime freezeEndDate) {
        this.freezeEndDate = freezeEndDate;
    }

    public Integer getFreezeDays() {
        return freezeDays;
    }

    public void setFreezeDays(Integer freezeDays) {
        this.freezeDays = freezeDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getApprovalOpinion() {
        return approvalOpinion;
    }

    public void setApprovalOpinion(String approvalOpinion) {
        this.approvalOpinion = approvalOpinion;
    }

    public String getApprovalBy() {
        return approvalBy;
    }

    public void setApprovalBy(String approvalBy) {
        this.approvalBy = approvalBy;
    }

    public LocalDateTime getApprovalTime() {
        return approvalTime;
    }

    public void setApprovalTime(LocalDateTime approvalTime) {
        this.approvalTime = approvalTime;
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
