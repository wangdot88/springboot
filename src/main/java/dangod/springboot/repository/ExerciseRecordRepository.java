package dangod.springboot.repository;

import dangod.springboot.entity.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    List<ExerciseRecord> findByStudentId(Long studentId);

    List<ExerciseRecord> findByRecordDate(Date recordDate);

    List<ExerciseRecord> findByStudentIdAndRecordDate(Long studentId, Date recordDate);

    @Query("SELECT er FROM ExerciseRecord er WHERE er.student.classInfo.id = :classId AND er.recordDate = :recordDate")
    List<ExerciseRecord> findByClassIdAndRecordDate(@Param("classId") Long classId, @Param("recordDate") Date recordDate);

    @Query("SELECT er FROM ExerciseRecord er WHERE er.recordDate BETWEEN :startDate AND :endDate")
    List<ExerciseRecord> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(DISTINCT er.student.id) FROM ExerciseRecord er WHERE er.student.classInfo.id = :classId AND er.recordDate = :recordDate")
    Long countActiveStudentsByClassIdAndDate(@Param("classId") Long classId, @Param("recordDate") Date recordDate);

    @Query("SELECT SUM(er.distance) FROM ExerciseRecord er WHERE er.student.id = :studentId AND er.recordDate = :recordDate")
    Double getTotalDistanceByStudentAndDate(@Param("studentId") Long studentId, @Param("recordDate") Date recordDate);

    @Query("SELECT SUM(er.distance) FROM ExerciseRecord er WHERE er.student.classInfo.id = :classId AND er.recordDate = :recordDate")
    Double getTotalDistanceByClassIdAndDate(@Param("classId") Long classId, @Param("recordDate") Date recordDate);

    @Query("SELECT er.student.id, SUM(er.distance) as totalDistance FROM ExerciseRecord er WHERE er.recordDate = :recordDate GROUP BY er.student.id ORDER BY totalDistance DESC")
    List<Object[]> findDailyRanking(@Param("recordDate") Date recordDate);

    @Query("SELECT er.student.id, SUM(er.distance) as totalDistance FROM ExerciseRecord er WHERE er.recordDate BETWEEN :startDate AND :endDate GROUP BY er.student.id ORDER BY totalDistance DESC")
    List<Object[]> findPeriodRanking(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
