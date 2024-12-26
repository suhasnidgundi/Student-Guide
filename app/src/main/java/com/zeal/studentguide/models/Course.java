package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "courses")
public class Course {
    @PrimaryKey
    @NonNull
    private String courseId;
    private String courseName;
    private String courseCode;
    private String semester;
    private int credits;
    private String description;
    private String facultyId;
    private boolean isActive;

    // Constructor
    public Course(@NonNull String courseId, String courseName, String courseCode, int credits) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.credits = credits;
        this.isActive = true;
    }

    // Getters and Setters
    @NonNull
    public String getCourseId() { return courseId; }
    public void setCourseId(@NonNull String courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}