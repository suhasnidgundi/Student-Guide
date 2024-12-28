package com.zeal.studentguide.models;


import androidx.room.*;
import androidx.annotation.NonNull;

@Entity(tableName = "course_enrollments",
        primaryKeys = {"courseId", "studentId"},
        foreignKeys = {
                @ForeignKey(entity = Course.class,
                        parentColumns = "courseId",
                        childColumns = "courseId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Student.class,
                        parentColumns = "studentId",
                        childColumns = "studentId",
                        onDelete = ForeignKey.CASCADE)
        })
public class CourseEnrollment {
    @NonNull
    public String courseId;
    @NonNull
    public String studentId;
    public String enrollmentDate;
    
    public CourseEnrollment(@NonNull String courseId, @NonNull String studentId, String enrollmentDate) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.enrollmentDate = enrollmentDate;
    }
}