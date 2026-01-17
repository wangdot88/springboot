package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "member_course_enrollment", indexes = {
        @Index(name = "idx_member_id", columnList = "memberId"),
        @Index(name = "idx_schedule_id", columnList = "scheduleId"),
        @Index(name = "idx_status", columnList = "status")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_schedule", columnNames = {"memberId", "scheduleId"})
})
public class MemberCourseEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "enroll_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date enrollTime;

    @Column(columnDefinition = "TINYINT default 0")
    private Integer status;

    @Column(name = "queue_position")
    private Integer queuePosition;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        enrollTime = new Date();
    }
}
