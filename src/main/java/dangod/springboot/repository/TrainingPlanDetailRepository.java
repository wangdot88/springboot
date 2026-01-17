package dangod.springboot.repository;

import dangod.springboot.entity.TrainingPlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingPlanDetailRepository extends JpaRepository<TrainingPlanDetail, Long> {
    List<TrainingPlanDetail> findByPlanIdOrderByDayOfWeekAsc(Long planId);
    
    List<TrainingPlanDetail> findByPlanIdAndDayOfWeek(Long planId, Integer dayOfWeek);
    
    void deleteByPlanId(Long planId);
    
    @org.springframework.data.jpa.repository.Query("SELECT d FROM TrainingPlanDetail d WHERE d.planId = ?1")
    List<TrainingPlanDetail> findAllByPlanId(Long planId);
}