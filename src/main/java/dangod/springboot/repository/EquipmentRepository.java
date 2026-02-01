package dangod.springboot.repository;

import dangod.springboot.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    Optional<Equipment> findByEquipmentCode(String equipmentCode);
    
    List<Equipment> findByCategory(String category);
    
    List<Equipment> findByBrand(String brand);
    
    @Query("SELECT e FROM Equipment e WHERE e.availableQuantity <= e.minStockAlert")
    List<Equipment> findLowStockEquipment();
    
    @Query("SELECT e FROM Equipment e WHERE e.repairQuantity > 0")
    List<Equipment> findEquipmentUnderRepair();
    
    @Query("SELECT e FROM Equipment e WHERE e.borrowedQuantity > 0")
    List<Equipment> findBorrowedEquipment();
    
    boolean existsByEquipmentCode(String equipmentCode);
}