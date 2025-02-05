package com.zeal.studentguide.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private long timestamp;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        return timeFormat.format(new Date(timestamp));
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}