package dangod.springboot.repository;

import dangod.springboot.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
    
    Optional<Coach> findByCoachNo(String coachNo);
    
    List<Coach> findByGymId(Long gymId);
    
    List<Coach> findByGymIdAndStatus(Long gymId, Integer status);
    
    List<Coach> findBySpecialtyContaining(String specialty);
}
