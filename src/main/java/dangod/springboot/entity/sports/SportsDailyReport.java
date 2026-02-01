package dangod.springboot.entity.sports;

import dangod.springboot.entity.BaseEntity;
import dangod.springboot.entity.Clazz;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "t_sports_daily_report")
public class SportsDailyReport extends BaseEntity {

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Clazz clazz;

    @Column(name = "total_students")
    private Integer totalStudents;

    @Column(name = "checked_in_count")
    private Integer checkedInCount;

    @Column(name = "not_checked_in_count")
    private Integer notCheckedInCount;

    @Column(name = "total_distance")
    private Double totalDistance;

    @Column(name = "avg_distance")
    private Double avgDistance;

    @Column(name = "check_in_rate")
    private Double checkInRate;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData;
}
