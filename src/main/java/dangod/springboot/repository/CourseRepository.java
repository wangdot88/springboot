package dangod.springboot.repository;

import dangod.springboot.model.Course;
import dangod.springboot.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByTrainer_Id(Long trainerId);
    
    List<Course> findByGym_StoreId(String storeId);
    
    List<Course> findByStatus(CourseStatus status);
    
    List<Course> findByCategory(String category);
    
    @Query("SELECT c FROM Course c WHERE c.startTime >= :startTime AND c.endTime <= :endTime")
    List<Course> findCoursesBetween(@Param("startTime") LocalDateTime startTime, 
                                     @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT c FROM Course c WHERE c.startTime >= :now AND c.status = :status ORDER BY c.startTime")
    List<Course> findUpcomingCourses(@Param("now") LocalDateTime now, 
                                      @Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE c.currentBookings < c.maxCapacity AND c.startTime > :now AND c.status = :status")
    List<Course> findAvailableCourses(@Param("now") LocalDateTime now, 
                                       @Param("status") CourseStatus status);
    
    @Query("SELECT c FROM Course c WHERE c.currentBookings >= c.maxCapacity AND c.startTime > :now AND c.status = :status")
    List<Course> findFullyBookedCourses(@Param("now") LocalDateTime now, 
                                        @Param("status") CourseStatus status);
    
    @Query("SELECT c.category, COUNT(c) FROM Course c WHERE c.status = :status GROUP BY c.category")
    List<Object[]> countCoursesByCategory(@Param("status") CourseStatus status);
    
    @Query("SELECT c.trainer.id, COUNT(c) FROM Course c WHERE c.status = :status GROUP BY c.trainer.id")
    List<Object[]> countCoursesByTrainer(@Param("status") CourseStatus status);
}