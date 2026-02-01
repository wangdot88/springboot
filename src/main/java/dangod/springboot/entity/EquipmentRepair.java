package dangod.springboot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment_repair")
public class EquipmentRepair {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private String repairReason;
    
    @Column(nullable = false)
    private String status;
    
    @Column
    private String repairDescription;
    
    @Column
    private Double repairCost;
    
    @Column
    private LocalDateTime reportDate;
    
    @Column
    private LocalDateTime repairDate;
    
    @Column
    private LocalDateTime completedDate;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    public EquipmentRepair() {}
    
    public EquipmentRepair(Equipment equipment, Student student, Integer quantity, String repairReason, String repairDescription) {
        this.equipment = equipment;
        this.student = student;
        this.quantity = quantity;
        this.repairReason = repairReason;
        this.repairDescription = repairDescription;
        this.status = "REPORTED";
        this.reportDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Equipment getEquipment() {
        return equipment;
    }
    
    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
    
    public Student getStudent() {
        return student;
    }
    
    public void setStudent(Student student) {
        this.student = student;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getRepairReason() {
        return repairReason;
    }
    
    public void setRepairReason(String repairReason) {
        this.repairReason = repairReason;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRepairDescription() {
        return repairDescription;
    }
    
    public void setRepairDescription(String repairDescription) {
        this.repairDescription = repairDescription;
    }
    
    public Double getRepairCost() {
        return repairCost;
    }
    
    public void setRepairCost(Double repairCost) {
        this.repairCost = repairCost;
    }
    
    public LocalDateTime getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }
    
    public LocalDateTime getRepairDate() {
        return repairDate;
    }
    
    public void setRepairDate(LocalDateTime repairDate) {
        this.repairDate = repairDate;
    }
    
    public LocalDateTime getCompletedDate() {
        return completedDate;
    }
    
    public void setCompletedDate(LocalDateTime completedDate) {
        this.completedDate = completedDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}