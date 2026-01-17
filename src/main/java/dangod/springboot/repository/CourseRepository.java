package dangod.springboot.repository;

import dangod.springboot.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStoreId(Long storeId);
    
    List<Course> findByCoachId(Long coachId);
    
    List<Course> findByCourseTypeId(Long courseTypeId);
    
    List<Course> findByStoreIdAndStatus(Long storeId, Integer status);
    
    List<Course> findByStatus(Integer status);
    
    @Query("SELECT c FROM Course c WHERE c.courseDate >= :startDate AND c.courseDate <= :endDate AND c.storeId = :storeId ORDER BY c.courseDate ASC")
    List<Course> findCoursesByDateRange(@Param("storeId") Long storeId, 
                                        @Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM Course c WHERE c.courseDate >= :startDate AND c.courseDate <= :endDate AND c.coachId = :coachId ORDER BY c.courseDate ASC")
    List<Course> findCoursesByCoachAndDateRange(@Param("coachId") Long coachId, 
                                                @Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT c FROM Course c WHERE c.currentParticipants >= c.maxParticipants AND c.status = 1")
    List<Course> findFullCourses();
    
    @Query("SELECT c FROM Course c WHERE c.status = 1 ORDER BY c.heatScore DESC")
    List<Course> findHotCourses(@Param("limit") int limit);
    
    @Query("SELECT c FROM Course c WHERE c.courseDate >= :now AND c.status = 1 ORDER BY c.heatScore DESC")
    List<Course> findUpcomingHotCourses(@Param("now") LocalDateTime now);
    
    @Query("SELECT c FROM Course c WHERE c.courseDate >= :startDate AND c.status = 1")
    List<Course> findUpcomingCourses(@Param("startDate") LocalDateTime startDate);
}
