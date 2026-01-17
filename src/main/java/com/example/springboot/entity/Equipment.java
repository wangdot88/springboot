package com.example.springboot.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "equipment")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String type;
    private String specification;
    private String location;
    
    @Column(nullable = false)
    private Integer totalCount;
    
    @Column(nullable = false)
    private Integer availableCount;
    
    private Integer warningThreshold;
    private String status;
    
    public Integer getWarningThreshold() {
        return warningThreshold;
    }
    
    public Integer getTotalCount() {
        return totalCount;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getAvailableCount() {
        return availableCount;
    }
    
    public void setAvailableCount(Integer availableCount) {
        this.availableCount = availableCount;
    }
    
    public String getName() {
        return name;
    }
}