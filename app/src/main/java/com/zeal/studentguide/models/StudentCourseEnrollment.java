package com.zeal.studentguide.models;

import android.os.Build;
import androidx.annotation.NonNull;  // Add this import
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import java.time.LocalDate;

@Entity(tableName = "student_course_enrollments",
        primaryKeys = {"studentId", "courseId"},
        foreignKeys = {
                @ForeignKey(entity = Student.class,
                        parentColumns = "studentId",
                        childColumns = "studentId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseId",
                        childColumns = "courseId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index("studentId"),
                @Index("courseId")
        })
public class StudentCourseEnrollment {
    @NonNull
    private String studentId;
    @NonNull
    private String courseId;
    private String enrollmentDate;
    private String grade;
    private boolean isActive;

    public StudentCourseEnrollment(@NonNull String studentId, @NonNull String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.enrollmentDate = LocalDate.now().toString();
        }
        this.isActive = true;
    }

    // Update getters and setters with @NonNull
    @NonNull
    public String getStudentId() { return studentId; }
    public void setStudentId(@NonNull String studentId) { this.studentId = studentId; }

    @NonNull
    public String getCourseId() { return courseId; }
    public void setCourseId(@NonNull String courseId) { this.courseId = courseId; }

    public String getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(String enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}