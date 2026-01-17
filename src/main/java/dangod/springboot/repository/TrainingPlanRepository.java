package dangod.springboot.repository;

import dangod.springboot.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findByMemberId(Long memberId);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.memberId = :memberId ORDER BY tp.createTime DESC")
    List<TrainingPlan> findLatestByMemberId(@Param("memberId") Long memberId);
    
    TrainingPlan findByMemberIdAndStatus(Long memberId, Integer status);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.memberId = :memberId AND tp.startDate >= :startDate")
    List<TrainingPlan> findUpcomingPlans(@Param("memberId") Long memberId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT COUNT(tp) FROM TrainingPlan tp WHERE tp.coachId = :coachId")
    long countByCoachId(@Param("coachId") Long coachId);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.status = 1")
    List<TrainingPlan> findActivePlans();
}
