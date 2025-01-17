package com.zeal.studentguide.activities;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.AdminVirtualTourAdapter;
import com.zeal.studentguide.databinding.ActivityVirtualTourManagementBinding;
import com.zeal.studentguide.models.VirtualTourLocation;
import java.util.ArrayList;
import java.util.UUID;

public class VirtualTourManagementActivity extends AppCompatActivity {
    private ActivityVirtualTourManagementBinding binding;
    private AdminVirtualTourAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private static final int PICK_IMAGE = 1;
    private Uri selectedImageUri;
    private Dialog addLocationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVirtualTourManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        setupRecyclerView();
        setupClickListeners();
        loadLocations();
    }

    private void setupRecyclerView() {
        adapter = new AdminVirtualTourAdapter(new ArrayList<>(), this::deleteLocation);
        binding.recyclerViewLocations.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewLocations.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.fabAddLocation.setOnClickListener(v -> showAddLocationDialog());
    }

    private void showAddLocationDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_virtual_tour);

        // Initialize dialog views and set click listeners
        dialog.findViewById(R.id.btnSelectImage).setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

        dialog.findViewById(R.id.btnSave).setOnClickListener(v -> {
            // Get input values and save location
            String title = ((EditText) dialog.findViewById(R.id.editTextTitle)).getText().toString();
            String iframeUrl = ((EditText) dialog.findViewById(R.id.editTextIframeUrl)).getText().toString();

            if (validateInput(title, iframeUrl, selectedImageUri)) {
                uploadImageAndSaveLocation(title, iframeUrl, selectedImageUri);
                dialog.dismiss();
            }
        });

        dialog.show();
        addLocationDialog = dialog;
    }

    private boolean validateInput(String title, String iframeUrl, Uri selectedImageUri) {
        if (title == null || title.trim().isEmpty()) {
            showToast("Please enter a title");
            return false;
        }

        if (iframeUrl == null || iframeUrl.trim().isEmpty()) {
            showToast("Please enter the Google Maps embed URL");
            return false;
        }

        if (!iframeUrl.startsWith("https://www.google.com/maps/embed")) {
            showToast("Please enter a valid Google Maps embed URL");
            return false;
        }

        if (selectedImageUri == null) {
            showToast("Please select a thumbnail image");
            return false;
        }

        return true;
    }

    private void uploadImageAndSaveLocation(String title, String iframeUrl, Uri imageUri) {
        String imageFileName = "virtual_tour/" + UUID.randomUUID().toString();
        StorageReference ref = storage.getReference().child(imageFileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    ref.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        VirtualTourLocation location = new VirtualTourLocation(title, downloadUri.toString(), iframeUrl);
                        saveLocationToFirestore(location);
                    });
                })
                .addOnFailureListener(e -> showToast("Failed to upload image: " + e.getMessage()));
    }

    private void saveLocationToFirestore(VirtualTourLocation location) {
        db.collection("virtual_tour_locations")
                .add(location)
                .addOnSuccessListener(documentReference -> {
                    location.setId(documentReference.getId());
                    adapter.addLocation(location);
                    showToast("Location added successfully");
                })
                .addOnFailureListener(e -> showToast("Failed to save location: " + e.getMessage()));
    }

    private void deleteLocation(VirtualTourLocation location) {
        db.collection("virtual_tour_locations")
                .document(location.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Delete thumbnail from storage
                    if (location.getThumbnailUrl() != null) {
                        StorageReference photoRef = storage.getReferenceFromUrl(location.getThumbnailUrl());
                        photoRef.delete();
                    }
                    adapter.removeLocation(location);
                    showToast("Location deleted successfully");
                })
                .addOnFailureListener(e -> showToast("Failed to delete location: " + e.getMessage()));
    }

    private void loadLocations() {
        db.collection("virtual_tour_locations")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<VirtualTourLocation> locations = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        VirtualTourLocation location = document.toObject(VirtualTourLocation.class);
                        location.setId(document.getId());
                        locations.add(location);
                    }
                    adapter.setLocations(locations);
                })
                .addOnFailureListener(e -> showToast("Failed to load locations: " + e.getMessage()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            // Update image preview in dialog if needed
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}