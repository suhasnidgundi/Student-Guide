package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.AnnouncementAdapter;
import com.zeal.studentguide.databinding.ActivityAnnouncementsBinding;
import com.zeal.studentguide.models.Announcement;
import com.zeal.studentguide.models.Departments;
import com.zeal.studentguide.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnnouncementsActivity extends AppCompatActivity implements AnnouncementAdapter.OnAnnouncementActionListener {
    private ActivityAnnouncementsBinding binding;
    private FirebaseFirestore db;
    private AnnouncementAdapter adapter;
    private PreferenceManager preferenceManager;

    // Announcement types
    private final String[] announcementTypes = {"Event", "Important", "Information", "Deadline", "Academic", "Administrative"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnnouncementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(this);

        setupUI();
        setupRecyclerView();
        loadAnnouncements();
    }

    private void setupUI() {
        // Back button
        binding.buttonBack.setOnClickListener(v -> finish());

        // Setup announcement type dropdown
        AutoCompleteTextView typeDropdown = binding.dropdownAnnouncementType;
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, announcementTypes);
        typeDropdown.setAdapter(typeAdapter);

        // Setup department dropdown
        AutoCompleteTextView departmentDropdown = binding.dropdownDepartment;
        List<String> departments = new ArrayList<>(Arrays.asList(Departments.getAllDepartments()));
        departments.add(0, "All Departments");
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, R.layout.dropdown_item, departments);
        departmentDropdown.setAdapter(departmentAdapter);

        // Post button
        binding.buttonPost.setOnClickListener(v -> postAnnouncement());
    }

    private void setupRecyclerView() {
        adapter = new AnnouncementAdapter(this, this);
        binding.recyclerAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerAnnouncements.setAdapter(adapter);
    }

    private void loadAnnouncements() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textNoAnnouncements.setVisibility(View.GONE);

        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.progressBar.setVisibility(View.GONE);
                    List<Announcement> announcements = queryDocumentSnapshots.toObjects(Announcement.class);
                    adapter.setAnnouncements(announcements);

                    if (announcements.isEmpty()) {
                        binding.textNoAnnouncements.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.textNoAnnouncements.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Failed to load announcements: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void postAnnouncement() {
        String type = binding.dropdownAnnouncementType.getText().toString().trim();
        String department = binding.dropdownDepartment.getText().toString().trim();
        String message = binding.editTextMessage.getText().toString().trim();

        // Validate inputs
        if (type.isEmpty()) {
            Toast.makeText(this, "Please select an announcement type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (department.isEmpty()) {
            Toast.makeText(this, "Please select a department", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.isEmpty()) {
            binding.textInputMessage.setError("Message cannot be empty");
            return;
        } else {
            binding.textInputMessage.setError(null);
        }

        // Show loading
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonPost.setEnabled(false);

        // Create announcement object
        String adminId = preferenceManager.getUserId();
        Announcement announcement = new Announcement(type, message, department, adminId);

        // Save to Firestore
        db.collection("announcements")
                .add(announcement)
                .addOnSuccessListener(documentReference -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.buttonPost.setEnabled(true);

                    // Set the ID from Firestore
                    announcement.setId(documentReference.getId());

                    // Add to UI
                    adapter.addAnnouncement(announcement);
                    binding.textNoAnnouncements.setVisibility(View.GONE);

                    // Clear inputs
                    binding.dropdownAnnouncementType.setText("");
                    binding.dropdownDepartment.setText("");
                    binding.editTextMessage.setText("");

                    Toast.makeText(this, "Announcement posted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.buttonPost.setEnabled(true);
                    Toast.makeText(this, "Failed to post announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDeleteAnnouncement(Announcement announcement, int position) {
        if (announcement.getId() != null) {
            db.collection("announcements")
                    .document(announcement.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        adapter.removeAnnouncement(position);
                        Toast.makeText(this, "Announcement deleted", Toast.LENGTH_SHORT).show();

                        if (adapter.getItemCount() == 0) {
                            binding.textNoAnnouncements.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}