package com.zeal.studentguide.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeal.studentguide.databinding.ActivityEditProfileBinding;
import com.zeal.studentguide.models.Student;
import com.zeal.studentguide.utils.PreferenceManager;

import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private FirebaseStorage storage;
    private Uri selectedImageUri;
    private Student currentStudent;

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        binding.imageProfile.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        database = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        setupToolbar();
        loadStudentProfile();
        setupClickListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarEditProfile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        binding.toolbarEditProfile.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadStudentProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);

        database.collection("students")
                .document(preferenceManager.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        currentStudent = task.getResult().toObject(Student.class);
                        if (currentStudent != null) {
                            populateFields();
                        }
                    } else {
                        showToast("Unable to load profile");
                        finish();
                    }
                });
    }

    private void populateFields() {
        binding.inputFullName.setText(currentStudent.getFullName());
        binding.inputEmail.setText(currentStudent.getEmail());
        binding.inputRollNumber.setText(currentStudent.getRollNumber());
        binding.inputBranch.setText(currentStudent.getBranch());
        binding.inputSemester.setText(currentStudent.getSemester());
        // Load profile image if exists
        // You'll need to implement image loading logic here
    }

    private void setupClickListeners() {
        binding.buttonChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImage.launch(intent);
        });

        binding.buttonSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        if (!validateInput()) return;

        binding.progressBar.setVisibility(View.VISIBLE);

        // Update student object with new values
        currentStudent.setFullName(binding.inputFullName.getText().toString());
        currentStudent.setEmail(binding.inputEmail.getText().toString());
        currentStudent.setRollNumber(binding.inputRollNumber.getText().toString());
        currentStudent.setBranch(binding.inputBranch.getText().toString());
        currentStudent.setSemester(binding.inputSemester.getText().toString());

        // If new image is selected, upload it first
        if (selectedImageUri != null) {
            uploadImageAndSaveProfile();
        } else {
            saveProfileToFirestore();
        }
    }

    private void uploadImageAndSaveProfile() {
        String imageFileName = UUID.randomUUID().toString();
        StorageReference storageRef = storage.getReference()
                .child("profile_images")
                .child(imageFileName);

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    currentStudent.setProfileImageUrl(uri.toString());
                                    saveProfileToFirestore();
                                })
                )
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Failed to upload image");
                });
    }

    private void saveProfileToFirestore() {
        database.collection("students")
                .document(currentStudent.getStudentId())
                .set(currentStudent)
                .addOnSuccessListener(aVoid -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Profile updated successfully");
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Failed to update profile");
                });
    }

    private boolean validateInput() {
        if (binding.inputFullName.getText().toString().trim().isEmpty()) {
            showToast("Please enter your full name");
            return false;
        }
        // Add more validation as needed
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}