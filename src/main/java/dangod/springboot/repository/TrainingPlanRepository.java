package dangod.springboot.repository;

import dangod.springboot.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    
    List<TrainingPlan> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    
    List<TrainingPlan> findByTestId(Long testId);
}
