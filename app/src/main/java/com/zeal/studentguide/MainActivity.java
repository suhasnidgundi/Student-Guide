package com.zeal.studentguide;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.zeal.studentguide.databinding.*;
import com.zeal.studentguide.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainActivityBinding;
    private ActivityLoginBinding loginActivityBinding;

    private ActivityStudentDashboardBinding studentDashboardActivityBinding;
    private ActivityFacultyDashboardBinding facultyDashboardActivityBinding;
    private ActivityAdminDashboardBinding adminDashboardActivityBinding;

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
            loginActivityBinding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(loginActivityBinding.getRoot());
        }

        switch (preferenceManager.getUserRole()) {
            case "admin":
                adminDashboardActivityBinding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
                setContentView(adminDashboardActivityBinding.getRoot());
                break;
            case "faculty":
                facultyDashboardActivityBinding = ActivityFacultyDashboardBinding.inflate(getLayoutInflater());
                setContentView(facultyDashboardActivityBinding.getRoot());
                break;
            default: // student
                studentDashboardActivityBinding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
                setContentView(studentDashboardActivityBinding.getRoot());
                break;
        }
    }
}