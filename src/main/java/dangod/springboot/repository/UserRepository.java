package dangod.springboot.repository;

import dangod.springboot.model.User;
import dangod.springboot.enums.UserRole;
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
    
    Optional<User> findByPhone(String phone);
    
    List<User> findByRole(UserRole role);
    
    List<User> findByGym_StoreId(String storeId);
    
    List<User> findByRoleAndGym_StoreId(UserRole role, String storeId);
    
    @Query("SELECT u FROM User u WHERE u.memberId = :memberId")
    Optional<User> findByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT u FROM User u WHERE u.trainerId = :trainerId")
    Optional<User> findByTrainerId(@Param("trainerId") Long trainerId);
    
    List<User> findByIsActive(Boolean isActive);
}