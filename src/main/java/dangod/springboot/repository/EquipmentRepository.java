package dangod.springboot.repository;

import dangod.springboot.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByStoreId(Long storeId);

    List<Equipment> findByType(Equipment.EquipmentType type);

    List<Equipment> findByStatus(Equipment.EquipmentStatus status);

    List<Equipment> findByStoreIdAndStatus(Long storeId, Equipment.EquipmentStatus status);
}
