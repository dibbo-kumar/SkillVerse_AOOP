package com.skillverse.service;

import com.skillverse.dto.NotificationRequest;
import com.skillverse.model.Notification;
import com.skillverse.model.User;
import com.skillverse.repository.NotificationRepository;
import com.skillverse.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    public Optional<Notification> getById(Long id) {
        return notificationRepository.findById(id);
    }

    public Optional<Notification> createNotification(NotificationRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return Optional.empty();
        }
        Notification notification = new Notification(user, request.getTitle(), request.getMessage(),
                request.getType() != null ? request.getType() : "SYSTEM", request.getReferenceId());
        return Optional.of(notificationRepository.save(notification));
    }

    public void sendNotification(User user, String title, String message, String type, Long referenceId) {
        if (user == null) return;
        try {
            notificationRepository.save(new Notification(user, title, message, type, referenceId));
        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }

    public Optional<Notification> markAsRead(Long id) {
        return notificationRepository.findById(id).map(notif -> {
            notif.setRead(true);
            return notificationRepository.save(notif);
        });
    }

    public void markAllAsRead(Long userId) {
        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Notification n : list) {
            n.setRead(true);
        }
        notificationRepository.saveAll(list);
    }

    public boolean deleteNotification(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
