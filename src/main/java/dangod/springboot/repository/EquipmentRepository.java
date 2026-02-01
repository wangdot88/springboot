package dangod.springboot.repository;

import dangod.springboot.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    
    List<Equipment> findByGymId(Long gymId);
    
    List<Equipment> findByType(String type);
    
    List<Equipment> findByGymIdAndStatus(Long gymId, Integer status);
}
