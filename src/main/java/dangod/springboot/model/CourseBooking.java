package dangod.springboot.model;

import dangod.springboot.enums.BookingStatus;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_bookings")
public class CourseBooking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.BOOKED;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    private LocalDateTime cancelTime;

    private LocalDateTime checkInTime;

    @Column(nullable = false)
    private Integer queuePosition;

    private String cancelReason;

    public CourseBooking() {
    }

    public CourseBooking(Member member, Course course, Integer queuePosition) {
        this.member = member;
        this.course = course;
        this.bookingTime = LocalDateTime.now();
        this.queuePosition = queuePosition;
    }

    // Getters and Setters
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public LocalDateTime getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Integer getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(Integer queuePosition) {
        this.queuePosition = queuePosition;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}