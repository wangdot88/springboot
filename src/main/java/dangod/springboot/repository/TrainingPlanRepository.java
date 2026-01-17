package dangod.springboot.repository;

import dangod.springboot.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    List<TrainingPlan> findByMemberIdAndStatusOrderByStartDateDesc(Long memberId, Integer status);
    
    List<TrainingPlan> findByMemberIdOrderByStartDateDesc(Long memberId);
    
    @Query("SELECT p FROM TrainingPlan p WHERE p.memberId = ?1 AND p.status = 1 AND p.startDate <= ?2 AND p.endDate >= ?2")
    List<TrainingPlan> findCurrentPlansByMemberId(Long memberId, Date date);
    
    @Query("SELECT COUNT(p) FROM TrainingPlan p WHERE p.memberId = ?1 AND p.status = 1")
    Long countActivePlansByMemberId(Long memberId);
}