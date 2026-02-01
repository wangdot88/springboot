package dangod.springboot.model;

import dangod.springboot.enums.MemberCardType;
import dangod.springboot.enums.MemberStatus;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String memberNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private MemberCardType cardType;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private MemberStatus status;

    @Column(nullable = false)
    private LocalDate joinDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(precision = 10, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private String storeId;

    private String freezeReason;
    private LocalDate freezeDate;
    private Long freezeApprovedBy;

    @Column(nullable = false)
    private Boolean chainStoreAccess = true;

    @Column(nullable = false)
    private Integer totalSessions = 0;

    @Column(nullable = false)
    private Integer usedSessions = 0;

    @Column(nullable = false)
    private LocalDate lastVisitDate;

    @Column(nullable = false)
    private Boolean renewalReminderSent = false;

    public Member() {
    }

    public Member(String memberNumber, String name, String phone, String email, 
                  MemberCardType cardType, LocalDate expiryDate, String storeId) {
        this.memberNumber = memberNumber;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.cardType = cardType;
        this.status = MemberStatus.ACTIVE;
        this.joinDate = LocalDate.now();
        this.expiryDate = expiryDate;
        this.balance = BigDecimal.ZERO;
        this.storeId = storeId;
        this.lastVisitDate = LocalDate.now();
    }

    public Integer getRemainingSessions() {
        return totalSessions - usedSessions;
    }

    public Boolean isExpiringSoon() {
        return expiryDate.minusDays(7).isBefore(LocalDate.now()) && expiryDate.isAfter(LocalDate.now());
    }

    public Boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public MemberCardType getCardType() {
        return cardType;
    }

    public void setCardType(MemberCardType cardType) {
        this.cardType = cardType;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getFreezeReason() {
        return freezeReason;
    }

    public void setFreezeReason(String freezeReason) {
        this.freezeReason = freezeReason;
    }

    public LocalDate getFreezeDate() {
        return freezeDate;
    }

    public void setFreezeDate(LocalDate freezeDate) {
        this.freezeDate = freezeDate;
    }

    public Long getFreezeApprovedBy() {
        return freezeApprovedBy;
    }

    public void setFreezeApprovedBy(Long freezeApprovedBy) {
        this.freezeApprovedBy = freezeApprovedBy;
    }

    public Boolean getChainStoreAccess() {
        return chainStoreAccess;
    }

    public void setChainStoreAccess(Boolean chainStoreAccess) {
        this.chainStoreAccess = chainStoreAccess;
    }

    public Integer getTotalSessions() {
        return totalSessions;
    }

    public void setTotalSessions(Integer totalSessions) {
        this.totalSessions = totalSessions;
    }

    public Integer getUsedSessions() {
        return usedSessions;
    }

    public void setUsedSessions(Integer usedSessions) {
        this.usedSessions = usedSessions;
    }

    public LocalDate getLastVisitDate() {
        return lastVisitDate;
    }

    public void setLastVisitDate(LocalDate lastVisitDate) {
        this.lastVisitDate = lastVisitDate;
    }

    public Boolean getRenewalReminderSent() {
        return renewalReminderSent;
    }

    public void setRenewalReminderSent(Boolean renewalReminderSent) {
        this.renewalReminderSent = renewalReminderSent;
    }
}