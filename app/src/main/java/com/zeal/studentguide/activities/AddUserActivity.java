package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.zeal.studentguide.databinding.ActivityAddUserBinding;
import com.zeal.studentguide.models.Faculty;import com.zeal.studentguide.models.Student;import com.zeal.studentguide.models.User;
import com.zeal.studentguide.models.UserRole;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

public class AddUserActivity extends AppCompatActivity {
    private ActivityAddUserBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String[] roleItems = new String[]{"Student", "Faculty"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setupToolbar();
        setupRoleDropdown();
        setupListeners();
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarAddUser);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New User");
        }
    }

    private void setupRoleDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                roleItems
        );

        AutoCompleteTextView roleDropdown = binding.spinnerRole;
        roleDropdown.setAdapter(adapter);
        roleDropdown.setText(roleItems[0], false); // Set default value to "Student"
    }

    private void setupListeners() {
        binding.buttonAddUser.setOnClickListener(v -> validateAndAddUser());
    }

    private void validateAndAddUser() {
        String name = binding.editTextName.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();
        String password = generateTemporaryPassword();
        String selectedRole = binding.spinnerRole.getText().toString();
        UserRole role = selectedRole.equalsIgnoreCase("Student") ? UserRole.STUDENT : UserRole.FACULTY;

        if (name.isEmpty() || email.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // Create Authentication account
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();

                        // Create User object
                        User user = new User(userId, email, name, role);
                        user.setPhoneNumber(phone);
                        user.setActive(true);
                        user.setRegistrationDate(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));

                        // Save to users collection
                        db.collection("users")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {
                                    // Create role-specific document
                                    if (role == UserRole.STUDENT) {
                                        createStudentDocument(userId, name, email);
                                    } else if (role == UserRole.FACULTY) {
                                        createFacultyDocument(userId, name, email);
                                    }

                                    // Send password reset email
                                    auth.sendPasswordResetEmail(email)
                                            .addOnSuccessListener(unused -> {
                                                showToast("User added successfully. Password reset email sent.");
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                showToast("User added but failed to send reset email");
                                                finish();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    binding.progressBar.setVisibility(View.GONE);
                                    showToast("Failed to add user to database");
                                });
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        showToast("Failed to create user authentication");
                    }
                });
    }

    private void createStudentDocument(String userId, String fullName, String email) {
        Student student = new Student(userId);
        student.setFullName(fullName);
        student.setEmail(email);
        student.setAttendancePercentage(0.0);
        student.setActiveBacklogCount(0);
        student.setCgpa(0.0);

        db.collection("students")
                .document(userId)
                .set(student)
                .addOnFailureListener(e ->
                        showToast("Warning: Failed to create student profile"));
    }

    private void createFacultyDocument(String userId, String name, String email) {
        Faculty faculty = new Faculty(userId, "", "");  // Department and designation can be updated later
        faculty.setQualifications("");
        faculty.setExperienceYears(0);
        faculty.setSpecialization("");

        db.collection("faculty")
                .document(userId)
                .set(faculty)
                .addOnFailureListener(e ->
                        showToast("Warning: Failed to create faculty profile"));
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}