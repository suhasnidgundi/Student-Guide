package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

@Entity(tableName = "students",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "studentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("studentId")})
public class Student {
    @PrimaryKey
    @NonNull
    private String studentId; // This is also the userId from User table

    @PropertyName("roll_number")
    private String rollNumber;

    private String semester;
    private String branch;
    private String batch;
    private double cgpa;

    @PropertyName("current_year")
    private int currentYear;

    // Additional fields for profile
    @PropertyName("profile_image_url")
    private String profileImageUrl;

    @PropertyName("email")
    private String email;

    @PropertyName("full_name")
    private String fullName;

    // Fields for academic tracking
    @PropertyName("attendance_percentage")
    private double attendancePercentage;

    @PropertyName("active_backlog_count")
    private int activeBacklogCount;

    // Default constructor for Firebase
    @Ignore
    public Student() {}

    // Constructor for Room
    public Student(@NonNull String studentId, String rollNumber, String semester, String branch) {
        this.studentId = studentId;
        this.rollNumber = rollNumber;
        this.semester = semester;
        this.branch = branch;
    }

    // Getters and Setters
    @NonNull
    public String getStudentId() { return studentId; }
    public void setStudentId(@NonNull String studentId) { this.studentId = studentId; }

    @PropertyName("roll_number")
    public String getRollNumber() { return rollNumber; }
    @PropertyName("roll_number")
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    @PropertyName("current_year")
    public int getCurrentYear() { return currentYear; }
    @PropertyName("current_year")
    public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }

    @PropertyName("profile_image_url")
    public String getProfileImageUrl() { return profileImageUrl; }
    @PropertyName("profile_image_url")
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @PropertyName("full_name")
    public String getFullName() { return fullName; }
    @PropertyName("full_name")
    public void setFullName(String fullName) { this.fullName = fullName; }

    @PropertyName("attendance_percentage")
    public double getAttendancePercentage() { return attendancePercentage; }
    @PropertyName("attendance_percentage")
    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    @PropertyName("active_backlog_count")
    public int getActiveBacklogCount() { return activeBacklogCount; }
    @PropertyName("active_backlog_count")
    public void setActiveBacklogCount(int activeBacklogCount) {
        this.activeBacklogCount = activeBacklogCount;
    }

    // Helper method to check if profile is complete
    @Exclude
    public boolean isProfileComplete() {
        return false;
    }

    public String getDepartment() {
        return null;
    }
}