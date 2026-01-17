package dangod.springboot.repository;

import dangod.springboot.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCourseCode(String courseCode);
    List<Course> findByCategoryCode(String categoryCode);
    List<Course> findByStatus(Integer status);
    List<Course> findByDifficultyLevel(Integer difficultyLevel);
    List<Course> findByStatusAndCategoryCode(Integer status, String categoryCode);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = :status")
    Long countByStatus(@Param("status") Integer status);

    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findById(@Param("id") Long id);
}
