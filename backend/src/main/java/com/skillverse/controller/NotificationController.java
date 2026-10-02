package com.skillverse.controller;

import com.skillverse.dto.NotificationRequest;
import com.skillverse.model.Notification;
import com.skillverse.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for User Notifications.
 * Architecture Flow: User/Client -> Controller -> Service -> Repository -> Model (Entity)
 */
@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Get User Notifications with PathVariable
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    /**
     * Standard CRUD: Get Notification by ID with PathVariable
     */
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(@PathVariable Long id) {
        return notificationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get unread count with PathVariable
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * Standard CRUD: Save / Create notification with RequestBody
     */
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, Object> payload) {
        NotificationRequest req = new NotificationRequest();
        if (payload.containsKey("userId")) req.setUserId(Long.parseLong(payload.get("userId").toString()));
        if (payload.containsKey("title")) req.setTitle(payload.get("title").toString());
        if (payload.containsKey("message")) req.setMessage(payload.get("message").toString());
        if (payload.containsKey("type")) req.setType(payload.get("type").toString());
        if (payload.containsKey("referenceId") && payload.get("referenceId") != null) {
            req.setReferenceId(Long.parseLong(payload.get("referenceId").toString()));
        }

        return notificationService.createNotification(req)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Mark notification as read with PathVariable
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mark all user notifications as read with PathVariable
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
    }

    /**
     * Standard CRUD: Delete Notification with PathVariable
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (notificationService.deleteNotification(id)) {
            return ResponseEntity.ok(Map.of("message", "Notification deleted"));
        }
        return ResponseEntity.notFound().build();
    }
}
