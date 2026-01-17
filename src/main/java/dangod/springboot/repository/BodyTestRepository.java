package dangod.springboot.repository;

import dangod.springboot.entity.BodyTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BodyTestRepository extends JpaRepository<BodyTest, Long> {
    List<BodyTest> findByMemberId(Long memberId);
    
    @Query("SELECT bt FROM BodyTest bt WHERE bt.memberId = :memberId ORDER BY bt.testDate DESC")
    List<BodyTest> findLatestByMemberId(@Param("memberId") Long memberId, @Param("limit") int limit);
    
    @Query("SELECT bt FROM BodyTest bt WHERE bt.memberId = :memberId ORDER BY bt.testDate DESC")
    List<BodyTest> findAllByMemberIdOrderByTestDateDesc(@Param("memberId") Long memberId);
    
    @Query("SELECT bt FROM BodyTest bt WHERE bt.testDate >= :startDate AND bt.testDate <= :endDate")
    List<BodyTest> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT bt FROM BodyTest bt WHERE bt.memberId = :memberId AND bt.testDate >= :startDate AND bt.testDate <= :endDate ORDER BY bt.testDate ASC")
    List<BodyTest> findByMemberAndDateRange(@Param("memberId") Long memberId, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(bt) FROM BodyTest bt WHERE bt.memberId = :memberId")
    long countByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT bt FROM BodyTest bt WHERE (bt.bmi > 28 OR bt.bmi < 18.5 OR bt.bodyFat > 30 OR bt.visceralFat > 15) AND bt.status = 1")
    List<BodyTest> findAbnormalTests();
    
    List<BodyTest> findByTestDateAfter(LocalDateTime testDate);
    
    long countByTestDateBetween(LocalDateTime start, LocalDateTime end);
}
