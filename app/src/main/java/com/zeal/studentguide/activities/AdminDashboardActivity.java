package com.zeal.studentguide.activities;

import android.content.Intent;import android.os.Bundle;import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.databinding.ActivityAdminDashboardBinding;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupClickListeners();
    }

    private void setupClickListeners() {
            binding.cardUserManagement.setOnClickListener(v -> startActivity(new Intent(this, UserManagementActivity.class)));
            binding.cardCourseManagement.setOnClickListener(v -> startActivity(new Intent(this, CourseManagementActivity.class)));
            binding.cardAnnouncements.setOnClickListener(v -> Toast.makeText(this, "Announcements Clicked", Toast.LENGTH_SHORT).show());
            binding.cardReports.setOnClickListener(v -> Toast.makeText(this, "Reports Clicked", Toast.LENGTH_SHORT).show());
    }
}