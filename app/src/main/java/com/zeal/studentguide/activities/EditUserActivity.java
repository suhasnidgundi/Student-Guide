package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.databinding.ActivityEditUserBinding;
import com.zeal.studentguide.models.User;

public class EditUserActivity extends AppCompatActivity {
    private ActivityEditUserBinding binding;
    private FirebaseFirestore db;
    private String userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userId = getIntent().getStringExtra("userId");
        if (userId == null) {
            finish();
            return;
        }

        init();
        setupToolbar();
        loadUserDetails();
        setupListeners();
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarEditUser);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Edit User");
        }
    }

    private void loadUserDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    binding.progressBar.setVisibility(View.GONE);
                    currentUser = document.toObject(User.class);
                    if (currentUser != null) {
                        displayUserDetails();
                    } else {
                        showToast("User not found");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Failed to load user details");
                    finish();
                });
    }

    private void displayUserDetails() {
        binding.editTextName.setText(currentUser.getName());
        binding.editTextPhone.setText(currentUser.getPhoneNumber());
        binding.switchActive.setChecked(currentUser.isActive());
    }

    private void setupListeners() {
        binding.buttonSave.setOnClickListener(v -> validateAndUpdateUser());
        binding.buttonResetPassword.setOnClickListener(v -> resetUserPassword());
    }

    private void resetUserPassword() {
    if (currentUser == null || currentUser.getEmail() == null) {
        showToast("User email not available");
        return;
    }

    binding.progressBar.setVisibility(View.VISIBLE);
    FirebaseAuth.getInstance().sendPasswordResetEmail(currentUser.getEmail())
        .addOnSuccessListener(unused -> {
            binding.progressBar.setVisibility(View.GONE);
            showToast("Password reset email sent");
        })
        .addOnFailureListener(e -> {
            binding.progressBar.setVisibility(View.GONE);
            showToast("Failed to send reset email");
        });
}

    private void validateAndUpdateUser() {
        String name = binding.editTextName.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();
        boolean isActive = binding.switchActive.isChecked();

        if (name.isEmpty()) {
            showToast("Please fill all required fields");
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        currentUser.setName(name);
        currentUser.setPhoneNumber(phone);
        currentUser.setActive(isActive);

        db.collection("users")
                .document(userId)
                .set(currentUser)
                .addOnSuccessListener(aVoid -> {
                    showToast("User updated successfully");
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Failed to update user");
                });
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