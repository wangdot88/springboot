package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "member_no", length = 32, unique = true)
    private String memberNo;
    
    @Column(name = "name", length = 50)
    private String name;
    
    @Column(name = "gender")
    private Integer gender;
    
    @Column(name = "phone", length = 20, unique = true)
    private String phone;
    
    @Column(name = "id_card", length = 18)
    private String idCard;
    
    @Column(name = "birthday")
    private LocalDate birthday;
    
    @Column(name = "avatar")
    private String avatar;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "emergency_contact", length = 50)
    private String emergencyContact;
    
    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;
    
    @Column(name = "register_gym_id")
    private Long registerGymId;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
