package dangod.springboot.repository;

import dangod.springboot.entity.EquipmentUsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EquipmentUsageRecordRepository extends JpaRepository<EquipmentUsageRecord, Long> {
    List<EquipmentUsageRecord> findByEquipmentIdAndEndTimeIsNotNullOrderByStartTimeDesc(Long equipmentId);
    
    List<EquipmentUsageRecord> findByMemberIdOrderByStartTimeDesc(Long memberId);
    
    @Query("SELECT e.equipmentId, COUNT(e), SUM(e.duration) FROM EquipmentUsageRecord e WHERE e.startTime >= ?1 AND e.endTime IS NOT NULL GROUP BY e.equipmentId")
    List<Object[]> findEquipmentUsageStatistics(Date startTime);
    
    @Query("SELECT e.equipmentId, COUNT(e), SUM(e.duration) FROM EquipmentUsageRecord e WHERE e.startTime >= ?1 AND e.startTime < ?2 AND e.endTime IS NOT NULL GROUP BY e.equipmentId ORDER BY COUNT(e) DESC")
    List<Object[]> findUsageRanking(Date startDate, Date endDate);
    
    @Query("SELECT COUNT(e) FROM EquipmentUsageRecord e WHERE e.equipmentId = ?1 AND e.startTime >= ?2 AND e.endTime IS NOT NULL")
    Long countUsageByEquipmentIdAndDate(Long equipmentId, Date startDate);
    
    @Query("SELECT COALESCE(SUM(e.duration), 0) FROM EquipmentUsageRecord e WHERE e.equipmentId = ?1 AND e.startTime >= ?2 AND e.endTime IS NOT NULL")
    Long sumUsageDurationByEquipmentIdAndDate(Long equipmentId, Date startDate);
    
    @Query("SELECT e FROM EquipmentUsageRecord e WHERE e.equipmentId = ?1 AND e.endTime IS NULL")
    EquipmentUsageRecord findCurrentUsage(Long equipmentId);
}