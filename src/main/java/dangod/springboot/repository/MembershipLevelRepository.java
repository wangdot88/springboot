package dangod.springboot.repository;

import dangod.springboot.entity.MembershipLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipLevelRepository extends JpaRepository<MembershipLevel, Long> {
    Optional<MembershipLevel> findByLevelCode(String levelCode);
    List<MembershipLevel> findByStatus(Integer status);
}
