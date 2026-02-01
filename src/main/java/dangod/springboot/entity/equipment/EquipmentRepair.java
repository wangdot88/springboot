package dangod.springboot.entity.equipment;

import dangod.springboot.entity.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "t_equipment_repair")
public class EquipmentRepair extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "report_date")
    private LocalDate reportDate;

    @Column(name = "repair_date")
    private LocalDate repairDate;

    @Column(name = "complete_date")
    private LocalDate completeDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "repair_cost")
    private Double repairCost;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "repair_man", length = 50)
    private String repairMan;

    @Column(name = "remark", length = 500)
    private String remark;
}
