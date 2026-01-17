package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "fitness_test_record", indexes = {
        @Index(name = "idx_member_id", columnList = "memberId"),
        @Index(name = "idx_test_date", columnList = "testDate")
})
public class FitnessTestRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "test_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testDate;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "tester_id")
    private Long testerId;

    @Column(name = "create_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @PrePersist
    protected void onCreate() {
        createTime = new Date();
        testDate = new Date();
    }
}
