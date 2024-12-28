package com.zeal.studentguide.utils;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.zeal.studentguide.models.Student;
import com.zeal.studentguide.models.Faculty;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.models.UserRole;

import java.util.Objects;

public class FirebaseManager {
    private static FirebaseManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private FirebaseManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public void registerUser(String name, String email, String password, UserRole role, FirebaseCallback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // Create base user record first
                    User user = new User(uid, email, name, role);
                    user.setActive(true);

                    // Save the base user record
                    db.collection("users")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                // After user is saved, create role-specific record
                                switch (role) {
                                    case STUDENT:
                                        createStudentRecord(user, callback);
                                        break;
                                    case FACULTY:
                                        createFacultyRecord(user, callback);
                                        break;
                                    default:
                                        // For other roles, just return the user object
                                        callback.onSuccess(user);
                                        break;
                                }
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    private void createStudentRecord(User user, FirebaseCallback<User> callback) {
    Student student = new Student(user.getUserId());
    student.setEmail(user.getEmail());
    student.setFullName(user.getName());

    db.collection("students")
            .document(user.getUserId())
            .set(student)
            .addOnSuccessListener(aVoid -> callback.onSuccess(user))
            .addOnFailureListener(callback::onError);
    }

    private void createFacultyRecord(User user, FirebaseCallback<User> callback) {
        Faculty faculty = new Faculty(user.getUserId(), "", "");  // Department and designation can be updated later

        db.collection("faculty")
                .document(user.getUserId())
                .set(faculty)
                .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                .addOnFailureListener(callback::onError);
    }

    public void loginUser(String email, String password, FirebaseCallback<User> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = Objects.requireNonNull(authResult.getUser()).getUid();
                    db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(document -> {
                                User user = document.toObject(User.class);
                                if (user != null) {
                                    callback.onSuccess(user);
                                } else {
                                    callback.onError(new Exception("User data not found"));
                                }
                            })
                            .addOnFailureListener(callback::onError);

                })
                .addOnFailureListener(callback::onError);
    }

    public void logout(Context context, FirebaseCallback<Void> callback) {
        // Clear local preferences first
        PreferenceManager preferenceManager = new PreferenceManager(context);
        preferenceManager.clear();

        // Sign out from Firebase Auth
        try {
            auth.signOut();
            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onError(e);
        }
    }

    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}