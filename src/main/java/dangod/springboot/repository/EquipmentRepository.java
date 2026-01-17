package dangod.springboot.repository;

import dangod.springboot.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    Equipment findByEquipmentNo(String equipmentNo);

    List<Equipment> findByCategory(String category);

    List<Equipment> findByStatus(String status);

    @Query("SELECT e FROM Equipment e WHERE e.availableCount <= e.minStock")
    List<Equipment> findLowStockEquipment();

    @Query("SELECT e FROM Equipment e WHERE e.name LIKE %:keyword% OR e.equipmentNo LIKE %:keyword% OR e.category LIKE %:keyword%")
    List<Equipment> searchByKeyword(@Param("keyword") String keyword);
}
