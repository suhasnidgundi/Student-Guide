package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.MainActivity;
import com.zeal.studentguide.databinding.ActivityLoginBinding;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.models.UserRole;
import com.zeal.studentguide.utils.FirebaseManager;
import com.zeal.studentguide.utils.PreferenceManager;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseManager firebaseManager;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = new PreferenceManager(this);
        db = FirebaseFirestore.getInstance();

        setupListeners();
    }

    private void setupListeners() {
        binding.buttonLogin.setOnClickListener(v -> handleLogin());
        binding.textRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void handleLogin() {
        if (validateInputs()) {
            showLoading();
            String email = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();

            firebaseManager.loginUser(email, password, new FirebaseManager.FirebaseCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    // Save basic user information
                    preferenceManager.setLoggedIn(true);
                    preferenceManager.setUserId(user.getUserId());
                    preferenceManager.setUsername(user.getName());
                    preferenceManager.setUserEmail(user.getEmail());
                    preferenceManager.setUserRole(user.getRole());

                    // Fetch additional role-specific information if needed
                    if (user.getRole() == UserRole.STUDENT) {
                        fetchStudentDepartment(user.getUserId());
                    } else if (user.getRole() == UserRole.FACULTY) {
                        fetchFacultyDepartment(user.getUserId());
                    } else {
                        // For admin or other roles, proceed directly to main activity
                        proceedToMainActivity();
                    }
                }

                @Override
                public void onError(Exception e) {
                    hideLoading();
                    Toast.makeText(LoginActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void fetchStudentDepartment(String userId) {
        db.collection("students")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("branch")) {
                        String department = documentSnapshot.getString("branch");
                        preferenceManager.setUserDepartment(department);
                    }
                    proceedToMainActivity();
                })
                .addOnFailureListener(e -> {
                    Log.e("LoginActivity", "Failed to fetch student details", e);
                    Toast.makeText(LoginActivity.this,
                            "Failed to fetch student details",
                            Toast.LENGTH_SHORT).show();
                    proceedToMainActivity();
                });
    }

    private void fetchFacultyDepartment(String userId) {
        db.collection("faculty")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("department")) {
                        String department = documentSnapshot.getString("department");
                        preferenceManager.setUserDepartment(department);
                    }
                    proceedToMainActivity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this,
                            "Failed to fetch faculty details",
                            Toast.LENGTH_SHORT).show();
                    proceedToMainActivity();
                });
    }

    private void proceedToMainActivity() {
        hideLoading();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();

        if (email.isEmpty()) {
            binding.editTextEmail.setError("Email is required");
            isValid = false;
        }

        if (password.isEmpty()) {
            binding.editTextPassword.setError("Password is required");
            isValid = false;
        }

        return isValid;
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonLogin.setEnabled(false);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.buttonLogin.setEnabled(true);
    }
}