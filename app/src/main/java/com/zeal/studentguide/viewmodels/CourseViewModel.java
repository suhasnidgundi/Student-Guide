package com.zeal.studentguide.viewmodels;

import android.app.Application;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.zeal.studentguide.database.AppDatabase;
import com.zeal.studentguide.database.dao.CourseDao;
import com.zeal.studentguide.database.dao.FacultyDao;
import com.zeal.studentguide.database.dao.UserDao;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.Faculty;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.models.FacultyWithUser;
import com.zeal.studentguide.models.User;

public class CourseViewModel extends AndroidViewModel {
    private static final String TAG = "CourseViewModel";
    private static final String COURSES_COLLECTION = "courses";
    private static final String FACULTY_COLLECTION = "faculty";
    private static final String USERS_COLLECTION = "users";

    private CourseDao courseDao;
    private FacultyDao facultyDao;
    private UserDao userDao;
    private FirebaseFirestore db;
    private ExecutorService executorService;
    private Handler mainHandler;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private LiveData<List<Course>> coursesLiveData;
    private LiveData<List<Faculty>> facultyLiveData;
    public CourseViewModel(Application application) {
        super(application);

        try {
            // Initialize database and executor
            AppDatabase localDb = AppDatabase.getInstance(application);
            courseDao = localDb.courseDao();
            facultyDao = localDb.facultyDao();
            userDao = localDb.userDao();
            db = FirebaseFirestore.getInstance();
            executorService = Executors.newFixedThreadPool(2);
            mainHandler = new Handler(Looper.getMainLooper());

            // Initialize LiveData from Room
            coursesLiveData = courseDao.getAllCourses();
            facultyLiveData = facultyDao.getAllFaculty();

            // Load initial data
            loadInitialData();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing CourseViewModel: " + e.getMessage());
            setErrorOnMainThread("Failed to initialize: " + e.getMessage());
        }
    }

    private void loadInitialData() {
        setLoadingOnMainThread(true);
        syncWithFirebase();
        syncFacultyWithFirebase();
    }

    private void setLoadingOnMainThread(boolean loading) {
        mainHandler.post(() -> isLoading.setValue(loading));
    }

    private void setErrorOnMainThread(String error) {
        mainHandler.post(() -> errorMessage.setValue(error));
    }

    public void syncWithFirebase() {
        setLoadingOnMainThread(true);

        // First sync faculty and users
        syncFacultyWithFirebase().addOnCompleteListener(task -> {
            // Then sync courses
            db.collection(COURSES_COLLECTION)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        executorService.execute(() -> {
                            try {
                                List<Course> courses = new ArrayList<>();
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    Course course = doc.toObject(Course.class);
                                    if (course != null && facultyDao.getFacultyById(course.getFacultyId()) != null) {
                                        courses.add(course);
                                        courseDao.insertCourse(course);
                                    }
                                }
                                setLoadingOnMainThread(false);
                            } catch (Exception e) {
                                Log.e(TAG, "Error processing courses: " + e.getMessage());
                                setErrorOnMainThread("Error processing data: " + e.getMessage());
                                setLoadingOnMainThread(false);
                            }
                        });
                    });
        });
    }

    private Task<Void> syncFacultyWithFirebase() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        db.collection(USERS_COLLECTION)
                .whereEqualTo("role", "FACULTY")
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    // Existing faculty sync logic
                    tcs.setResult(null);
                })
                .addOnFailureListener(tcs::setException);

        return tcs.getTask();
    }

    public void insertCourse(Course course) {
        isLoading.setValue(true);
        db.collection(COURSES_COLLECTION)
                .document(course.getCourseId())
                .set(course)
                .addOnSuccessListener(aVoid -> {
                    executorService.execute(() -> {
                        courseDao.insertCourse(course);
                        isLoading.postValue(false);
                    });
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
                    executorService.execute(() -> {
                        courseDao.updateCourse(course);
                        isLoading.postValue(false);
                    });
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to update course: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public void deleteCourse(Course course) {
        isLoading.setValue(true);
        course.setActive(false);
        db.collection(COURSES_COLLECTION)
                .document(course.getCourseId())
                .set(course)
                .addOnSuccessListener(aVoid -> {
                    executorService.execute(() -> {
                        courseDao.deleteCourse(course);
                        isLoading.postValue(false);
                    });
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to delete course: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public LiveData<List<FacultyWithUser>> getAllFacultyWithUsers() {
        MutableLiveData<List<FacultyWithUser>> facultyWithUsersLiveData = new MutableLiveData<>();

        executorService.execute(() -> {
            try {
                List<Faculty> faculties = facultyDao.getAllFacultySync();
                List<FacultyWithUser> facultyWithUsers = new ArrayList<>();

                for (Faculty faculty : faculties) {
                    User user = userDao.getUserById(faculty.getFacultyId());
                    if (user != null) {
                        facultyWithUsers.add(new FacultyWithUser(faculty, user));
                    }
                }

                facultyWithUsersLiveData.postValue(facultyWithUsers);
            } catch (Exception e) {
                Log.e(TAG, "Error loading faculty with users: " + e.getMessage());
                setErrorOnMainThread("Failed to load faculty: " + e.getMessage());
            }
        });

        return facultyWithUsersLiveData;
    }

    // Getters for LiveData
    public LiveData<List<Faculty>> getAllFaculty() {
        return facultyLiveData;
    }

    public LiveData<List<Course>> getAllCourses() {
        return coursesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}