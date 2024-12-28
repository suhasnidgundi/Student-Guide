package com.zeal.studentguide.activities;

import android.annotation.SuppressLint;
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

    @SuppressLint("SetTextI18n")
    private void loadFacultyDetails() {
        if (userId == null) {
            showToast("Invalid user ID");
            finish();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // First load basic faculty details
        db.collection("faculty")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    try {
                        Faculty faculty = document.toObject(Faculty.class);
                        if (faculty != null) {
                            displayFacultyDetails(faculty);
                            // After displaying basic details, load associated courses
                            loadFacultyCourses();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showToast("Faculty details not found");
                        }
                    } catch (Exception e) {
                        binding.progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                        showToast("Error : " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Error loading faculty details: " + e.getMessage());
                });
    }

    private void displayFacultyDetails(Faculty faculty) {
        try {
            binding.layoutFacultyDetails.setVisibility(View.VISIBLE);

            // Use safe getter methods with null checks
            binding.textDepartment.setText(getValueOrDefault(faculty.getDepartment()));
            binding.textDesignation.setText(getValueOrDefault(faculty.getDesignation()));
            binding.textSpecialization.setText(getValueOrDefault(faculty.getSpecialization()));

            // Handle experience years safely
            Integer experienceYears = faculty.getExperienceYears();
            binding.textExperience.setText(experienceYears != null ?
                    experienceYears + " years" : "N/A");

            binding.textQualifications.setText(getValueOrDefault(faculty.getQualifications()));
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error displaying faculty information");
        }
    }

    private void loadFacultyCourses() {
        // Query courses where facultyId matches the current userId
        db.collection("courses")
                .whereEqualTo("faculty_id", userId)
                .whereEqualTo("isActive", true)
                .get()
                .addOnSuccessListener(courseDocuments -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (!courseDocuments.isEmpty()) {
                        // Handle displaying courses if needed
                        // For example, you could add a RecyclerView to show the courses
                        int courseCount = courseDocuments.size();
                        // You might want to add a TextView to show the course count
                        // binding.textCourseCount.setText("Teaching " + courseCount + " courses");
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                });
    }

    private String getValueOrDefault(String value) {
        return value != null && !value.trim().isEmpty() ? value : "N/A";
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}