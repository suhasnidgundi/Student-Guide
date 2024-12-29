package com.zeal.studentguide.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.models.Faculty;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.models.FacultyWithUser;
import java.util.ArrayList;
import java.util.List;

public class FacultiesViewModel extends AndroidViewModel {
    private final FirebaseFirestore firestore;

    public FacultiesViewModel(Application application) {
        super(application);
        firestore = FirebaseFirestore.getInstance();
    }

    public LiveData<List<FacultyWithUser>> getFacultiesByDepartment(String department) {
        MutableLiveData<List<FacultyWithUser>> facultiesLiveData = new MutableLiveData<>();
        List<FacultyWithUser> facultyList = new ArrayList<>();

        firestore.collection("faculty")
                .whereEqualTo("department", department)
                .get()
                .addOnSuccessListener(facultySnapshots -> {
                    int totalFaculty = facultySnapshots.size();
                    int[] loadedCount = {0};  // Using array to modify in lambda

                    if (totalFaculty == 0) {
                        facultiesLiveData.setValue(facultyList);
                        return;
                    }

                    for (Faculty faculty : facultySnapshots.toObjects(Faculty.class)) {
                        // For each faculty, get the corresponding user data
                        firestore.collection("users")
                                .document(faculty.getFacultyId())
                                .get()
                                .addOnSuccessListener(userSnapshot -> {
                                    User user = userSnapshot.toObject(User.class);
                                    if (user != null) {
                                        FacultyWithUser facultyWithUser = new FacultyWithUser(faculty, user);
                                        facultyList.add(facultyWithUser);
                                    }

                                    loadedCount[0]++;
                                    // Only update LiveData when all faculty members are loaded
                                    if (loadedCount[0] == totalFaculty) {
                                        facultiesLiveData.setValue(facultyList);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    loadedCount[0]++;
                                    if (loadedCount[0] == totalFaculty) {
                                        facultiesLiveData.setValue(facultyList);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> facultiesLiveData.setValue(null));

        return facultiesLiveData;
    }

    public LiveData<FacultyWithUser> getFacultyWithUserData(String facultyId) {
        MutableLiveData<FacultyWithUser> facultyData = new MutableLiveData<>();

        firestore.collection("faculty")
                .document(facultyId)
                .get()
                .addOnSuccessListener(facultyDoc -> {
                    Faculty faculty = facultyDoc.toObject(Faculty.class);
                    if (faculty != null) {
                        firestore.collection("users")
                                .document(facultyId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    User user = userDoc.toObject(User.class);
                                    if (user != null) {
                                        FacultyWithUser facultyWithUser = new FacultyWithUser(faculty, user);
                                        facultyData.setValue(facultyWithUser);
                                    }
                                });
                    }
                });

        return facultyData;
    }
}