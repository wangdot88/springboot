package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_coach")
public class Coach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "coach_no", length = 32, unique = true)
    private String coachNo;
    
    @Column(name = "name", length = 50)
    private String name;
    
    @Column(name = "gender")
    private Integer gender;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "specialty")
    private String specialty;
    
    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "gym_id")
    private Long gymId;
    
    @Column(name = "level", length = 20)
    private String level;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
