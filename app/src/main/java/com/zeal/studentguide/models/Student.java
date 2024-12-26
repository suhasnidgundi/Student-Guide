package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
    private String rollNumber;
    private String semester;
    private String branch;
    private String batch;
    private double cgpa;
    private int currentYear;

    // Constructor
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

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }

    public String getBatch() { return batch; }
    public void setBatch(String batch) { this.batch = batch; }

    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }

    public int getCurrentYear() { return currentYear; }
    public void setCurrentYear(int currentYear) { this.currentYear = currentYear; }
}