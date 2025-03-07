package com.zeal.studentguide.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

public class Announcement {
    @DocumentId
    private String id;
    private String type;
    private String message;
    private String department; // can be "All" or specific department
    @ServerTimestamp
    private Timestamp timestamp;
    private String createdBy; // admin user ID

    // Required empty constructor for Firestore
    public Announcement() {
    }

    public Announcement(String type, String message, String department, String createdBy) {
        this.type = type;
        this.message = message;
        this.department = department;
        this.createdBy = createdBy;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}