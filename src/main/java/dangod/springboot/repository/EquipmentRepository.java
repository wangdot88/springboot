package dangod.springboot.repository;

import dangod.springboot.model.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByEquipmentNo(String equipmentNo);
    List<Equipment> findByCategory(String category);
    List<Equipment> findByStatus(String status);
    
    @Query("SELECT e FROM Equipment e WHERE e.availableCount < e.alertThreshold")
    List<Equipment> findLowStockEquipment();
    
    @Query("SELECT e FROM Equipment e WHERE e.availableCount = 0")
    List<Equipment> findOutOfStockEquipment();
    
    @Query("SELECT e FROM Equipment e WHERE e.brokenCount > 0")
    List<Equipment> findBrokenEquipment();
    
    @Query("SELECT e.category, COUNT(e) as count, SUM(e.totalCount) as total FROM Equipment e GROUP BY e.category")
    List<Object[]> findEquipmentStatisticsByCategory();
    
    List<Equipment> findByEquipmentNameContaining(String equipmentName);
    List<Equipment> findByEquipmentNameContainingAndCategory(String equipmentName, String category);
}
