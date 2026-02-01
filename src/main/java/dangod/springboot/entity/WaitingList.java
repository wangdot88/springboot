package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_waiting_list")
public class WaitingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "course_id")
    private Long courseId;
    
    @Column(name = "queue_position")
    private Integer queuePosition;
    
    @Column(name = "join_time")
    private LocalDateTime joinTime;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "convert_time")
    private LocalDateTime convertTime;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
