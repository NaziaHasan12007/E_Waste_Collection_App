package com.ewaste.server.application.service;

import com.ewaste.server.api.dto.response.NotificationResponseDto;
import com.ewaste.server.domain.model.notification.Notification;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.repository.NotificationRepository;
import com.ewaste.server.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new notification for a user
     */
    @Transactional
    public NotificationResponseDto createNotification(Long userId, String message) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Create notification
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        notification.setRead(false);
        notification.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));

        Notification savedNotification = notificationRepository.save(notification);
        return mapToResponseDto(savedNotification);
    }

    /**
     * Get all notifications for a user
     */
    public List<NotificationResponseDto> getNotificationsByUser(Long userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return notificationRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notifications for a user
     */
    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return notificationRepository.findUnreadByUserId(userId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Get notification by ID
     */
    public NotificationResponseDto getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        return mapToResponseDto(notification);
    }

    /**
     * Mark a notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        // Check if notification exists
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

        notificationRepository.markAsRead(notificationId);
    }

    /**
     * Mark all notifications for a user as read
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        notificationRepository.markAllAsReadByUserId(userId);
    }

    /**
     * Delete a notification
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        // Check if notification exists
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));

        notificationRepository.deleteById(notificationId);
    }

    /**
     * Delete all notifications for a user
     */
    @Transactional
    public void deleteAllNotificationsForUser(Long userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        notificationRepository.deleteByUserId(userId);
    }

    /**
     * Get unread count for a user
     */
    public long getUnreadCount(Long userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return notificationRepository.findUnreadByUserId(userId).size();
    }

    /**
     * Create pickup status notification
     */
    @Transactional
    public void notifyPickupStatusChange(Long userId, Long pickupId, String oldStatus, String newStatus) {
        String message = String.format(
                "Your pickup request #%d status has been updated from '%s' to '%s'",
                pickupId, oldStatus, newStatus
        );
        createNotification(userId, message);
    }

    /**
     * Create collector assignment notification
     */
    @Transactional
    public void notifyCollectorAssignment(Long customerId, Long collectorId, Long pickupId) {
        String message = String.format(
                "Collector #%d has been assigned to your pickup request #%d",
                collectorId, pickupId
        );
        createNotification(customerId, message);
    }

    /**
     * Create reward notification
     */
    @Transactional
    public void notifyRewardEarned(Long customerId, int points, int totalBalance) {
        String message = String.format(
                "Congratulations! You've earned %d recycling points. Total balance: %d points",
                points, totalBalance
        );
        createNotification(customerId, message);
    }

    /**
     * Map Notification entity to Response DTO
     */
    private NotificationResponseDto mapToResponseDto(Notification notification) {
        return new NotificationResponseDto(
                notification.getNotificationId(),
                notification.getMessage(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}