package com.zeal.studentguide.models;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("response")
    private String response;

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("timestamp")
    private long timestamp;

    @SerializedName("status")
    private String status;

    @SerializedName("error_message")
    private String errorMessage;

    public ChatResponse(String response, String sessionId) {
        this.response = response;
        this.sessionId = sessionId;
        this.timestamp = System.currentTimeMillis();
        this.status = "success";
    }

    // Getters
    public String getResponse() {
        return response;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Setters
    public void setResponse(String response) {
        this.response = response;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}