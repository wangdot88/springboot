package com.example.springboot.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "exercise_records")
public class ExerciseRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    
    @Column(nullable = false)
    private java.time.LocalDate recordDate;
    
    private Double runningDistance;
    private Integer runningTime;
    private Double distance;
    private Integer duration;
    private Integer calories;
    private java.time.LocalDateTime recordTime;
    private String exerciseType;
    private String notes;
}
