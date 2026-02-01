package dangod.springboot.entity.equipment;

import dangod.springboot.entity.BaseEntity;
import dangod.springboot.entity.Student;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_equipment_borrow")
public class EquipmentBorrow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "borrow_date")
    private LocalDate borrowDate;

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "is_overdue")
    private Boolean isOverdue = false;

    @Column(name = "borrow_purpose", length = 500)
    private String borrowPurpose;

    @Column(name = "remark", length = 500)
    private String remark;
}
