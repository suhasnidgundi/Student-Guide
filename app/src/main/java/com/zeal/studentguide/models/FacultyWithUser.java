// FacultyWithUser.java
package com.zeal.studentguide.models;

import androidx.room.Embedded;
import androidx.room.Relation;

public class FacultyWithUser {
    @Embedded
    private Faculty faculty;

    @Embedded(prefix = "user_")
    private User user;

    // Default constructor required for Firestore
    public FacultyWithUser() {
        this.faculty = new Faculty();
        this.user = new User();
    }

    public FacultyWithUser(Faculty faculty, User user) {
        this.faculty = faculty;
        this.user = user;
    }

    // Getters and Setters
    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Convenience methods to directly access commonly used fields
    public String getName() {
        return user != null ? user.getName() : "";
    }

    public String getEmail() {
        return user != null ? user.getEmail() : "";
    }

    public String getPhoneNumber() {
        return user != null ? user.getPhoneNumber() : "";
    }

    public String getDepartment() {
        return faculty != null ? faculty.getDepartment() : "";
    }

    public String getDesignation() {
        return faculty != null ? faculty.getDesignation() : "";
    }

    public String getSpecialization() {
        return faculty != null ? faculty.getSpecialization() : "";
    }

    public int getExperienceYears() {
        return faculty != null ? faculty.getExperienceYears() : 0;
    }

    public String getQualifications() {
        return faculty != null ? faculty.getQualifications() : "";
    }

    public String getFacultyId() {
        return faculty != null ? faculty.getFacultyId() : "";
    }

    public boolean isActive() {
        return user != null && user.isActive();
    }
}