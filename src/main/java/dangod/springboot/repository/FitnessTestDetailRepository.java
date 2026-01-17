package dangod.springboot.repository;

import dangod.springboot.entity.FitnessTestDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessTestDetailRepository extends JpaRepository<FitnessTestDetail, Long> {
    List<FitnessTestDetail> findByRecordId(Long recordId);
    
    @Query("SELECT d FROM FitnessTestDetail d WHERE d.recordId IN ?1")
    List<FitnessTestDetail> findByRecordIds(List<Long> recordIds);
    
    @Query("SELECT d FROM FitnessTestDetail d WHERE d.recordId = ?1 AND d.isAbnormal = 1")
    List<FitnessTestDetail> findAbnormalMetricsByRecordId(Long recordId);
    
    void deleteByRecordId(Long recordId);
}