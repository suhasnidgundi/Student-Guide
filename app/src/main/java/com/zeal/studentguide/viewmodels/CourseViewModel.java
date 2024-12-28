package com.zeal.studentguide.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.ArrayList;

import com.google.firebase.firestore.DocumentSnapshot;
import com.zeal.studentguide.database.AppDatabase;
import com.zeal.studentguide.database.dao.CourseDao;
import com.zeal.studentguide.database.dao.FacultyDao;import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.Faculty;import com.zeal.studentguide.utils.FirebaseManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CourseViewModel extends AndroidViewModel {
    private static final String COURSES_COLLECTION = "courses";
    private static final String FACULTY_COLLECTION = "faculty";
    private CourseDao courseDao;
    private FacultyDao facultyDao;
    private FirebaseFirestore db;
    private LiveData<List<Course>> allCourses;
    private LiveData<List<Faculty>> allFaculty;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    public CourseViewModel(Application application) {
        super(application);
        AppDatabase localDb = AppDatabase.getInstance(application);
        courseDao = localDb.courseDao();
        facultyDao = localDb.facultyDao();
        db = FirebaseFirestore.getInstance();
        allCourses = courseDao.getAllCourses();
        allFaculty = facultyDao.getAllFaculty();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();

        // Initial sync with Firebase
        syncWithFirebase();
        syncFacultyWithFirebase();
    }

    public void syncWithFirebase() {
        isLoading.setValue(true);
        db.collection(COURSES_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Course> firebaseCourses = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Course course = document.toObject(Course.class);
                        if (course != null) {
                            firebaseCourses.add(course);
                        }
                    }
                    updateLocalDatabase(firebaseCourses);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to sync course data: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    // Add new method to sync faculty data
    private void syncFacultyWithFirebase() {
        isLoading.setValue(true);
        db.collection(FACULTY_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Faculty> firebaseFaculty = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Faculty faculty = document.toObject(Faculty.class);
                        if (faculty != null) {
                            firebaseFaculty.add(faculty);
                        }
                    }
                    updateLocalFacultyDatabase(firebaseFaculty);
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to sync faculty data: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    private void updateLocalFacultyDatabase(List<Faculty> firebaseFaculty) {
        new Thread(() -> {
            for (Faculty faculty : firebaseFaculty) {
                facultyDao.insert(faculty);
            }
        }).start();
    }

    private void updateLocalDatabase(List<Course> firebaseCourses) {
        new Thread(() -> {
            for (Course course : firebaseCourses) {
                courseDao.insertCourse(course);
            }
        }).start();
    }

    public void insertCourse(Course course) {
        isLoading.setValue(true);
        // Add to Firebase first
        db.collection(COURSES_COLLECTION)
                .document(course.getCourseId())
                .set(course)
                .addOnSuccessListener(aVoid -> {
                    // On success, add to local database
                    new Thread(() -> {
                        courseDao.insertCourse(course);
                        isLoading.postValue(false);
                    }).start();
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to add course: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void updateCourse(Course course) {
        isLoading.setValue(true);
        db.collection(COURSES_COLLECTION)
                .document(course.getCourseId())
                .set(course)
                .addOnSuccessListener(aVoid -> {
                    new Thread(() -> {
                        courseDao.updateCourse(course);
                        isLoading.postValue(false);
                    }).start();
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to update course: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void deleteCourse(Course course) {
        isLoading.setValue(true);
        // Soft delete in Firebase
        course.setActive(false);
        db.collection(COURSES_COLLECTION)
                .document(course.getCourseId())
                .set(course)
                .addOnSuccessListener(aVoid -> {
                    new Thread(() -> {
                        courseDao.deleteCourse(course);
                        isLoading.postValue(false);
                    }).start();
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to delete course: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public LiveData<List<Faculty>> getAllFaculty() {
        return allFaculty;
    }  

    public LiveData<List<Course>> getAllCourses() {
        return allCourses;
    }

    public LiveData<Course> getCourseById(String courseId) {
        return courseDao.getCourseById(courseId);
    }

    public LiveData<List<Course>> getCoursesBySemester(String semester) {
        return courseDao.getCoursesBySemester(semester);
    }

    public LiveData<List<Course>> getCoursesByFaculty(String facultyId) {
        return courseDao.getCoursesByFaculty(facultyId);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}