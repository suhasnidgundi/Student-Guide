package com.zeal.studentguide.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.database.AppDatabase;
import com.zeal.studentguide.models.Faculty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FacultyViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final FirebaseFirestore firestore;
    private final ExecutorService executorService;

    public FacultyViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        firestore = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<Faculty> getFacultyData(String facultyId) {
        MutableLiveData<Faculty> facultyData = new MutableLiveData<>();

        // First try to get from local database
        executorService.execute(() -> {
            Faculty localFaculty = database.facultyDao().getFacultyById(facultyId);
            if (localFaculty != null) {
                facultyData.postValue(localFaculty);
            }

            // Also fetch from Firestore to ensure data is up to date
            firestore.collection("faculty")
                    .document(facultyId)
                    .get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            Faculty faculty = document.toObject(Faculty.class);
                            if (faculty != null) {
                                // Update local database
                                executorService.execute(() -> {
                                    database.facultyDao().update(faculty);
                                    facultyData.postValue(faculty);
                                });
                            }
                        }
                    });
        });

        return facultyData;
    }

    public LiveData<Boolean> updateFacultyProfile(Faculty faculty) {
        MutableLiveData<Boolean> updateResult = new MutableLiveData<>();

        // Update in Firestore
        firestore.collection("faculty")
                .document(faculty.getFacultyId())
                .set(faculty)
                .addOnSuccessListener(aVoid -> {
                    // Update local database
                    executorService.execute(() -> {
                        database.facultyDao().update(faculty);
                        updateResult.postValue(true);
                    });
                })
                .addOnFailureListener(e -> updateResult.postValue(false));

        return updateResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}