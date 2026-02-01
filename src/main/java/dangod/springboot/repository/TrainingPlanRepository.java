package dangod.springboot.repository;

import dangod.springboot.model.TrainingPlan;
import dangod.springboot.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingPlanRepository extends JpaRepository<TrainingPlan, Long> {
    
    List<TrainingPlan> findByMember_Id(Long memberId);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.member.id = :memberId AND tp.isActive = true")
    List<TrainingPlan> findActivePlansForMember(@Param("memberId") Long memberId);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.member.id = :memberId ORDER BY tp.creationDate DESC")
    List<TrainingPlan> findByMemberIdOrderByDateDesc(@Param("memberId") Long memberId);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.trainer.id = :trainerId")
    List<TrainingPlan> findByTrainerId(@Param("trainerId") Long trainerId);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.startDate <= :date AND tp.endDate >= :date")
    List<TrainingPlan> findPlansForDate(@Param("date") LocalDate date);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.endDate < :date AND tp.isActive = true")
    List<TrainingPlan> findExpiredActivePlans(@Param("date") LocalDate date);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.member.id = :memberId AND tp.endDate < :date AND tp.isActive = true")
    TrainingPlan findLatestExpiredPlanForMember(@Param("memberId") Long memberId, 
                                                 @Param("date") LocalDate date);
    
    @Query("SELECT tp FROM TrainingPlan tp WHERE tp.member.id = :memberId AND tp.isActive = true ORDER BY tp.creationDate DESC")
    TrainingPlan findLatestActivePlanForMember(@Param("memberId") Long memberId);
}