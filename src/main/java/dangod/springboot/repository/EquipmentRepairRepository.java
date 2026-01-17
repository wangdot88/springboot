package dangod.springboot.repository;

import dangod.springboot.entity.EquipmentRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface EquipmentRepairRepository extends JpaRepository<EquipmentRepair, Long> {

    List<EquipmentRepair> findByEquipmentId(Long equipmentId);

    List<EquipmentRepair> findByStatus(String status);

    @Query("SELECT er FROM EquipmentRepair er WHERE er.reportDate BETWEEN :startDate AND :endDate")
    List<EquipmentRepair> findByReportDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(er) FROM EquipmentRepair er WHERE er.reportDate BETWEEN :startDate AND :endDate")
    Long countRepairsByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT SUM(er.repairCost) FROM EquipmentRepair er WHERE er.reportDate BETWEEN :startDate AND :endDate")
    Double getTotalRepairCostByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT er.equipment.id, COUNT(er) as repairCount FROM EquipmentRepair er WHERE er.reportDate BETWEEN :startDate AND :endDate GROUP BY er.equipment.id ORDER BY repairCount DESC")
    List<Object[]> findMostRepairedEquipment(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
