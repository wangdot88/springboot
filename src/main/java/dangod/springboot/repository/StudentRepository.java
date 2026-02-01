package dangod.springboot.repository;

import dangod.springboot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByStudentId(String studentId);
    
    List<Student> findByClassName(String className);
    
    List<Student> findByMajor(String major);
    
    List<Student> findByIsWeakPhysical(Boolean isWeakPhysical);
    
    @Query("SELECT s FROM Student s WHERE s.className = :className AND s.isWeakPhysical = true")
    List<Student> findWeakStudentsByClass(@Param("className") String className);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.className = :className")
    Long countByClassName(@Param("className") String className);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.className = :className AND s.isWeakPhysical = true")
    Long countWeakStudentsByClass(@Param("className") String className);
    
    boolean existsByStudentId(String studentId);
}