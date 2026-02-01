package dangod.springboot.repository.fitness;

import dangod.springboot.entity.fitness.FitnessTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FitnessTestRepository extends JpaRepository<FitnessTest, Long>, JpaSpecificationExecutor<FitnessTest> {

    List<FitnessTest> findByStudentId(Long studentId);

    List<FitnessTest> findBySemester(String semester);

    @Query("SELECT f FROM FitnessTest f WHERE f.student.clazz.id = :classId AND f.semester = :semester")
    List<FitnessTest> findByClassIdAndSemester(@Param("classId") Long classId, @Param("semester") String semester);

    @Query("SELECT COUNT(f) FROM FitnessTest f WHERE f.student.clazz.id = :classId AND f.semester = :semester AND f.isPassed = true")
    Long countPassedByClassIdAndSemester(@Param("classId") Long classId, @Param("semester") String semester);

    @Query("SELECT COUNT(f) FROM FitnessTest f WHERE f.student.clazz.id = :classId AND f.semester = :semester")
    Long countByClassIdAndSemester(@Param("classId") Long classId, @Param("semester") String semester);

    @Query("SELECT f FROM FitnessTest f WHERE f.isWeakStudent = true AND f.semester = :semester")
    List<FitnessTest> findWeakStudents(@Param("semester") String semester);

    FitnessTest findByStudentIdAndSemester(Long studentId, String semester);
}
