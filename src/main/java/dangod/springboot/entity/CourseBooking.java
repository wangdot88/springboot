package dangod.springboot.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "course_booking")
public class CourseBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column
    private Integer queuePosition;

    @Column
    private Boolean isSignedIn;

    @Column
    private Date signInTime;

    @Column
    private Date createTime;

    @Column
    private Date updateTime;

    public enum BookingStatus {
        CONFIRMED,
        QUEUED,
        CANCELLED,
        COMPLETED,
        NO_SHOW
    }

    public CourseBooking() {
        this.createTime = new Date();
        this.updateTime = new Date();
        this.status = BookingStatus.CONFIRMED;
        this.isSignedIn = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public Boolean getIsSignedIn() {
        return isSignedIn;
    }

    public void setIsSignedIn(Boolean isSignedIn) {
        this.isSignedIn = isSignedIn;
    }

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
