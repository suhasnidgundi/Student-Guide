package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

@Entity(tableName = "faculty_schedules",
        foreignKeys = {
                @ForeignKey(
                        entity = Faculty.class,
                        parentColumns = "facultyId",
                        childColumns = "facultyId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Course.class,
                        parentColumns = "courseId",
                        childColumns = "courseId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {
                @Index(value = {"facultyId", "courseId", "timeSlot", "dayOfWeek"}, unique = true),
                @Index("facultyId"),
                @Index("courseId")
        })
public class FacultySchedule {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String facultyId;

    @NonNull
    private String courseId;

    private String timeSlot;
    private String dayOfWeek;
    private String roomNumber;
    private String semester;
    private boolean isActive;

    // Constructor, getters, and setters
    // ... implementation
}