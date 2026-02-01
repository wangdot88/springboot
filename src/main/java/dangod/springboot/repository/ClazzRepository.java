package dangod.springboot.repository;

import dangod.springboot.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Long> {
    default Optional<Clazz> findById(Long id) {
        return Optional.ofNullable(findOne(id));
    }

    Optional<Clazz> findByClassNameAndGrade(String className, String grade);
}
