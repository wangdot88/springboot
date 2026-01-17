package dangod.springboot.repository;

import dangod.springboot.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByStoreId(Long storeId);

    List<Course> findByType(Course.CourseType type);

    List<Course> findByStatus(Course.CourseStatus status);

    @Query("SELECT c FROM Course c WHERE c.storeId = :storeId AND c.type = :type AND c.difficulty <= :difficulty")
    List<Course> findRecommendedCourses(@Param("storeId") Long storeId, @Param("type") Course.CourseType type, @Param("difficulty") Integer difficulty);

    @Query("SELECT c FROM Course c ORDER BY c.currentEnrolled DESC")
    List<Course> findPopularCourses();
}
