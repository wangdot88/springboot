package com.example.springboot.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "students")
public class Student extends BaseEntity {
    public String getStudentNo() {
        return studentNo;
    }
    
    public String getName() {
        return name;
    }
    
    public String getClassName() {
        return className;
    }
    
    public String getPhone() {
        return phone;
    }
    
    @Column(nullable = false, unique = true)
    private String studentNo;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String className;
    
    private String gender;
    private Integer age;
    private String phone;
    @Column(columnDefinition = "tinyint(1) default 0")
    private Boolean isWeak = false;
}
