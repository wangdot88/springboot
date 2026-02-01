package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "notification_no", length = 32, unique = true)
    private String notificationNo;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "gym_id")
    private Long gymId;
    
    @Column(name = "type", length = 50)
    private String type;
    
    @Column(name = "title", length = 100)
    private String title;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "send_time")
    private LocalDateTime sendTime;
    
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    @Column(name = "read_status")
    private Integer readStatus;
    
    @Column(name = "push_status")
    private Integer pushStatus;
    
    @Column(name = "push_fail_reason")
    private String pushFailReason;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
