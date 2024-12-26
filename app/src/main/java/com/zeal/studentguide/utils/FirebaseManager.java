package com.zeal.studentguide.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.zeal.studentguide.models.User;

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

    public void registerUser(String name, String email, String password, String role, FirebaseCallback<User> callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    User user = new User();
                    user.setUserId(authResult.getUser().getUid());
                    user.setEmail(email);
                    user.setRole(role);
                    user.setName(name);

                    // Save user to Firestore
                    db.collection("users")
                            .document(user.getUserId())
                            .set(user)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(user))
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    public void loginUser(String email, String password, FirebaseCallback<User> callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String userId = authResult.getUser().getUid();
                    // Get user data from Firestore
                    db.collection("users")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(document -> {
                                User user = document.toObject(User.class);
                                callback.onSuccess(user);
                            })
                            .addOnFailureListener(callback::onError);
                })
                .addOnFailureListener(callback::onError);
    }

    public interface FirebaseCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }
}