package dangod.springboot.entity.fitness;

import dangod.springboot.entity.BaseEntity;
import dangod.springboot.entity.Clazz;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_fitness_test_report")
public class FitnessTestReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Clazz clazz;

    @Column(name = "semester", length = 50)
    private String semester;

    @Column(name = "total_students")
    private Integer totalStudents;

    @Column(name = "tested_students")
    private Integer testedStudents;

    @Column(name = "passed_students")
    private Integer passedStudents;

    @Column(name = "failed_students")
    private Integer failedStudents;

    @Column(name = "pass_rate")
    private Double passRate;

    @Column(name = "grade_a_count")
    private Integer gradeACount;

    @Column(name = "grade_b_count")
    private Integer gradeBCount;

    @Column(name = "grade_c_count")
    private Integer gradeCCount;

    @Column(name = "grade_d_count")
    private Integer gradeDCount;

    @Column(name = "weak_students_count")
    private Integer weakStudentsCount;

    @Column(name = "avg_total_score")
    private Double avgTotalScore;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData;
}
