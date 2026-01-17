package dangod.springboot.repository;

import dangod.springboot.model.entity.TestScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestScoreRepository extends JpaRepository<TestScore, Long> {
    Optional<TestScore> findByStudentIdAndTestYear(Long studentId, Integer testYear);
    List<TestScore> findByTestYear(Integer testYear);
    List<TestScore> findByStudentId(Long studentId);
    
    @Query("SELECT ts FROM TestScore ts JOIN Student s ON ts.studentId = s.id WHERE ts.testYear = :testYear AND s.className = :className AND ts.level = :level")
    Page<TestScore> findByTestYearAndClassNameAndLevel(@Param("testYear") Integer testYear, @Param("className") String className, @Param("level") String level, Pageable pageable);
    
    @Query("SELECT ts FROM TestScore ts JOIN Student s ON ts.studentId = s.id WHERE ts.testYear = :testYear AND s.className = :className")
    Page<TestScore> findByTestYearAndClassName(@Param("testYear") Integer testYear, @Param("className") String className, Pageable pageable);
    
    Page<TestScore> findByTestYearAndLevel(Integer testYear, String level, Pageable pageable);
    
    Page<TestScore> findByTestYear(Integer testYear, Pageable pageable);
    
    @Query("SELECT ts FROM TestScore ts JOIN Student s ON ts.studentId = s.id WHERE s.className = :className AND ts.testYear = :testYear")
    List<TestScore> findByClassNameAndTestYear(@Param("className") String className, @Param("testYear") Integer testYear);
    
    @Query("SELECT ts FROM TestScore ts WHERE ts.testYear = :testYear AND ts.isPass = true")
    List<TestScore> findPassedScoresByYear(@Param("testYear") Integer testYear);
    
    @Query("SELECT ts FROM TestScore ts WHERE ts.testYear = :testYear AND ts.isPass = false")
    List<TestScore> findFailedScoresByYear(@Param("testYear") Integer testYear);
    
    @Query("SELECT COUNT(ts) FROM TestScore ts WHERE ts.testYear = :testYear AND ts.isPass = true")
    long countPassedByYear(@Param("testYear") Integer testYear);
    
    @Query("SELECT COUNT(ts) FROM TestScore ts WHERE ts.testYear = :testYear")
    long countTotalByYear(@Param("testYear") Integer testYear);
}
