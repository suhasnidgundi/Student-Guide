package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;import androidx.room.Index;import androidx.room.PrimaryKey;
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
    private String courseName;
    private String courseCode;
    private String semester;
    private int credits;
    private String description;

    @PropertyName("faculty_id")
    @NonNull
    private String facultyId;
    private boolean isActive;

    @PropertyName("start_date")
    private String startDate;

    @PropertyName("end_date")
    private String endDate;


    // Constructor
    public Course(@NonNull String courseId, String courseName, String courseCode, int credits, @NonNull String facultyId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.credits = credits;
        this.facultyId = facultyId;
        this.isActive = true;
    }



    // Getters and Setters
    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }



    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

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

    @NonNull
    public String getFacultyId() { return facultyId; }
    public void setFacultyId(@NonNull String facultyId) { this.facultyId = facultyId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}