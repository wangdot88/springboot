package dangod.springboot.repository;

import dangod.springboot.model.entity.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {
    Optional<RunningRecord> findByStudentIdAndRecordDate(Long studentId, Date recordDate);
    List<RunningRecord> findByRecordDate(Date recordDate);
    List<RunningRecord> findByStudentIdAndRecordDateBetween(Long studentId, Date startDate, Date endDate);
    List<RunningRecord> findByRecordDateBetween(Date startDate, Date endDate);
    
    @Query("SELECT r FROM RunningRecord r JOIN Student s ON r.studentId = s.id WHERE s.className = :className AND r.recordDate = :recordDate")
    List<RunningRecord> findByClassNameAndRecordDate(@Param("className") String className, @Param("recordDate") Date recordDate);
    
    @Query("SELECT r.studentId, SUM(r.distance) as totalDistance FROM RunningRecord r WHERE r.recordDate = :recordDate GROUP BY r.studentId ORDER BY totalDistance DESC")
    List<Object[]> findDailyRanking(@Param("recordDate") Date recordDate);
    
    @Query("SELECT s.id, s.name, s.className FROM Student s WHERE s.id NOT IN (SELECT r.studentId FROM RunningRecord r WHERE r.recordDate = :recordDate) AND s.isWeak = false")
    List<Object[]> findStudentsNotExercised(@Param("recordDate") Date recordDate);
    
    @Query("SELECT SUM(r.distance) FROM RunningRecord r WHERE r.studentId = :studentId AND r.recordDate BETWEEN :startDate AND :endDate")
    Double findTotalDistanceByStudentAndDateRange(@Param("studentId") Long studentId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
