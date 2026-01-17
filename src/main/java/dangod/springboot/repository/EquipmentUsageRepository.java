package dangod.springboot.repository;

import dangod.springboot.entity.Equipment;
import dangod.springboot.entity.EquipmentUsage;
import dangod.springboot.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentUsageRepository extends JpaRepository<EquipmentUsage, Long> {
    @Query("SELECT COUNT(eu) FROM EquipmentUsage eu WHERE eu.member = :member")
    long countByMember(@Param("member") Member member);
    
    @Query("SELECT eu FROM EquipmentUsage eu WHERE eu.member = :member ORDER BY eu.usageTime DESC")
    List<EquipmentUsage> findByMemberOrderByUsageTimeDesc(@Param("member") Member member, Pageable pageable);
    
    @Query("SELECT eu FROM EquipmentUsage eu WHERE eu.equipment = :equipment AND eu.usageTime BETWEEN :startDate AND :endDate")
    List<EquipmentUsage> findByEquipmentAndUsageTimeBetween(@Param("equipment") Equipment equipment, 
                                                            @Param("startDate") LocalDateTime startDate, 
                                                            @Param("endDate") LocalDateTime endDate);
    
    long countByUsageTimeBetween(LocalDateTime start, LocalDateTime end);
}
