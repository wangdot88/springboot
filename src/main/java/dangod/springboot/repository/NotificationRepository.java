package dangod.springboot.repository;

import dangod.springboot.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberId(Long memberId);
    List<Notification> findByMemberIdAndIsRead(Long memberId, Integer isRead);
    List<Notification> findByType(String type);
}
