package com.zeal.studentguide.database.dao;

import androidx.room.*;
import com.zeal.studentguide.models.Student;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.StudentCourseEnrollment;

import java.util.List;

@Dao
public interface StudentDao {
    @Insert
    long insert(Student student);

    @Update
    void update(Student student);

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    Student getStudentById(String studentId);

    @Transaction
    @Query("SELECT students.* FROM students " +
            "INNER JOIN student_course_enrollments ON students.studentId = student_course_enrollments.studentId " +
            "WHERE student_course_enrollments.courseId = :courseId AND student_course_enrollments.isActive = 1")
    List<Student> getStudentsByCourse(String courseId);

    @Insert
    void insertEnrollment(StudentCourseEnrollment enrollment);

    @Update
    void updateEnrollment(StudentCourseEnrollment enrollment);

    @Query("DELETE FROM student_course_enrollments WHERE studentId = :studentId AND courseId = :courseId")
    void removeEnrollment(String studentId, String courseId);

    @Query("SELECT * FROM student_course_enrollments WHERE studentId = :studentId")
    List<StudentCourseEnrollment> getStudentEnrollments(String studentId);

    @Transaction
    @Query("SELECT courses.* FROM courses " +
            "INNER JOIN student_course_enrollments ON courses.courseId = student_course_enrollments.courseId " +
            "WHERE student_course_enrollments.studentId = :studentId AND student_course_enrollments.isActive = 1")
    List<Course> getEnrolledCourses(String studentId);
}