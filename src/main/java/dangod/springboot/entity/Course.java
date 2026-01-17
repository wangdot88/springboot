package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "course", indexes = {
        @Index(name = "idx_course_code", columnList = "courseCode"),
        @Index(name = "idx_category_code", columnList = "categoryCode"),
        @Index(name = "idx_status", columnList = "status")
})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", unique = true, nullable = false, length = 50)
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 100)
    private String courseName;

    @Column(name = "category_code", nullable = false, length = 50)
    private String categoryCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer duration;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    private BigDecimal price;

    @Column(name = "difficulty_level")
    private Integer difficultyLevel;

    @Column(name = "cover_image", length = 255)
    private String coverImage;

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
