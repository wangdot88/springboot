package dangod.springboot.repository;

import dangod.springboot.entity.FitnessTestRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FitnessTestRecordRepository extends JpaRepository<FitnessTestRecord, Long> {
    List<FitnessTestRecord> findByMemberIdOrderByTestDateDesc(Long memberId);
    
    FitnessTestRecord findTopByMemberIdOrderByTestDateDesc(Long memberId);
    
    @Query("SELECT r FROM FitnessTestRecord r WHERE r.memberId = ?1 AND r.testDate >= ?2 ORDER BY r.testDate ASC")
    List<FitnessTestRecord> findByMemberIdAndTestDateAfter(Long memberId, Date afterDate);
    
    @Query("SELECT COUNT(r) FROM FitnessTestRecord r WHERE r.memberId = ?1")
    Long countByMemberId(Long memberId);
}