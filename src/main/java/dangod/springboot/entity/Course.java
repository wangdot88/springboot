package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "course_name", length = 100)
    private String courseName;
    
    @Column(name = "course_type_id")
    private Long courseTypeId;
    
    @Column(name = "coach_id")
    private Long coachId;
    
    @Column(name = "gym_id")
    private Long gymId;
    
    @Column(name = "classroom", length = 50)
    private String classroom;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "max_capacity")
    private Integer maxCapacity;
    
    @Column(name = "current_capacity")
    private Integer currentCapacity;
    
    @Column(name = "min_level_id")
    private Long minLevelId;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "remark")
    private String remark;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
