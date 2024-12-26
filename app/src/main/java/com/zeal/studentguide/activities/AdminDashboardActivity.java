package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.databinding.ActivityAdminDashboardBinding;
import com.zeal.studentguide.utils.PreferenceManager;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        setupClickListeners();
    }

    private void setupClickListeners() {
    //        binding.cardUserManagement.setOnClickListener(v ->
    //                startActivity(new Intent(this, UserManagementActivity.class)));
    //        binding.cardCourseManagement.setOnClickListener(v ->
    //                startActivity(new Intent(this, CourseManagementActivity.class)));
    //        binding.cardAnnouncements.setOnClickListener(v ->
    //                startActivity(new Intent(this, AnnouncementsActivity.class)));
    //        binding.cardReports.setOnClickListener(v ->
    //                startActivity(new Intent(this, ReportsActivity.class)));
    }
}