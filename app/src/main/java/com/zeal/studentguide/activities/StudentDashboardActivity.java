package com.zeal.studentguide.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.databinding.ActivityStudentDashboardBinding;
import com.zeal.studentguide.utils.PreferenceManager;

public class StudentDashboardActivity extends AppCompatActivity {
    private ActivityStudentDashboardBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        setupProfile();
        setupClickListeners();
    }

    private void setupProfile() {
        String studentName = preferenceManager.getUserId();
        binding.textStudentName.setText(studentName);
    }

    private void setupClickListeners() {
//        binding.cardVirtualTour.setOnClickListener(v -> startActivity(new Intent(this, VirtualTourActivity.class)));
//        binding.cardAcademics.setOnClickListener(v -> startActivity(new Intent(this, AcademicsActivity.class)));
//        binding.cardFacilities.setOnClickListener(v -> startActivity(new Intent(this, FacilitiesActivity.class)));
//        binding.cardAdmission.setOnClickListener(v -> startActivity(new Intent(this, AdmissionActivity.class)));
    }
}