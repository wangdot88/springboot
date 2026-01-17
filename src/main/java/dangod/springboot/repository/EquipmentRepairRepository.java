package dangod.springboot.repository;

import dangod.springboot.model.entity.EquipmentRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepairRepository extends JpaRepository<EquipmentRepair, Long> {
    Optional<EquipmentRepair> findByRepairNo(String repairNo);
    List<EquipmentRepair> findByEquipmentId(Long equipmentId);
    List<EquipmentRepair> findByReporterId(Long reporterId);
    List<EquipmentRepair> findByStatus(String status);
    List<EquipmentRepair> findByStatusIn(List<String> statusList);
    List<EquipmentRepair> findByEquipmentIdAndStatus(Long equipmentId, String status);
}
