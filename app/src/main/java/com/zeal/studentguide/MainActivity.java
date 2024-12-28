package com.zeal.studentguide;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.zeal.studentguide.activities.AdminDashboardActivity;
import com.zeal.studentguide.activities.FacultyDashboardActivity;
import com.zeal.studentguide.activities.LoginActivity;
import com.zeal.studentguide.activities.StudentDashboardActivity;
import com.zeal.studentguide.databinding.*;
import com.zeal.studentguide.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainActivityBinding;
    private PreferenceManager preferenceManager;

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

        checkUserRoleAndNavigate();
    }

    private void checkUserRoleAndNavigate() {
        if (!preferenceManager.isLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        Intent dashboardIntent;
        switch (preferenceManager.getUserRole()) {
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
                // Handle unknown role or logout
                preferenceManager.clearPreferences();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return;
        }

        startActivity(dashboardIntent);
        finish();
    }
}