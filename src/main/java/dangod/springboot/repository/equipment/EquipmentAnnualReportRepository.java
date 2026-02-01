package dangod.springboot.repository.equipment;

import dangod.springboot.entity.equipment.EquipmentAnnualReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentAnnualReportRepository extends JpaRepository<EquipmentAnnualReport, Long> {

    List<EquipmentAnnualReport> findByYear(Integer year);

    EquipmentAnnualReport findByEquipmentIdAndYear(Long equipmentId, Integer year);
}
