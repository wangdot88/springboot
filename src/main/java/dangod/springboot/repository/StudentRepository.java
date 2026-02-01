package dangod.springboot.repository;

import dangod.springboot.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    default Optional<Student> findById(Long id) {
        return Optional.ofNullable(findOne(id));
    }

    Optional<Student> findByStudentNo(String studentNo);

    List<Student> findByClazzId(Long classId);

    List<Student> findByIsWeak(Boolean isWeak);

    boolean existsByStudentNo(String studentNo);
}
