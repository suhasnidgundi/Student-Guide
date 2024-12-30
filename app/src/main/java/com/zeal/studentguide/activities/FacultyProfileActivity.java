package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.MainActivity;
import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityFacultyProfileBinding;
import com.zeal.studentguide.models.Departments;
import com.zeal.studentguide.models.Faculty;
import com.zeal.studentguide.utils.PreferenceManager;
import com.zeal.studentguide.viewmodels.FacultyViewModel;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FacultyProfileActivity extends AppCompatActivity {
    private ActivityFacultyProfileBinding binding;
    private FacultyViewModel viewModel;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(FacultyViewModel.class);
        preferenceManager = new PreferenceManager(this);
        firebaseAuth = FirebaseAuth.getInstance();

        setupToolbar();
        setupDepartmentDropdown();
        setupClickListeners();
        loadFacultyData();
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbarEditProfile.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupDepartmentDropdown() {
        String[] departments = Arrays.stream(Departments.values())
                .map(Departments::getDepartmentName)
                .collect(Collectors.toList())
                .toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                departments
        );
        binding.editDepartment.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnSaveChanges.setOnClickListener(v -> saveChanges());
        binding.btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void loadFacultyData() {
        String facultyId = preferenceManager.getUserId();
        if (facultyId != null && !facultyId.isEmpty()) {
            viewModel.getFacultyData(facultyId).observe(this, faculty -> {
                if (faculty != null) {
                    // Use the name from PreferenceManager
                    binding.editName.setText(preferenceManager.getUsername());
                    binding.editDepartment.setText(faculty.getDepartment(), false);
                    binding.editDesignation.setText(faculty.getDesignation());
                    binding.editSpecialization.setText(faculty.getSpecialization());
                    binding.editExperience.setText(String.valueOf(faculty.getExperienceYears()));
                    binding.editQualifications.setText(faculty.getQualifications());
                }
            });
        }
    }

    private boolean validateInput() {
        if (binding.editName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.editDepartment.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please select your department", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.editDesignation.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your designation", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.editExperience.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your years of experience", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveChanges() {
        if (!validateInput()) {
            return;
        }

        String facultyId = preferenceManager.getUserId();
        if (facultyId == null || facultyId.isEmpty()) return;

        Faculty updatedFaculty = new Faculty(facultyId,
                binding.editDepartment.getText().toString(),
                binding.editDesignation.getText().toString());

        updatedFaculty.setSpecialization(binding.editSpecialization.getText().toString());
        updatedFaculty.setQualifications(binding.editQualifications.getText().toString());

        try {
            updatedFaculty.setExperienceYears(
                    Integer.parseInt(binding.editExperience.getText().toString()));
        } catch (NumberFormatException e) {
            binding.experienceLayout.setError("Please enter a valid number");
            return;
        }

        viewModel.updateFacultyProfile(updatedFaculty).observe(this, success -> {
            if (success) {
                // Update profile completion status
                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(facultyId)
                        .update("isProfileComplete", true)
                        .addOnSuccessListener(unused -> {
                            preferenceManager.setUserProfileComplete(true);
                            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to update profile status", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetPassword() {
        String email = preferenceManager.getUserEmail();
        if (email != null && !email.isEmpty()) {
            firebaseAuth.sendPasswordResetEmail(email)
                    .addOnSuccessListener(unused ->
                            Toast.makeText(this,
                                    "Password reset email sent to " + email,
                                    Toast.LENGTH_LONG).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this,
                                    "Failed to send reset email: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show());
        }
    }

    @Override
    public void onBackPressed() {
        if (!preferenceManager.isUserProfileComplete()) {
            return; // Prevent going back if profile is incomplete
        }
        super.onBackPressed();
    }
}