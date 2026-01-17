package dangod.springboot.repository;

import dangod.springboot.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNo(String studentNo);
    List<Student> findByClassName(String className);
    List<Student> findByIsWeak(Boolean isWeak);
    
    @Query("SELECT s FROM Student s WHERE s.className = :className AND s.isWeak = false")
    List<Student> findNormalStudentsByClassName(@Param("className") String className);
    
    @Query("SELECT DISTINCT s.className FROM Student s")
    List<String> findAllClassNames();
    
    List<Student> findByStudentNoIn(List<String> studentNos);
}
