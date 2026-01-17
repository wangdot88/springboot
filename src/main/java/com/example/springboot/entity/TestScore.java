package com.example.springboot.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "test_scores")
public class TestScore extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    private Integer sitUp;
    private Integer pushUp;
    private Double run1000m;
    private Double run800m;
    private Double vitalCapacity;
    private Double height;
    private Double weight;
    private Double eyesightLeft;
    private Double eyesightRight;
    
    @Column(nullable = false)
    private Integer totalScore;
    
    @Column(nullable = false, length = 1)
    private String grade;
    
    @Column(nullable = false)
    private java.time.LocalDate testDate;
}
