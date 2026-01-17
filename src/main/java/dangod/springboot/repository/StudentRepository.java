package dangod.springboot.repository;

import dangod.springboot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Student findByStudentNo(String studentNo);

    List<Student> findByClassInfoId(Long classId);

    List<Student> findByIsWeak(Boolean isWeak);

    List<Student> findByNameContaining(String name);

    @Query("SELECT s FROM Student s WHERE s.studentNo LIKE %:keyword% OR s.name LIKE %:keyword%")
    List<Student> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.classInfo.id = :classId")
    Integer countByClassId(@Param("classId") Long classId);
}
