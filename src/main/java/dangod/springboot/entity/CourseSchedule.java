package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "course_schedule", indexes = {
        @Index(name = "idx_course_id", columnList = "courseId"),
        @Index(name = "idx_branch_id", columnList = "branchId"),
        @Index(name = "idx_schedule_date", columnList = "scheduleDate"),
        @Index(name = "idx_status", columnList = "status")
})
public class CourseSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "coach_id")
    private Long coachId;

    @Column(name = "schedule_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date scheduleDate;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date startTime;

    @Column(name = "end_time", nullable = false)
    @Temporal(TemporalType.TIME)
    private Date endTime;

    @Column(name = "current_participants")
    private Integer currentParticipants;

    @Column(name = "max_participants", nullable = false)
    private Integer maxParticipants;

    @Column(columnDefinition = "TINYINT default 1")
    private Integer status;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}
