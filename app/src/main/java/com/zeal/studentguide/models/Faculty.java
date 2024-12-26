package com.zeal.studentguide.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.room.PrimaryKey;

@Entity(tableName = "faculty",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "facultyId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("facultyId")})
public class Faculty {
    @PrimaryKey
    @NonNull
    private String facultyId; // This is also the userId from User table
    private String department;
    private String designation;
    private String specialization;
    private int experienceYears;
    private String qualifications;

    // Constructor
    public Faculty(@NonNull String facultyId, String department, String designation) {
        this.facultyId = facultyId;
        this.department = department;
        this.designation = designation;
    }

    // Getters and Setters
    @NonNull
    public String getFacultyId() { return facultyId; }
    public void setFacultyId(@NonNull String facultyId) { this.facultyId = facultyId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }
}