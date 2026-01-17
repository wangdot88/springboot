package com.example.springboot.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "equipment_repairs")
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
    private String problemDescription;
    
    private String repairStatus;
    private String repairNotes;
    private java.time.LocalDateTime submitTime;
    private java.time.LocalDateTime completeTime;
}
