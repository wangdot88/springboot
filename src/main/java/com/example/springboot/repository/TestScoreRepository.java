package com.example.springboot.repository;

import com.example.springboot.entity.TestScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestScoreRepository extends JpaRepository<TestScore, Long> {
    List<TestScore> findByStudentClassName(String className);
    
    @Query("SELECT ts FROM TestScore ts WHERE ts.student.className = :className AND ts.testDate = :date")
    List<TestScore> findByClassNameAndTestDate(@Param("className") String className, @Param("date") java.time.LocalDate date);
    
    @Query("SELECT ts FROM TestScore ts WHERE ts.grade = 'D'")
    List<TestScore> findWeakStudentScores();
}
