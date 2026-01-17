package dangod.springboot.repository;

import dangod.springboot.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    Coach findByCoachCode(String coachCode);
    
    Coach findByPhone(String phone);
    
    List<Coach> findByStoreId(Long storeId);
    
    List<Coach> findByStatus(Integer status);
    
    List<Coach> findBySpecialtyContaining(String specialty);
    
    @Query("SELECT c FROM Coach c WHERE c.storeId = :storeId AND c.status = 1")
    List<Coach> findActiveCoachesByStore(@Param("storeId") Long storeId);
    
    @Query("SELECT c FROM Coach c WHERE c.rating >= :minRating AND c.status = 1 ORDER BY c.rating DESC")
    List<Coach> findTopRatedCoaches(@Param("minRating") Double minRating, @Param("limit") int limit);
    
    @Query("SELECT c FROM Coach c WHERE c.courseCount >= :minCourseCount AND c.status = 1 ORDER BY c.courseCount DESC")
    List<Coach> findPopularCoaches(@Param("minCourseCount") Integer minCourseCount);
}
