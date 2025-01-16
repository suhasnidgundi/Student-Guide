package com.zeal.studentguide.database.dao;

import androidx.lifecycle.LiveData;import androidx.room.*;
import com.zeal.studentguide.models.Faculty;
import com.zeal.studentguide.models.FacultyWithUser;

import java.util.List;

@Dao
public interface FacultyDao {
    @Insert
    long insert(Faculty faculty);

    @Update
    void update(Faculty faculty);

    @Delete
    void delete(Faculty faculty);

    @Query("SELECT * FROM faculty WHERE department = :department")
    List<Faculty> getFacultyByDepartment(String department);

    @Query("SELECT * FROM faculty")
    LiveData<List<Faculty>> getAllFaculty();

    @Query("SELECT * FROM faculty")
    List<Faculty> getAllFacultySync();

    @Query("SELECT * FROM faculty WHERE facultyId = :facultyId")
    Faculty getFacultyById(String facultyId);

    @Transaction
    @Query("SELECT * FROM faculty")
    LiveData<List<FacultyWithUser>> getAllFacultyWithUsers();

}