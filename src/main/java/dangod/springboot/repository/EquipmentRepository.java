package dangod.springboot.repository;

import dangod.springboot.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByBranchIdAndStatus(Long branchId, Integer status);
    
    List<Equipment> findByBranchId(Long branchId);
    
    List<Equipment> findByTypeAndBranchId(String type, Long branchId);
    
    @Query("SELECT e.type, COUNT(e) FROM Equipment e WHERE e.branchId = ?1 GROUP BY e.type")
    List<Object[]> countByType(Long branchId);
    
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.branchId = ?1 AND e.status = 1")
    Long countActiveEquipment(Long branchId);
    
    @Query("SELECT COUNT(e) FROM Equipment e WHERE e.branchId = ?1")
    Long countTotalEquipment(Long branchId);
}