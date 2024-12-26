package com.zeal.studentguide.database.dao;

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

    @Query("SELECT * FROM courses WHERE courseId = :courseId")
    Course getCourseById(String courseId);

    @Query("SELECT * FROM courses WHERE facultyId = :facultyId")
    List<Course> getCoursesByFaculty(String facultyId);

    @Query("SELECT * FROM courses WHERE semester = :semester AND isActive = 1")
    List<Course> getCoursesBySemester(String semester);

    @Query("SELECT * FROM courses WHERE isActive = 1")
    List<Course> getActiveCourses();

    @Transaction
    @Query("SELECT students.* FROM students " +
            "INNER JOIN student_course_enrollments ON students.studentId = student_course_enrollments.studentId " +
            "WHERE student_course_enrollments.courseId = :courseId AND student_course_enrollments.isActive = 1")
    List<Student> getEnrolledStudents(String courseId);  // Changed return type to List<Student>
}