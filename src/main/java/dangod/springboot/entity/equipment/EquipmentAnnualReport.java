package dangod.springboot.entity.equipment;

import dangod.springboot.entity.BaseEntity;
import dangod.springboot.entity.equipment.Equipment;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_equipment_annual_report")
public class EquipmentAnnualReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(name = "year")
    private Integer year;

    @Column(name = "total_borrow_count")
    private Integer totalBorrowCount;

    @Column(name = "total_borrow_days")
    private Integer totalBorrowDays;

    @Column(name = "repair_count")
    private Integer repairCount;

    @Column(name = "repair_cost")
    private Double repairCost;

    @Column(name = "utilization_rate")
    private Double utilizationRate;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData;
}
