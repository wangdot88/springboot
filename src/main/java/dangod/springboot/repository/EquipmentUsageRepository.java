package dangod.springboot.repository;

import dangod.springboot.entity.EquipmentUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentUsageRepository extends JpaRepository<EquipmentUsage, Long> {
    
    List<EquipmentUsage> findByEquipmentId(Long equipmentId);
    
    List<EquipmentUsage> findByEquipmentIdAndStartTimeBetween(Long equipmentId, LocalDateTime start, LocalDateTime end);
    
    List<EquipmentUsage> findByMemberId(Long memberId);
    
    List<EquipmentUsage> findByMemberIdAndStartTimeBetween(Long memberId, LocalDateTime start, LocalDateTime end);
}
