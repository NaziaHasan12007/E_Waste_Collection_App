package com.ewaste.server.domain.repository;

import com.ewaste.server.domain.model.notification.Notification;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository {
    /**
     * Save a notification to the database
     * @param notification The notification to save
     * @return The saved notification with generated ID
     */
    Notification save(Notification notification);

    /**
     * Find a notification by its ID
     * @param notificationId The notification ID
     * @return Optional containing the notification if found
     */
    Optional<Notification> findById(Long notificationId);

    /**
     * Find all notifications for a user
     * @param userId The user ID
     * @return List of notifications for the user
     */
    List<Notification> findByUserId(Long userId);

    /**
     * Find unread notifications for a user
     * @param userId The user ID
     * @return List of unread notifications
     */
    List<Notification> findUnreadByUserId(Long userId);

    /**
     * Mark a notification as read
     * @param notificationId The notification ID
     */
    void markAsRead(Long notificationId);

    /**
     * Mark all notifications for a user as read
     * @param userId The user ID
     */
    void markAllAsReadByUserId(Long userId);

    /**
     * Delete a notification by its ID
     * @param notificationId The notification ID to delete
     */
    void deleteById(Long notificationId);

    /**
     * Delete all notifications for a user
     * @param userId The user ID
     */
    void deleteByUserId(Long userId);
}