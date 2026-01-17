package dangod.springboot.repository;

import dangod.springboot.entity.FitnessTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessTestRepository extends JpaRepository<FitnessTest, Long> {

    List<FitnessTest> findByStudentId(Long studentId);

    List<FitnessTest> findByYearAndSemester(Integer year, Integer semester);

    List<FitnessTest> findByYear(Integer year);

    @Query("SELECT ft FROM FitnessTest ft WHERE ft.student.classInfo.id = :classId AND ft.year = :year AND ft.semester = :semester")
    List<FitnessTest> findByClassIdAndYearAndSemester(@Param("classId") Long classId, @Param("year") Integer year, @Param("semester") Integer semester);

    @Query("SELECT ft FROM FitnessTest ft WHERE ft.level = :level AND ft.year = :year AND ft.semester = :semester")
    List<FitnessTest> findByLevelAndYearAndSemester(@Param("level") String level, @Param("year") Integer year, @Param("semester") Integer semester);

    @Query("SELECT ft FROM FitnessTest ft WHERE ft.isQualified = false AND ft.year = :year AND ft.semester = :semester")
    List<FitnessTest> findUnqualifiedByYearAndSemester(@Param("year") Integer year, @Param("semester") Integer semester);

    @Query("SELECT AVG(ft.totalScore) FROM FitnessTest ft WHERE ft.student.classInfo.id = :classId AND ft.year = :year AND ft.semester = :semester")
    Double getAverageScoreByClassId(@Param("classId") Long classId, @Param("year") Integer year, @Param("semester") Integer semester);

    @Query("SELECT COUNT(ft) FROM FitnessTest ft WHERE ft.student.classInfo.id = :classId AND ft.year = :year AND ft.semester = :semester AND ft.isQualified = true")
    Long countQualifiedByClassId(@Param("classId") Long classId, @Param("year") Integer year, @Param("semester") Integer semester);

    @Query("SELECT COUNT(ft) FROM FitnessTest ft WHERE ft.student.classInfo.id = :classId AND ft.year = :year AND ft.semester = :semester")
    Long countTotalByClassId(@Param("classId") Long classId, @Param("year") Integer year, @Param("semester") Integer semester);
}
