package dangod.springboot.repository.equipment;

import dangod.springboot.entity.equipment.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    default Optional<Equipment> findById(Long id) {
        return Optional.ofNullable(findOne(id));
    }

    Optional<Equipment> findByCode(String code);

    List<Equipment> findByCategory(String category);

    List<Equipment> findByStatus(String status);

    @Query("SELECT e FROM Equipment e WHERE e.availableQuantity < e.minStock")
    List<Equipment> findLowStockEquipments();

    @Query("SELECT e FROM Equipment e WHERE e.availableQuantity = 0")
    List<Equipment> findOutOfStockEquipments();

    List<Equipment> findByNameContaining(String name);
}
