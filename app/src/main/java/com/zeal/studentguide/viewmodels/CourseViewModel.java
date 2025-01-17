package com.zeal.studentguide.viewmodels;

import android.app.Application;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
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
import com.zeal.studentguide.repositories.FacultyRepository;

public class CourseViewModel extends AndroidViewModel {
    private static final String TAG = "CourseViewModel";
    private static final String COURSES_COLLECTION = "courses";
    private static final String FACULTY_COLLECTION = "faculty";
    private static final String USERS_COLLECTION = "users";
    private FacultyRepository facultyRepository;

    private CourseDao courseDao;
    private FacultyDao facultyDao;
    private UserDao userDao;
    private FirebaseFirestore db;
    private ExecutorService executorService;
    private Handler mainHandler;

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<List<FacultyWithUser>> facultyWithUsersLiveData = new MutableLiveData<>();
    private LiveData<List<Course>> coursesLiveData;

    public CourseViewModel(@NonNull Application application) {
        super(application);

        try {
            facultyRepository = new FacultyRepository(application);

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

            // Load initial data including faculty
            loadInitialData();
        } catch (Exception e) {
            Log.e(TAG, "Error initializing CourseViewModel: " + e.getMessage());
            setErrorOnMainThread("Failed to initialize: " + e.getMessage());
        }
    }

    private void loadInitialData() {
        setLoadingOnMainThread(true);
        // Load faculty data immediately
        loadFacultyWithUsers();
        syncWithFirebase();
    }

