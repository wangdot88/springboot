package dangod.springboot.repository.equipment;

import dangod.springboot.entity.equipment.EquipmentRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepairRepository extends JpaRepository<EquipmentRepair, Long> {

    default Optional<EquipmentRepair> findById(Long id) {
        return Optional.ofNullable(findOne(id));
    }

    List<EquipmentRepair> findByEquipmentId(Long equipmentId);

    List<EquipmentRepair> findByStatus(String status);

    @Query("SELECT r FROM EquipmentRepair r WHERE r.reportDate BETWEEN :startDate AND :endDate")
    List<EquipmentRepair> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COALESCE(SUM(r.repairCost), 0) FROM EquipmentRepair r WHERE r.reportDate BETWEEN :startDate AND :endDate")
    Double sumRepairCost(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
