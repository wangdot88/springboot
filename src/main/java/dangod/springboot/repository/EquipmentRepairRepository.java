package dangod.springboot.repository;

import dangod.springboot.entity.EquipmentRepair;
import dangod.springboot.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentRepairRepository extends JpaRepository<EquipmentRepair, Long> {
    
    List<EquipmentRepair> findByEquipment(Equipment equipment);
    
    List<EquipmentRepair> findByEquipmentEquipmentCode(String equipmentCode);
    
    List<EquipmentRepair> findByStudentStudentId(String studentId);
    
    List<EquipmentRepair> findByStatus(String status);
    
    @Query("SELECT er FROM EquipmentRepair er WHERE er.status = 'REPORTED'")
    List<EquipmentRepair> findPendingRepairs();
    
    @Query("SELECT er FROM EquipmentRepair er WHERE er.reportDate BETWEEN :startDate AND :endDate")
    List<EquipmentRepair> findByReportDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(er) FROM EquipmentRepair er WHERE er.equipment.equipmentCode = :equipmentCode AND er.status != 'COMPLETED'")
    Long countActiveRepairsByEquipment(@Param("equipmentCode") String equipmentCode);
}