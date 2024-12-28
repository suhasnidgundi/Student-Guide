package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.databinding.ActivityUserDetailBinding;
import com.zeal.studentguide.models.Faculty;
import com.zeal.studentguide.models.Student;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.models.UserRole;

public class UserDetailActivity extends AppCompatActivity {
    private ActivityUserDetailBinding binding;
    private FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            finish();
            return;
        }

        init();
        setupToolbar();
        loadUserDetails();
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarUserDetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Details");
        }
    }

    private void loadUserDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    User user = document.toObject(User.class);
                    if (user != null) {
                        displayUserDetails(user);
                        loadRoleSpecificDetails(user);
                    } else {
                        showToast("User not found");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Failed to load user details");
                });
    }

    private void displayUserDetails(User user) {
        binding.textName.setText(user.getName());
        binding.textEmail.setText(user.getEmail());
        binding.textRole.setText(user.getRole().toString());
        binding.textPhone.setText(user.getPhoneNumber());
        binding.textStatus.setText(user.isActive() ? "Active" : "Inactive");
        binding.textLastLogin.setText(user.getLastLoginDate());
        binding.textRegistrationDate.setText(user.getRegistrationDate());
    }

    private void loadRoleSpecificDetails(User user) {
        if (user.getRole() == UserRole.STUDENT) {
            loadStudentDetails();
        } else if (user.getRole() == UserRole.FACULTY) {
            loadFacultyDetails();
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void loadStudentDetails() {
        db.collection("students")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Student student = document.toObject(Student.class);
                    if (student != null) {
                        binding.layoutStudentDetails.setVisibility(View.VISIBLE);
                        binding.textRollNumber.setText(student.getRollNumber());
                        binding.textBranch.setText(student.getBranch());
                        binding.textSemester.setText(student.getSemester());
                        binding.textBatch.setText(student.getBatch());
                        binding.textCgpa.setText(String.valueOf(student.getCgpa()));
                    }
                });
    }

    private void loadFacultyDetails() {
        db.collection("faculty")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Faculty faculty = document.toObject(Faculty.class);
                    if (faculty != null) {
                        binding.layoutFacultyDetails.setVisibility(View.VISIBLE);
                        binding.textDepartment.setText(faculty.getDepartment());
                        binding.textDesignation.setText(faculty.getDesignation());
                        binding.textSpecialization.setText(faculty.getSpecialization());
                        binding.textExperience.setText(faculty.getExperienceYears() + " years");
                        binding.textQualifications.setText(faculty.getQualifications());
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}