package dangod.springboot.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipment")
public class Equipment implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(length = 200)
    private String description;

    @Column(length = 50)
    private String category;

    @Column(length = 50)
    private String brand;

    @Column(length = 50)
    private String model;

    @Column(nullable = false)
    private LocalDateTime purchaseDate;

    @Column(nullable = false)
    private Integer status;

    @Column(nullable = false)
    private Integer usageCount;

    @Column(precision = 5, scale = 2)
    private Double usageRate;

    @Column(precision = 5, scale = 2)
    private Double avgDailyUsage;

    @Column(length = 200)
    private String lastMaintenance;

    @Column(nullable = false)
    private LocalDateTime nextMaintenance;

    @Column(length = 50)
    private String location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(length = 50)
    private String createBy;

    @Column(nullable = false)
    private LocalDateTime createTime;

    @Column(length = 50)
    private String updateBy;

    private LocalDateTime updateTime;

    public Equipment() {
        this.status = 1;
        this.usageCount = 0;
        this.usageRate = 0.0;
        this.avgDailyUsage = 0.0;
        this.purchaseDate = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Double getUsageRate() {
        return usageRate;
    }

    public void setUsageRate(Double usageRate) {
        this.usageRate = usageRate;
    }

    public Double getAvgDailyUsage() {
        return avgDailyUsage;
    }

    public void setAvgDailyUsage(Double avgDailyUsage) {
        this.avgDailyUsage = avgDailyUsage;
    }

    public String getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(String lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public LocalDateTime getNextMaintenance() {
        return nextMaintenance;
    }

    public void setNextMaintenance(LocalDateTime nextMaintenance) {
        this.nextMaintenance = nextMaintenance;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
