package dangod.springboot.repository;

import dangod.springboot.entity.TrainingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {

    List<TrainingPlan> findByMemberId(Long memberId);

    List<TrainingPlan> findByStatus(TrainingPlan.PlanStatus status);

    List<TrainingPlan> findByMemberIdAndStatus(Long memberId, TrainingPlan.PlanStatus status);
}
