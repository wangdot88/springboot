package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_member_card")
public class MemberCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "card_no", length = 32, unique = true)
    private String cardNo;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "level_id")
    private Long levelId;
    
    @Column(name = "gym_id")
    private Long gymId;
    
    @Column(name = "active_time")
    private LocalDateTime activeTime;
    
    @Column(name = "expire_time")
    private LocalDateTime expireTime;
    
    @Column(name = "remain_courses")
    private Integer remainCourses;
    
    @Column(name = "total_courses")
    private Integer totalCourses;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "freeze_reason")
    private String freezeReason;
    
    @Column(name = "freeze_time")
    private LocalDateTime freezeTime;
    
    @Column(name = "unfreeze_time")
    private LocalDateTime unfreezeTime;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
