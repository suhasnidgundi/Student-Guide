package com.zeal.studentguide;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
            case "admin":
                dashboardIntent = new Intent(this, AdminDashboardActivity.class);
                break;
            case "faculty":
                dashboardIntent = new Intent(this, FacultyDashboardActivity.class);
                break;
            case "student":
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