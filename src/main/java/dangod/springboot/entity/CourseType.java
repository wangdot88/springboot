package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_course_type")
public class CourseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "type_name", length = 50)
    private String typeName;
    
    @Column(name = "type_code", length = 20, unique = true)
    private String typeCode;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "calories")
    private Integer calories;
    
    @Column(name = "difficulty")
    private Integer difficulty;
    
    @Column(name = "suitable_for")
    private String suitableFor;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
