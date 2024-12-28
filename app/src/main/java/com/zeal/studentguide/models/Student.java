package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;
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

    @PropertyName("current_year")
    private int currentYear;

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

    private String course;
    private double cgpa;


    // Constructor matching the usage in AddUserActivity
    public Student(@NonNull String studentId) {
        this.studentId = studentId;
    }

    // Default constructor required for Firestore
    public Student() {
        this.studentId = "";
    }

    // Getters and Setters
    @NonNull
    public String getStudentId() { return studentId; }
    public void setStudentId(@NonNull String studentId) { this.studentId = studentId; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

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

}