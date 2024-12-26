package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "notifications")
public class Notification {
    @PrimaryKey
    @NonNull
    private String notificationId;
    private String title;
    private String message;
    private String type;
    private long timestamp;
    private String targetUserId;
    private boolean isRead;

    // Constructor
    public Notification(@NonNull String notificationId, String title, String message, String type) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
    }

    // Getters and Setters
    @NonNull
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(@NonNull String notificationId) { this.notificationId = notificationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}