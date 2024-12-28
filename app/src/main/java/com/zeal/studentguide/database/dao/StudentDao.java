package com.zeal.studentguide.database.dao;

import androidx.room.*;

import com.zeal.studentguide.models.Student;

@Dao
public interface StudentDao {
    @Insert
    long insert(Student student);

    @Update
    void update(Student student);

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    Student getStudentById(String studentId);

}