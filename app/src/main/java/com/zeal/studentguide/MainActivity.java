package com.zeal.studentguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.activities.AdminDashboardActivity;
import com.zeal.studentguide.activities.FacultyDashboardActivity;
import com.zeal.studentguide.activities.LoginActivity;
import com.zeal.studentguide.activities.StudentDashboardActivity;
import com.zeal.studentguide.activities.EditProfileActivity;
import com.zeal.studentguide.activities.FacultyProfileActivity;
import com.zeal.studentguide.activities.EditAdminProfileActivity;
import com.zeal.studentguide.databinding.*;
import com.zeal.studentguide.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainActivityBinding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this);
        } catch (Exception e) {
            Log.e("Application", "Error initializing Google Play Services: " + e.getMessage());
        }
        FirebaseApp.initializeApp(this);
        mainActivityBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainActivityBinding.getRoot());

        preferenceManager = new PreferenceManager(this);
        db = FirebaseFirestore.getInstance();

        checkLoginAndProfileStatus();
    }

    private void checkLoginAndProfileStatus() {
        if (!preferenceManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Check profile completion status from Firestore
        String userId = preferenceManager.getUserId();
        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Boolean isProfileComplete = documentSnapshot.getBoolean("isProfileComplete");
                        if (isProfileComplete != null && isProfileComplete) {
                            // Profile is complete, proceed to dashboard
                            navigateToDashboard();
                        } else {
                            // Profile is incomplete, redirect to appropriate edit profile activity
                            redirectToProfileCompletion();
                        }
                    } else {
                        // Document doesn't exist, handle error
                        Log.e("MainActivity", "User document not found");
                        redirectToLogin();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MainActivity", "Error checking profile status", e);
                    redirectToLogin();
                });
    }

    private void redirectToProfileCompletion() {
        Intent profileIntent;
        String userRole = preferenceManager.getUserRole();

        switch (userRole) {
            case "STUDENT":
                profileIntent = new Intent(this, EditProfileActivity.class);
                break;
            case "FACULTY":
                profileIntent = new Intent(this, FacultyProfileActivity.class);
                break;
            case "ADMIN":
                profileIntent = new Intent(this, EditAdminProfileActivity.class);
                break;
            default:
                redirectToLogin();
                return;
        }

        // Add flags to prevent going back
        profileIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(profileIntent);
        finish();
    }

    private void navigateToDashboard() {
        Intent dashboardIntent;
        String userRole = preferenceManager.getUserRole();

        switch (userRole) {
            case "ADMIN":
                dashboardIntent = new Intent(this, AdminDashboardActivity.class);
                break;
            case "FACULTY":
                dashboardIntent = new Intent(this, FacultyDashboardActivity.class);
                break;
            case "STUDENT":
                dashboardIntent = new Intent(this, StudentDashboardActivity.class);
                break;
            default:
                redirectToLogin();
                return;
        }

        startActivity(dashboardIntent);
        finish();
    }

    private void redirectToLogin() {
        preferenceManager.clear();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}