    private void loadFacultyWithUsers() {
        executorService.execute(() -> {
            try {
                // First check Firestore for faculty data
                db.collection(USERS_COLLECTION)
                        .whereEqualTo("role", "FACULTY")
                        .whereEqualTo("active", true)
                        .get()
                        .addOnSuccessListener(userSnapshot -> {
                            List<FacultyWithUser> facultyWithUsers = new ArrayList<>();
                            int totalDocuments = userSnapshot.size();
                            int[] processedCount = {0};

                            if (totalDocuments == 0) {
                                mainHandler.post(() -> facultyWithUsersLiveData.setValue(new ArrayList<>()));
                                return;
                            }

                            for (DocumentSnapshot doc : userSnapshot.getDocuments()) {
                                User user = doc.toObject(User.class);
                                if (user != null) {
                                    // Get corresponding faculty data
                                    db.collection(FACULTY_COLLECTION)
                                            .document(user.getUserId())
                                            .get()
                                            .addOnSuccessListener(facultyDoc -> {
                                                Faculty faculty = facultyDoc.toObject(Faculty.class);
                                                if (faculty != null && user.isActive()) {
                                                    facultyWithUsers.add(new FacultyWithUser(faculty, user));
                                                }

                                                processedCount[0]++;
                                                if (processedCount[0] == totalDocuments) {
                                                    // Sort faculty list by name
                                                    facultyWithUsers.sort((f1, f2) ->
                                                            f1.getName().compareTo(f2.getName()));

                                                    // Update LiveData on main thread
                                                    mainHandler.post(() ->
                                                            facultyWithUsersLiveData.setValue(facultyWithUsers));
                                                }
                                            });
                                } else {
                                    processedCount[0]++;
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error loading faculty data: " + e.getMessage());
                            // Fallback to local database
                            loadFacultyFromLocalDb();
                        });
            } catch (Exception e) {
                Log.e(TAG, "Error in loadFacultyWithUsers: " + e.getMessage());
                loadFacultyFromLocalDb();
            }
        });
    }

    private void loadFacultyFromLocalDb() {
        executorService.execute(() -> {
            try {
                List<Faculty> faculties = facultyDao.getAllFacultySync();
                List<FacultyWithUser> facultyWithUsers = new ArrayList<>();

                for (Faculty faculty : faculties) {
                    User user = userDao.getUserById(faculty.getFacultyId());
                    if (user != null && user.isActive()) {
                        facultyWithUsers.add(new FacultyWithUser(faculty, user));
                    }
                }

                facultyWithUsers.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
                mainHandler.post(() -> facultyWithUsersLiveData.setValue(facultyWithUsers));
            } catch (Exception e) {
                Log.e(TAG, "Error loading faculty from local DB: " + e.getMessage());
                mainHandler.post(() -> facultyWithUsersLiveData.setValue(new ArrayList<>()));
            }
        });
    }

    private void setLoadingOnMainThread(boolean loading) {
        mainHandler.post(() -> isLoading.setValue(loading));
    }

    private void setErrorOnMainThread(String error) {
        mainHandler.post(() -> errorMessage.setValue(error));
    }

    public void syncWithFirebase() {
        setLoadingOnMainThread(true);
        clearErrorMessage();

        // First sync faculty and users
        syncFacultyWithFirebase()
                .addOnSuccessListener(aVoid -> {
                    // Only proceed with course sync after faculty sync succeeds
                    db.collection(COURSES_COLLECTION)
                            .whereEqualTo("is_active", true)  // Only get active courses
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                executorService.execute(() -> {
                                    try {
                                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                            Course course = doc.toObject(Course.class);
                                            if (course != null) {
                                                // Verify faculty exists before inserting course
                                                Faculty faculty = facultyDao.getFacultyById(course.getFacultyId());
                                                if (faculty != null) {
                                                    courseDao.insertCourse(course);
                                                } else {
                                                    Log.w(TAG, "Skipping course " + course.getCourseId() +
                                                            " due to missing faculty: " + course.getFacultyId());
                                                }
                                            }
                                        }
                                        setLoadingOnMainThread(false);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error processing courses: " + e.getMessage());
                                        setErrorOnMainThread("Error processing courses: " + e.getMessage());
                                        setLoadingOnMainThread(false);
                                    }
                                });
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error fetching courses: " + e.getMessage());
                                setErrorOnMainThread("Error fetching courses: " + e.getMessage());
                                setLoadingOnMainThread(false);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error syncing faculty: " + e.getMessage());
                    setErrorOnMainThread("Error syncing faculty: " + e.getMessage());
                    setLoadingOnMainThread(false);
                });
    }

    private void clearErrorMessage() {
        mainHandler.post(() -> errorMessage.setValue(null));
    }

    private Task<Void> syncFacultyWithFirebase() {
        TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();

        db.collection(USERS_COLLECTION)
                .whereEqualTo("role", "FACULTY")
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    executorService.execute(() -> {
                        try {
                            for (DocumentSnapshot doc : userSnapshot.getDocuments()) {
                                User user = doc.toObject(User.class);
                                if (user != null) {
                                    try {
                                        // Insert/update user with conflict resolution
                                        userDao.insert(user);

                                        // Get and insert faculty data
                                        Task<DocumentSnapshot> facultyTask = db.collection(FACULTY_COLLECTION)
                                                .document(user.getUserId())
                                                .get();

                                        // Wait for faculty data
                                        while (!facultyTask.isComplete()) {
                                            Thread.sleep(100);
                                        }

                                        if (facultyTask.isSuccessful() && facultyTask.getResult() != null) {
                                            Faculty faculty = facultyTask.getResult().toObject(Faculty.class);
                                            if (faculty != null) {
                                                // Insert with conflict resolution strategy
                                                facultyDao.insert(faculty);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error processing faculty member: " + e.getMessage());
                                        // Continue with next faculty member instead of failing entire sync
                                        continue;
                                    }
                                }
                            }
                            tcs.setResult(null);
                        } catch (Exception e) {
                            tcs.setException(e);
                        }
                    });
                })
                .addOnFailureListener(tcs::setException);

        return tcs.getTask();
    }
    public void insertCourse(Course course) {
        isLoading.setValue(true);
        clearErrorMessage();

        // Verify faculty exists before inserting
        executorService.execute(() -> {
            try {
                Faculty faculty = facultyDao.getFacultyById(course.getFacultyId());
                if (faculty == null) {
                    setErrorOnMainThread("Faculty not found. Please try again.");
                    setLoadingOnMainThread(false);
                    return;
                }

                // Proceed with course insertion
                course.setActive(true); // Ensure course is active
                insertCourseToFirebaseAndLocal(course);
            } catch (Exception e) {
                setErrorOnMainThread("Error checking faculty: " + e.getMessage());
                setLoadingOnMainThread(false);
            }
        });
    }

    private void insertCourseToFirebaseAndLocal(Course course) {
        db.collection(COURSES_COLLECTION)
                .document(course.getCourseId())
                .set(course)
                .addOnSuccessListener(aVoid -> {
                    executorService.execute(() -> {
                        try {
                            courseDao.insertCourse(course);
                            mainHandler.post(() -> isLoading.setValue(false));
                        } catch (Exception e) {
                            mainHandler.post(() -> {
                                errorMessage.setValue("Failed to save course locally: " + e.getMessage());
                                isLoading.setValue(false);
                            });
                        }
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
        db.collection(COURSES_COLLECTION)
                .document(course.getCourseId())
                .delete()  // Actually delete from Firestore
                .addOnSuccessListener(aVoid -> {
                    executorService.execute(() -> {
                        courseDao.deleteCourse(course);  // Actually delete from local DB
                        isLoading.postValue(false);
                    });
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to delete course: " + e.getMessage());
                    isLoading.setValue(false);
                });
    }

    public LiveData<List<FacultyWithUser>> getAllFacultyWithUsers() {
        if (facultyWithUsersLiveData.getValue() == null || facultyWithUsersLiveData.getValue().isEmpty()) {
            loadFacultyWithUsers();
        }
        return facultyWithUsersLiveData;
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

    public LiveData<List<Course>> getCoursesByFaculty(String facultyId) {
        MutableLiveData<List<Course>> facultyCoursesLiveData = new MutableLiveData<>();

        if (facultyId == null || facultyId.isEmpty()) {
            setErrorOnMainThread("Invalid faculty ID");
            facultyCoursesLiveData.postValue(new ArrayList<>());
            return facultyCoursesLiveData;
        }

        setLoadingOnMainThread(true);
        executorService.execute(() -> {
            try {
                // First try to get from local database
                List<Course> localCourses = courseDao.getCoursesByFacultySync(facultyId);
                // Post initial data from local DB
                facultyCoursesLiveData.postValue(localCourses);

                // Then fetch from Firebase to ensure data is up to date
                db.collection(COURSES_COLLECTION)
                        .whereEqualTo("faculty_id", facultyId)
                        .whereEqualTo("is_active", true)
                        .get()
                        .addOnSuccessListener(querySnapshot -> {
                            List<Course> courses = new ArrayList<>();
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                try {
                                    Course course = doc.toObject(Course.class);
                                    if (course != null) {
                                        courses.add(course);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error converting document to Course: " + e.getMessage());
                                }
                            }

                            // Update local database
                            executorService.execute(() -> {
                                try {
                                    for (Course course : courses) {
                                        courseDao.insertCourse(course);
                                    }
                                    // Post updated data
                                    facultyCoursesLiveData.postValue(courses);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error updating local database: " + e.getMessage());
                                }
                                setLoadingOnMainThread(false);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error getting faculty courses: " + e.getMessage());
                            setErrorOnMainThread("Failed to load courses: " + e.getMessage());
                            setLoadingOnMainThread(false);
                        });

            } catch (Exception e) {
                Log.e(TAG, "Error in getCoursesByFaculty: " + e.getMessage());
                setErrorOnMainThread("Error loading courses: " + e.getMessage());
                setLoadingOnMainThread(false);
                facultyCoursesLiveData.postValue(new ArrayList<>());
            }
        });

        return facultyCoursesLiveData;
    }
}