package dangod.springboot.repository;

import dangod.springboot.entity.PhysicalTest;
import dangod.springboot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhysicalTestRepository extends JpaRepository<PhysicalTest, Long> {
    
    List<PhysicalTest> findByStudent(Student student);
    
    List<PhysicalTest> findByStudentStudentId(String studentId);
    
    List<PhysicalTest> findByTestType(String testType);
    
    List<PhysicalTest> findByGrade(String grade);
    
    @Query("SELECT pt FROM PhysicalTest pt WHERE pt.student.studentId = :studentId AND pt.testType = :testType ORDER BY pt.testDate DESC")
    List<PhysicalTest> findByStudentIdAndTestTypeOrderByTestDateDesc(@Param("studentId") String studentId, @Param("testType") String testType);
    
    @Query("SELECT pt FROM PhysicalTest pt WHERE pt.student.className = :className AND pt.testType = :testType")
    List<PhysicalTest> findByClassAndTestType(@Param("className") String className, @Param("testType") String testType);
    
    @Query("SELECT pt FROM PhysicalTest pt WHERE pt.testDate BETWEEN :startDate AND :endDate")
    List<PhysicalTest> findByTestDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(pt) FROM PhysicalTest pt WHERE pt.student.className = :className AND pt.testType = :testType AND pt.grade IN ('A', 'B')")
    Long countQualifiedStudentsByClass(@Param("className") String className, @Param("testType") String testType);
    
    @Query("SELECT COUNT(pt) FROM PhysicalTest pt WHERE pt.student.className = :className AND pt.testType = :testType")
    Long countTotalStudentsByClass(@Param("className") String className, @Param("testType") String testType);
    
    @Query("SELECT pt FROM PhysicalTest pt WHERE pt.student.className = :className AND pt.testType = :testType AND pt.grade = 'D'")
    List<PhysicalTest> findUnqualifiedStudentsByClass(@Param("className") String className, @Param("testType") String testType);
}