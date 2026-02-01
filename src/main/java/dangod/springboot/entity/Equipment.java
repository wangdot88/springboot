package dangod.springboot.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String equipmentCode;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false)
    private String brand;
    
    @Column(nullable = false)
    private String model;
    
    @Column(nullable = false)
    private Integer totalQuantity;
    
    @Column(nullable = false)
    private Integer availableQuantity;
    
    @Column(nullable = false)
    private Integer borrowedQuantity;
    
    @Column
    private Integer repairQuantity;
    
    @Column
    private Integer minStockAlert;
    
    @Column
    private String description;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    public Equipment() {}
    
    public Equipment(String equipmentCode, String name, String category, String brand, String model, Integer totalQuantity, Integer minStockAlert) {
        this.equipmentCode = equipmentCode;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.model = model;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.borrowedQuantity = 0;
        this.repairQuantity = 0;
        this.minStockAlert = minStockAlert;
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
    
    public String getEquipmentCode() {
        return equipmentCode;
    }
    
    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
    
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    
    public Integer getBorrowedQuantity() {
        return borrowedQuantity;
    }
    
    public void setBorrowedQuantity(Integer borrowedQuantity) {
        this.borrowedQuantity = borrowedQuantity;
    }
    
    public Integer getRepairQuantity() {
        return repairQuantity;
    }
    
    public void setRepairQuantity(Integer repairQuantity) {
        this.repairQuantity = repairQuantity;
    }
    
    public Integer getMinStockAlert() {
        return minStockAlert;
    }
    
    public void setMinStockAlert(Integer minStockAlert) {
        this.minStockAlert = minStockAlert;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
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