package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_student")
public class Student extends BaseEntity {
    @Column(name = "student_no", nullable = false, unique = true, length = 20)
    private String studentNo;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "gender", length = 10)
    private String gender;

    @Column(name = "age")
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Clazz clazz;

    @Column(name = "is_weak")
    private Boolean isWeak = false;

    @Column(name = "height")
    private Double height;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "phone", length = 20)
    private String phone;
}
