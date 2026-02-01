package dangod.springboot.repository;

import dangod.springboot.entity.PhysicalTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PhysicalTestRepository extends JpaRepository<PhysicalTest, Long> {
    
    List<PhysicalTest> findByMemberIdOrderByTestTimeDesc(Long memberId);
    
    List<PhysicalTest> findTop5ByMemberIdOrderByTestTimeDesc(Long memberId);
    
    List<PhysicalTest> findByMemberIdAndTestTimeBetween(Long memberId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT p FROM PhysicalTest p WHERE p.memberId = :memberId AND p.testTime >= :threeMonthsAgo ORDER BY p.testTime ASC")
    List<PhysicalTest> findLastThreeMonthsData(Long memberId, LocalDateTime threeMonthsAgo);
    
    @Query("SELECT p FROM PhysicalTest p WHERE p.bmi > :bmiThreshold OR p.bodyFatRate > :fatThreshold OR p.restingHeartRate > :heartRateThreshold")
    List<PhysicalTest> findAbnormalData(double bmiThreshold, double fatThreshold, int heartRateThreshold);
}
