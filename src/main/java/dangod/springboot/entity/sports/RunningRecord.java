package dangod.springboot.entity.sports;

import dangod.springboot.entity.BaseEntity;
import dangod.springboot.entity.Student;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "t_running_record")
public class RunningRecord extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "avg_pace")
    private Double avgPace;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "remark", length = 500)
    private String remark;
}
