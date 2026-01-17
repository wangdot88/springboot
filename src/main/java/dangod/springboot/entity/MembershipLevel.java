package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "membership_level", indexes = {
        @Index(name = "idx_level_code", columnList = "levelCode")
})
public class MembershipLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_code", unique = true, nullable = false, length = 20)
    private String levelCode;

    @Column(name = "level_name", nullable = false, length = 50)
    private String levelName;

    @Column(precision = 3, scale = 2)
    private BigDecimal discount;

    @Column(name = "free_classes_per_month")
    private Integer freeClassesPerMonth;

    @Column(name = "max_courses_per_week")
    private Integer maxCoursesPerWeek;

    @Column(name = "can_freeze", columnDefinition = "TINYINT default 1")
    private Integer canFreeze;

    @Column(name = "freeze_duration")
    private Integer freezeDuration;

    @Column(name = "equipment_priority", columnDefinition = "TINYINT default 1")
    private Integer equipmentPriority;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}
