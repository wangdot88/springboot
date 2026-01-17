package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "equipment", indexes = {
        @Index(name = "idx_branch_id", columnList = "branchId"),
        @Index(name = "idx_status", columnList = "status")
})
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipment_code", unique = true, nullable = false, length = 50)
    private String equipmentCode;

    @Column(name = "equipment_name", nullable = false, length = 100)
    private String equipmentName;

    @Column(name = "branch_id", nullable = false)
    private Long branchId;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "status", nullable = false)
    private Integer status = 1;

    @Column(name = "purchase_date")
    @Temporal(TemporalType.DATE)
    private Date purchaseDate;

    @Column(name = "last_maintain_date")
    @Temporal(TemporalType.DATE)
    private Date lastMaintainDate;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
    }
}