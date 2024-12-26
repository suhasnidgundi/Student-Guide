package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    private String userId;
    private String email;
    private String name;
    private String role;
    private String profileImageUrl;
    private String phoneNumber;
    private boolean isActive;

    // Constructor
    public User(@NonNull String userId, String email, String name, String role) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.isActive = true;
    }

    public User() {
        // Default constructor required for Firestore
        this.isActive = true;
        this.userId = "";
        this.email = "";
        this.name = "";
        this.role = "";
        this.profileImageUrl = "";
        this.phoneNumber = "";
    }

    // Getters and Setters
    @NonNull
    public String getUserId() { return userId; }
    public void setUserId(@NonNull String userId) { this.userId = userId; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}