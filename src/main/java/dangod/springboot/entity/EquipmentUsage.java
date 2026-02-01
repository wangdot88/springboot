package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_equipment_usage")
public class EquipmentUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "equipment_id")
    private Long equipmentId;
    
    @Column(name = "member_id")
    private Long memberId;
    
    @Column(name = "gym_id")
    private Long gymId;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "calories_burned")
    private Integer caloriesBurned;
    
    @Column(name = "data_json", columnDefinition = "TEXT")
    private String dataJson;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
