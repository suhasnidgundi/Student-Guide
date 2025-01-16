package com.zeal.studentguide.repositories;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.database.AppDatabase;
import com.zeal.studentguide.database.dao.FacultyDao;
import com.zeal.studentguide.database.dao.UserDao;
import com.zeal.studentguide.models.Faculty;
import com.zeal.studentguide.models.FacultyWithUser;
import com.zeal.studentguide.models.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FacultyRepository {
    private final FacultyDao facultyDao;
    private final UserDao userDao;
    private final ExecutorService executorService;
    private final FirebaseFirestore firestore;

    public FacultyRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        facultyDao = db.facultyDao();
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<List<FacultyWithUser>> getAllFacultyWithUsers() {
        return facultyDao.getAllFacultyWithUsers();
    }

    public void insertFacultyWithUser(Faculty faculty, User user) {
        executorService.execute(() -> {
            userDao.insert(user);
            facultyDao.insert(faculty);
        });
    }

    public void updateFacultyWithUser(Faculty faculty, User user) {
        executorService.execute(() -> {
            userDao.update(user);
            facultyDao.update(faculty);
        });
    }

    public void deleteFacultyWithUser(Faculty faculty, User user) {
        executorService.execute(() -> {
            userDao.delete(user);
            facultyDao.delete(faculty);
        });
    }
}