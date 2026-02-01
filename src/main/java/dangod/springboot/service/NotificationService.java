package dangod.springboot.service;

import dangod.springboot.entity.Notification;
import dangod.springboot.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Async
    public void createNotification(Long memberId, String title, String content) {
        Notification notification = new Notification();
        notification.setMemberId(memberId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType("system");
        notification.setStatus(0);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setUpdatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    public List<Notification> getMemberNotifications(Long memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setStatus(1);
            notification.setUpdatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }
}
