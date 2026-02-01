package dangod.springboot.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_clazz")
public class Clazz extends BaseEntity {
    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "grade", nullable = false, length = 20)
    private String grade;

    @Column(name = "head_teacher", length = 50)
    private String headTeacher;

    @Column(name = "student_count")
    private Integer studentCount = 0;
}
