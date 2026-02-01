package dangod.springboot.repository;

import dangod.springboot.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    List<Course> findByGymIdAndStartTimeBetween(Long gymId, LocalDateTime start, LocalDateTime end);
    
    List<Course> findByCoachIdAndStartTimeBetween(Long coachId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT c FROM Course c WHERE c.status = 1 AND c.startTime > :now ORDER BY c.startTime")
    List<Course> findAvailableCourses(LocalDateTime now);
    
    @Query("SELECT c FROM Course c WHERE c.gymId = :gymId AND c.status = 1 AND c.currentCapacity < c.maxCapacity AND c.startTime > :now")
    List<Course> findBookableCourses(Long gymId, LocalDateTime now);
    
    @Query("SELECT c FROM Course c WHERE c.courseTypeId IN :typeIds AND c.status = 1 AND c.startTime > :now ORDER BY c.startTime")
    List<Course> findByCourseTypeIn(List<Long> typeIds, LocalDateTime now);
    
    @Query("SELECT c.courseTypeId, COUNT(r) as cnt FROM Course c LEFT JOIN Reservation r ON c.id = r.courseId " +
           "WHERE c.startTime BETWEEN :start AND :end AND c.status = 1 GROUP BY c.courseTypeId ORDER BY cnt DESC")
    List<Object[]> findHotCourseTypes(LocalDateTime start, LocalDateTime end);

    @Query("SELECT c FROM Course c WHERE c.gymId = :gymId AND c.courseType = :courseType AND c.status = 1 " +
           "AND c.currentCapacity < c.maxCapacity AND c.startTime > :now ORDER BY c.startTime")
    List<Course> recommendCoursesByType(Long gymId, String courseType, LocalDateTime now);

    List<Course> findByGymIdAndCourseTypeIdAndStatus(Long gymId, Long courseTypeId, Integer status);

    List<Course> findByCoachIdAndStatus(Long coachId, Integer status);
}
