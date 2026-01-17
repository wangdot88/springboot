package com.example.springboot.repository;

import com.example.springboot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findById(Long id);
    Optional<Student> findByStudentNo(String studentNo);
    List<Student> findByClassName(String className);
    List<Student> findByIsWeak(Boolean isWeak);
    
    @Query("SELECT DISTINCT s.className FROM Student s")
    List<String> findAllClassNames();
    
    @Query("SELECT s FROM Student s WHERE s.id NOT IN (SELECT e.student.id FROM ExerciseRecord e WHERE e.recordDate = :date)")
    List<Student> findStudentsWithoutExercise(@Param("date") java.time.LocalDate date);
}
