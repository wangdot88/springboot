package dangod.springboot.repository;

import dangod.springboot.entity.RenewalPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RenewalPredictionRepository extends JpaRepository<RenewalPrediction, Long> {
    
    Optional<RenewalPrediction> findByCardIdAndPredictionDate(Long cardId, LocalDate predictionDate);
    
    List<RenewalPrediction> findByStatusOrderByRenewalProbabilityDesc(Integer status);
    
    List<RenewalPrediction> findByMemberId(Long memberId);
    
    List<RenewalPrediction> findByPredictionDateBetween(LocalDate startDate, LocalDate endDate);
}
