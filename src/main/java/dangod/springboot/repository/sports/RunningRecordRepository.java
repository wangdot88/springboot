package dangod.springboot.repository.sports;

import dangod.springboot.entity.sports.RunningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RunningRecordRepository extends JpaRepository<RunningRecord, Long> {

    List<RunningRecord> findByStudentId(Long studentId);

    List<RunningRecord> findByRecordDate(LocalDate recordDate);

    List<RunningRecord> findByStudentIdAndRecordDate(Long studentId, LocalDate recordDate);

    @Query("SELECT r FROM RunningRecord r WHERE r.student.clazz.id = :classId AND r.recordDate = :recordDate")
    List<RunningRecord> findByClassIdAndRecordDate(@Param("classId") Long classId, @Param("recordDate") LocalDate recordDate);

    @Query("SELECT r.student.id, SUM(r.distance) FROM RunningRecord r WHERE r.recordDate BETWEEN :startDate AND :endDate GROUP BY r.student.id ORDER BY SUM(r.distance) DESC")
    List<Object[]> findDistanceRanking(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT r.student.clazz.id, SUM(r.distance) FROM RunningRecord r WHERE r.recordDate BETWEEN :startDate AND :endDate GROUP BY r.student.clazz.id ORDER BY SUM(r.distance) DESC")
    List<Object[]> findClassDistanceRanking(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(DISTINCT r.student.id) FROM RunningRecord r WHERE r.recordDate = :recordDate AND r.student.clazz.id = :classId")
    Long countCheckedInByClassIdAndDate(@Param("classId") Long classId, @Param("recordDate") LocalDate recordDate);

    @Query("SELECT r FROM RunningRecord r WHERE r.recordDate BETWEEN :startDate AND :endDate")
    List<RunningRecord> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
