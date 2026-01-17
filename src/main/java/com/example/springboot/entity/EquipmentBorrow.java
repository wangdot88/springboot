package com.example.springboot.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "equipment_borrows")
public class EquipmentBorrow {
    public Student getStudent() {
        return student;
    }
    
    public Equipment getEquipment() {
        return equipment;
    }
    
    public java.time.LocalDateTime getExpectedReturnTime() {
        return expectedReturnTime;
    }
    
    public Boolean getIsOverdue() {
        return isOverdue;
    }
    
    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;
    
    @Column(nullable = false)
    private Integer borrowCount;
    
    @Column(nullable = false)
    private java.time.LocalDateTime borrowTime;
    
    private java.time.LocalDateTime returnTime;
    private java.time.LocalDateTime expectedReturnTime;
    
    private Boolean isReturned = false;
    private Boolean isOverdue = false;
    private String notes;
}
