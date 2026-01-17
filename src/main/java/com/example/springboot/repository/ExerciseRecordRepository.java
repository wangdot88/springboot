package com.example.springboot.repository;

import com.example.springboot.entity.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    Optional<ExerciseRecord> findById(Long id);
    List<ExerciseRecord> findByStudentIdAndRecordDate(Long studentId, LocalDate date);
    List<ExerciseRecord> findByStudentIdAndRecordDateBetween(Long studentId, LocalDate startDate, LocalDate endDate);
    List<ExerciseRecord> findByRecordDate(LocalDate date);
    
    @Query("SELECT e.student.className, e.student.name, SUM(e.runningDistance) as totalDistance FROM ExerciseRecord e WHERE e.recordDate = :date GROUP BY e.student.id ORDER BY totalDistance DESC")
    List<Object[]> findClassRankingByDate(@Param("date") LocalDate date);
    
    @Query("SELECT e.student.className, SUM(e.runningDistance) as totalDistance FROM ExerciseRecord e WHERE e.recordDate = :date GROUP BY e.student.className ORDER BY totalDistance DESC")
    List<Object[]> findClassTotalRankingByDate(@Param("date") LocalDate date);
    List<ExerciseRecord> findClassRankings(LocalDate date);
    List<ExerciseRecord> findDailyExerciseReport(LocalDate date);
}
