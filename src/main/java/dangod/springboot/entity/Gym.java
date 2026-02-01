package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_gym")
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "gym_name", length = 100)
    private String gymName;
    
    @Column(name = "gym_code", length = 20, unique = true)
    private String gymCode;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "manager", length = 50)
    private String manager;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
