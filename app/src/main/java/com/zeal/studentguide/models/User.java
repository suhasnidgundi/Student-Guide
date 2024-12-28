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

    @NonNull
    private UserRole role;
    private String profileImageUrl;
    private String phoneNumber;
    private boolean isActive;

    private boolean isEmailVerified;
    private String lastLoginDate;
    private String registrationDate;


    // Constructor
    public User(@NonNull String userId, String email, String name, UserRole role) {
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
        this.role = UserRole.USER;
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

    public UserRole getRole() { return role; }

    public void setRole(UserRole role) { this.role = role; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isEmailVerified() {
        return isEmailVerified;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setEmailVerified(boolean emailVerified) {
        isEmailVerified = emailVerified;
    }
}