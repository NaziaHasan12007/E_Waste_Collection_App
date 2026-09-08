package com.ewaste.server.api.dto.response;

import java.sql.Timestamp;

public class NotificationResponseDto {

    private Long notificationId;
    private String message;
    private Boolean isRead;
    private Timestamp createdAt;

    // Constructors
    public NotificationResponseDto() {}

    public NotificationResponseDto(Long notificationId, String message,
                                   Boolean isRead, Timestamp createdAt) {
        this.notificationId = notificationId;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "NotificationResponseDto{" +
                "notificationId=" + notificationId +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}