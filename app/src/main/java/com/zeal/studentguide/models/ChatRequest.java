package com.zeal.studentguide.models;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {
    @SerializedName("message")
    private String message;

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("timestamp")
    private long timestamp;

    public ChatRequest(String message, String sessionId) {
        this.message = message;
        this.sessionId = sessionId;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}