package com.zeal.studentguide.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeal.studentguide.databinding.ActivityEditAdminProfileBinding;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.utils.PreferenceManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditAdminProfileActivity extends AppCompatActivity {
    private ActivityEditAdminProfileBinding binding;
    private PreferenceManager preferenceManager;
    private Uri selectedImageUri;
    private String currentUserId;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        Glide.with(this)
                                .load(selectedImageUri)
                                .centerCrop()
                                .into(binding.imageProfile);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAdminProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialize();
        loadUserData();
        setListeners();
    }

    private void initialize() {
        preferenceManager = new PreferenceManager(this);
        currentUserId = preferenceManager.getUserId();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    private void loadUserData() {
        showLoading(true);
        db.collection("users")
                .document(currentUserId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        binding.editTextName.setText(user.getName());
                        binding.editTextEmail.setText(user.getEmail());
                        binding.editTextPhone.setText(user.getPhoneNumber());

                        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
                            Glide.with(this)
                                    .load(user.getProfileImageUrl())
                                    .centerCrop()
                                    .into(binding.imageProfile);
                        }
                    }
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showToast("Failed to load profile data");
                });
    }

    private void setListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.imageProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });

        binding.buttonSave.setOnClickListener(v -> validateAndUpdateProfile());
    }

    private void validateAndUpdateProfile() {
        String name = binding.editTextName.getText().toString().trim();
        String phone = binding.editTextPhone.getText().toString().trim();

        if (name.isEmpty()) {
            showToast("Please enter your name");
            return;
        }

        showLoading(true);

        if (selectedImageUri != null) {
            uploadImageAndUpdateProfile(name, phone);
        } else {
            updateProfile(name, phone, null);
        }
    }

    private void uploadImageAndUpdateProfile(String name, String phone) {
        String imageFileName = "profile_images/" + UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageFileName);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                        .addOnSuccessListener(uri ->
                                updateProfile(name, phone, uri.toString()))
                        .addOnFailureListener(e -> {
                            showLoading(false);
                            showToast("Failed to upload image");
                        }))
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showToast("Failed to upload image");
                });
    }

    private void updateProfile(String name, String phone, String imageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phoneNumber", phone);
        if (imageUrl != null) {
            updates.put("profileImageUrl", imageUrl);
        }

        db.collection("users")
                .document(currentUserId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    showLoading(false);
                    preferenceManager.setUsername(name);
                    showToast("Profile updated successfully");
                    finish();
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showToast("Failed to update profile");
                });
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.buttonSave.setEnabled(!isLoading);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}