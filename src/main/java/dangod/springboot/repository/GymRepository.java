package dangod.springboot.repository;

import dangod.springboot.model.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
    
    Optional<Gym> findByStoreId(String storeId);
    
    List<Gym> findByCity(String city);
    
    List<Gym> findByIsActive(Boolean isActive);
}