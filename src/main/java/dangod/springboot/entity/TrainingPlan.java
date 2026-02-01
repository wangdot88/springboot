package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_training_plan")
public class TrainingPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "plan_no", length = 32, unique = true)
    private String planNo;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "coach_id")
    private Long coachId;
    
    @Column(name = "test_id")
    private Long testId;
    
    @Column(name = "plan_name", length = 100)
    private String planName;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(name = "goal", columnDefinition = "TEXT")
    private String goal;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "frequency", length = 50)
    private String frequency;
    
    @Column(name = "difficulty")
    private Integer difficulty;
    
    @Column(name = "calories_goal")
    private Integer caloriesGoal;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
