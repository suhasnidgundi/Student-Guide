package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

@Entity(tableName = "courses",
        foreignKeys = @ForeignKey(entity = Faculty.class,
                parentColumns = "facultyId",
                childColumns = "facultyId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("facultyId")})
public class Course {
    @PrimaryKey
    @NonNull
    private String courseId;

    @PropertyName("course_name")
    private String courseName;

    @PropertyName("course_code")
    private String courseCode;

    @PropertyName("semester")
    private String semester;

    @PropertyName("credits")
    private int credits;

    @PropertyName("description")
    private String description;

    @PropertyName("faculty_id")
    @NonNull
    private String facultyId;

    @PropertyName("is_active")
    private boolean isActive;

    @PropertyName("start_date")
    private String startDate;

    @PropertyName("end_date")
    private String endDate;

    @PropertyName("department")
    private String department;

    // Default constructor required for Firebase
    public Course() {
        this.courseId = "";
        this.facultyId = "";
    }

    // Constructor
    public Course(@NonNull String courseId, String courseName, String courseCode, int credits, @NonNull String facultyId, String department) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.credits = credits;
        this.facultyId = facultyId;
        this.isActive = true;
        this.department = department;
    }

    // Getters and setters with PropertyName annotations
    @PropertyName("start_date")
    public String getStartDate() { return startDate; }

    @PropertyName("start_date")
    public void setStartDate(String startDate) { this.startDate = startDate; }

    @PropertyName("end_date")
    public String getEndDate() { return endDate; }

    @PropertyName("end_date")
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @NonNull
    public String getCourseId() { return courseId; }
    public void setCourseId(@NonNull String courseId) { this.courseId = courseId; }

    @PropertyName("course_name")
    public String getCourseName() { return courseName; }

    @PropertyName("course_name")
    public void setCourseName(String courseName) { this.courseName = courseName; }

    @PropertyName("course_code")
    public String getCourseCode() { return courseCode; }

    @PropertyName("course_code")
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    @PropertyName("semester")
    public String getSemester() { return semester; }

    @PropertyName("semester")
    public void setSemester(String semester) { this.semester = semester; }

    @PropertyName("credits")
    public int getCredits() { return credits; }

    @PropertyName("credits")
    public void setCredits(int credits) { this.credits = credits; }

    @PropertyName("description")
    public String getDescription() { return description; }

    @PropertyName("description")
    public void setDescription(String description) { this.description = description; }

    @PropertyName("faculty_id")
    @NonNull
    public String getFacultyId() { return facultyId; }

    @PropertyName("faculty_id")
    public void setFacultyId(@NonNull String facultyId) { this.facultyId = facultyId; }

    @PropertyName("is_active")
    public boolean isActive() { return isActive; }

    @PropertyName("is_active")
    public void setActive(boolean active) { isActive = active; }

    @PropertyName("department")
    public String getDepartment() { return department; }

    @PropertyName("department")
    public void setDepartment(String department) { this.department = department; }
}