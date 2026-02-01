package dangod.springboot.repository;

import dangod.springboot.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    
    List<Notification> findByMemberIdAndStatus(Long memberId, Integer status);
}
