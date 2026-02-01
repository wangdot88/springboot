package dangod.springboot.repository;

import dangod.springboot.model.EquipmentUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentUsageRepository extends JpaRepository<EquipmentUsage, Long> {
    
    List<EquipmentUsage> findByEquipmentId(String equipmentId);
    
    @Query("SELECT eu FROM EquipmentUsage eu WHERE eu.startTime >= :startTime AND eu.endTime <= :endTime")
    List<EquipmentUsage> findUsagesBetween(@Param("startTime") LocalDateTime startTime, 
                                            @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT eu FROM EquipmentUsage eu WHERE eu.startTime >= :startTime AND eu.endTime <= :endTime AND eu.gym.storeId = :storeId")
    List<EquipmentUsage> findUsagesBetweenByStore(@Param("startTime") LocalDateTime startTime, 
                                                  @Param("endTime") LocalDateTime endTime,
                                                  @Param("storeId") String storeId);
    
    @Query("SELECT eu.equipmentId, COUNT(eu), SUM(eu.durationMinutes) FROM EquipmentUsage eu WHERE eu.startTime >= :startTime AND eu.endTime <= :endTime GROUP BY eu.equipmentId ORDER BY COUNT(eu) DESC")
    List<Object[]> getEquipmentUsageStats(@Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT eu.equipmentId, COUNT(eu), SUM(eu.durationMinutes) FROM EquipmentUsage eu WHERE eu.startTime >= :startTime AND eu.endTime <= :endTime AND eu.gym.storeId = :storeId GROUP BY eu.equipmentId ORDER BY COUNT(eu) DESC")
    List<Object[]> getEquipmentUsageStatsByStore(@Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime,
                                                 @Param("storeId") String storeId);
    
    @Query("SELECT eu.equipmentId, COUNT(eu), SUM(eu.durationMinutes) FROM EquipmentUsage eu WHERE eu.startTime >= :startTime AND eu.endTime <= :endTime AND eu.equipmentId = :equipmentId")
    List<Object[]> getSpecificEquipmentUsageStats(@Param("startTime") LocalDateTime startTime, 
                                                   @Param("endTime") LocalDateTime endTime,
                                                   @Param("equipmentId") String equipmentId);
    
    @Query("SELECT eu.equipmentId, COUNT(eu), SUM(eu.durationMinutes) FROM EquipmentUsage eu WHERE eu.startTime >= :startTime AND eu.endTime <= :endTime AND eu.equipmentId = :equipmentId AND eu.gym.storeId = :storeId")
    List<Object[]> getSpecificEquipmentUsageStatsByStore(@Param("startTime") LocalDateTime startTime, 
                                                         @Param("endTime") LocalDateTime endTime,
                                                         @Param("equipmentId") String equipmentId,
                                                         @Param("storeId") String storeId);
    
    @Query("SELECT eu FROM EquipmentUsage eu WHERE eu.equipmentId = :equipmentId AND eu.startTime >= :startTime AND eu.endTime <= :endTime ORDER BY eu.startTime DESC")
    List<EquipmentUsage> findEquipmentUsagesByTimeRange(@Param("equipmentId") String equipmentId, 
                                                        @Param("startTime") LocalDateTime startTime, 
                                                        @Param("endTime") LocalDateTime endTime);
}