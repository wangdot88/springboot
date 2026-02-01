package dangod.springboot.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "equipment_no", length = 32, unique = true)
    private String equipmentNo;
    
    @Column(name = "equipment_name", length = 100)
    private String equipmentName;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Column(name = "brand", length = 50)
    private String brand;
    
    @Column(name = "model", length = 50)
    private String model;
    
    @Column(name = "gym_id")
    private Long gymId;
    
    @Column(name = "location", length = 100)
    private String location;
    
    @Column(name = "purchase_date")
    private LocalDate purchaseDate;
    
    @Column(name = "warranty_expire")
    private LocalDate warrantyExpire;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "maintenance_cycle")
    private Integer maintenanceCycle;
    
    @Column(name = "last_maintenance")
    private LocalDate lastMaintenance;
    
    @Column(name = "next_maintenance")
    private LocalDate nextMaintenance;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
