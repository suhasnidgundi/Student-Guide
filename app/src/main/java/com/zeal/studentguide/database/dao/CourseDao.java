package com.zeal.studentguide.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.Student;  // Add this import
import java.util.List;

@Dao
public interface CourseDao {
    @Insert
    long insert(Course course);

    @Update
    void update(Course course);

    @Delete
    void delete(Course course);

    @Query("SELECT * FROM courses WHERE isActive = 1 ORDER BY courseName ASC")
    LiveData<List<Course>> getAllCourses();

    @Query("SELECT * FROM courses WHERE courseId = :courseId")
    LiveData<Course> getCourseById(String courseId);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCourse(Course course);

    @Update
    void updateCourse(Course course);

    @Delete
    void deleteCourse(Course course);

    @Query("SELECT * FROM courses WHERE semester = :semester AND isActive = 1")
    LiveData<List<Course>> getCoursesBySemester(String semester);

    @Query("SELECT * FROM courses WHERE facultyId = :facultyId AND isActive = 1")
    LiveData<List<Course>> getCoursesByFaculty(String facultyId);

    @Query("SELECT * FROM courses")
    List<Course> getAllCoursesSync();
}