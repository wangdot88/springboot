package dangod.springboot.repository;

import dangod.springboot.entity.ExerciseRecord;
import dangod.springboot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    
    List<ExerciseRecord> findByStudent(Student student);
    
    List<ExerciseRecord> findByStudentStudentId(String studentId);
    
    List<ExerciseRecord> findByExerciseType(String exerciseType);
    
    @Query("SELECT er FROM ExerciseRecord er WHERE er.student.studentId = :studentId AND er.exerciseDate BETWEEN :startDate AND :endDate")
    List<ExerciseRecord> findByStudentIdAndDateRange(@Param("studentId") String studentId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT er FROM ExerciseRecord er WHERE er.exerciseDate BETWEEN :startDate AND :endDate")
    List<ExerciseRecord> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT er FROM ExerciseRecord er WHERE er.student.className = :className AND er.exerciseDate BETWEEN :startDate AND :endDate")
    List<ExerciseRecord> findByClassAndDateRange(@Param("className") String className, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(DISTINCT er.student.studentId) FROM ExerciseRecord er WHERE er.exerciseDate = :date")
    Long countDistinctStudentsByDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT er.student.className, COUNT(DISTINCT er.student.studentId) as count FROM ExerciseRecord er WHERE er.exerciseDate = :date GROUP BY er.student.className ORDER BY count DESC")
    List<Object[]> countStudentsByClassForDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT er.student.studentId FROM ExerciseRecord er WHERE er.exerciseDate = :date")
    List<String> findStudentsWhoExercisedOnDate(@Param("date") LocalDateTime date);
    
    @Query("SELECT DISTINCT er.student.studentId FROM ExerciseRecord er WHERE er.exerciseDate BETWEEN :startDate AND :endDate")
    List<String> findStudentsWhoExercisedInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}