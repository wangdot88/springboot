package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_membership_level")
public class MembershipLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "level_name", length = 20)
    private String levelName;
    
    @Column(name = "level_code", length = 20, unique = true)
    private String levelCode;
    
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "duration_days")
    private Integer durationDays;
    
    @Column(name = "max_courses_per_week")
    private Integer maxCoursesPerWeek;
    
    @Column(name = "can_book_advance_days")
    private Integer canBookAdvanceDays;
    
    @Column(name = "discount_rate", precision = 3, scale = 2)
    private BigDecimal discountRate;
    
    @Column(name = "privileges", columnDefinition = "TEXT")
    private String privileges;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
