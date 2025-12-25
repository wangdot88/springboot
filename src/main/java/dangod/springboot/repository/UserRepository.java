package dangod.springboot.repository;

import dangod.springboot.entity.User;
import dangod.springboot.entity.User.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRoleTypeAndIsActiveTrue(RoleType roleType);
    
    @Query("SELECT u FROM User u WHERE u.roleType = :roleType AND u.isActive = true ORDER BY " +
           "(SELECT COUNT(t) FROM Ticket t WHERE t.assignedAgent = u AND t.status IN ('PENDING', 'IN_PROGRESS')) ASC")
    List<User> findAvailableAgentsByWorkload(@Param("roleType") RoleType roleType);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}