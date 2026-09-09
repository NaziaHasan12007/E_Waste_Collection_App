package com.ewaste.server.infrastructure.persistence.sqlite;

import com.ewaste.server.domain.model.notification.Notification;
import com.ewaste.server.domain.repository.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class SqliteNotificationRepository implements NotificationRepository {

    private final DatabaseConnectionManager connectionManager;

    public SqliteNotificationRepository(DatabaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public Notification save(Notification notification) {
        String sql;
        boolean isUpdate = notification.getNotificationId() != null;

        if (isUpdate) {
            sql = "UPDATE notifications SET user_id = ?, message = ?, is_read = ? WHERE notification_id = ?";
        } else {
            sql = "INSERT INTO notifications (user_id, message, is_read) VALUES (?, ?, ?)";
        }

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setInt(3, notification.isRead() ? 1 : 0);

            if (isUpdate) {
                stmt.setLong(4, notification.getNotificationId());
                stmt.executeUpdate();
            } else {
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        notification.setNotificationId(rs.getLong(1));
                    }
                }
            }

            return notification;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save notification: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Notification> findById(Long notificationId) {
        String sql = "SELECT * FROM notifications WHERE notification_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, notificationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find notification by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Notification> findByUserId(Long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find notifications by user ID: " + e.getMessage(), e);
        }
        return notifications;
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId) {
        String sql = "SELECT * FROM notifications WHERE user_id = ? AND is_read = 0 ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find unread notifications: " + e.getMessage(), e);
        }
        return notifications;
    }

    @Override
    public void markAsRead(Long notificationId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark notification as read: " + e.getMessage(), e);
        }
    }

    @Override
    public void markAllAsReadByUserId(Long userId) {
        String sql = "UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark all notifications as read: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Long notificationId) {
        String sql = "DELETE FROM notifications WHERE notification_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, notificationId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete notification: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM notifications WHERE user_id = ?";

        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete notifications for user: " + e.getMessage(), e);
        }
    }

    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getLong("notification_id"));
        notification.setUserId(rs.getLong("user_id"));
        notification.setMessage(rs.getString("message"));
        notification.setRead(rs.getInt("is_read") == 1);
        Timestamp timestamp = rs.getTimestamp("created_at");

        if (timestamp != null) {
            notification.setCreatedAt(timestamp.toLocalDateTime());
        }
        return notification;
    }
}