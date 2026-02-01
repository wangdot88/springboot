package dangod.springboot.entity.fitness;

import dangod.springboot.entity.BaseEntity;
import dangod.springboot.entity.Student;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "t_fitness_test")
public class FitnessTest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "test_date")
    private LocalDate testDate;

    @Column(name = "semester", length = 50)
    private String semester;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "bmi")
    private Double bmi;

    @Column(name = "vital_capacity")
    private Integer vitalCapacity;

    @Column(name = "vital_capacity_grade", length = 5)
    private String vitalCapacityGrade;

    @Column(name = "fifty_meter")
    private Double fiftyMeter;

    @Column(name = "fifty_meter_grade", length = 5)
    private String fiftyMeterGrade;

    @Column(name = "jump")
    private Integer jump;

    @Column(name = "jump_grade", length = 5)
    private String jumpGrade;

    @Column(name = "sit_reach")
    private Double sitReach;

    @Column(name = "sit_reach_grade", length = 5)
    private String sitReachGrade;

    @Column(name = "endurance_run")
    private Integer enduranceRun;

    @Column(name = "endurance_run_grade", length = 5)
    private String enduranceRunGrade;

    @Column(name = "pull_up")
    private Integer pullUp;

    @Column(name = "pull_up_grade", length = 5)
    private String pullUpGrade;

    @Column(name = "sit_up")
    private Integer sitUp;

    @Column(name = "sit_up_grade", length = 5)
    private String sitUpGrade;

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "total_grade", length = 5)
    private String totalGrade;

    @Column(name = "is_passed")
    private Boolean isPassed = false;

    @Column(name = "is_weak_student")
    private Boolean isWeakStudent = false;

    @Column(name = "remark", length = 500)
    private String remark;
}
