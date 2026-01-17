package dangod.springboot.repository;

import dangod.springboot.entity.EquipmentUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EquipmentUsageRepository extends JpaRepository<EquipmentUsage, Long> {

    List<EquipmentUsage> findByEquipmentId(Long equipmentId);

    List<EquipmentUsage> findByMemberId(Long memberId);

    List<EquipmentUsage> findByStoreId(Long storeId);

    @Query("SELECT eu FROM EquipmentUsage eu WHERE eu.equipmentId = :equipmentId AND eu.startTime BETWEEN :startDate AND :endDate")
    List<EquipmentUsage> findByEquipmentIdAndDateRange(@Param("equipmentId") Long equipmentId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT SUM(eu.duration) FROM EquipmentUsage eu WHERE eu.equipmentId = :equipmentId AND eu.startTime BETWEEN :startDate AND :endDate")
    Double getTotalUsageHours(@Param("equipmentId") Long equipmentId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(eu) FROM EquipmentUsage eu WHERE eu.equipmentId = :equipmentId AND eu.startTime BETWEEN :startDate AND :endDate")
    Long getUsageCount(@Param("equipmentId") Long equipmentId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
