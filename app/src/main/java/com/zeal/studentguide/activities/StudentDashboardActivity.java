package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.zeal.studentguide.databinding.ActivityStudentDashboardBinding;
import com.zeal.studentguide.utils.FirebaseManager;
import com.zeal.studentguide.utils.PreferenceManager;
import com.zeal.studentguide.R;
import com.zeal.studentguide.models.Announcement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentDashboardActivity extends AppCompatActivity {
    private ActivityStudentDashboardBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseManager firebaseManager;
    private FirebaseFirestore db;
    private int currentAnnouncementIndex = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseManager
        firebaseManager = FirebaseManager.getInstance();
        preferenceManager = new PreferenceManager(this);
        db = FirebaseFirestore.getInstance();

        // Check if user is logged in, if not redirect to login
        if (!preferenceManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setupProfile();
        loadAndDisplayAnnouncements();
        setupClickListeners();
    }

    private void logout() {
        // Show loading indicator if you have one
        // binding.progressBar.setVisibility(View.VISIBLE);

        firebaseManager.logout(this, new FirebaseManager.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Clear preferences first
                preferenceManager.clear();

                runOnUiThread(() -> {
                    // Hide loading indicator if you have one
                    // binding.progressBar.setVisibility(View.GONE);
                    redirectToLogin();
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> {
                    // Hide loading indicator if you have one
                    // binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(StudentDashboardActivity.this,
                            "Logout failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
        // Clear the back stack so user can't go back after logout
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void setupProfile() {
        String studentName = preferenceManager.getUsername();
        binding.textStudentName.setText(studentName);

        binding.imageProfile.setOnClickListener(this::showProfileMenu);
    }

    private void showProfileMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menuEditProfile) {
                startActivity(new Intent(this, EditProfileActivity.class));
                return true;
            } else if (itemId == R.id.menuLogout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void setupClickListeners() {
        binding.cardVirtualTour.setOnClickListener(v ->
                startActivity(new Intent(this, VirtualCollegeTourActivity.class)));
        binding.cardAcademics.setOnClickListener(v ->
                startActivity(new Intent(this, AcademicsActivity.class)));

        binding.navigationBot.setOnClickListener(v ->
                startActivity(new Intent(this, CollegeGuideBotActivity.class)));

        binding.cardAdmission.setOnClickListener(v ->
                startActivity(new Intent(this, AdmissionAdministrationDashboardActivity.class)));

        binding.cardFaculties.setOnClickListener(v -> {
            String department = preferenceManager.getUserDepartment();
            if (department == null || department.isEmpty()) {
                Toast.makeText(this, "Please complete your profile with department information first",
                        Toast.LENGTH_LONG).show();
                // Optionally redirect to profile completion
                startActivity(new Intent(this, EditProfileActivity.class));
            } else {
                Intent intent = new Intent(this, FacultiesActivity.class);
                intent.putExtra("branch", department);
                startActivity(intent);
            }
        });
    }

    private void loadAndDisplayAnnouncements() {
        // Get the student's department
        String studentDepartment = preferenceManager.getUserDepartment();

        // Query to get announcements for either "All Departments" or the student's specific department
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Announcement> announcements = queryDocumentSnapshots.toObjects(Announcement.class);
                    List<Announcement> relevantAnnouncements = new ArrayList<>();

                    // Filter announcements for current department or "All Departments"
                    for (Announcement announcement : announcements) {
                        if (announcement.getDepartment().equals("All Departments") ||
                                announcement.getDepartment().equals(studentDepartment)) {
                            relevantAnnouncements.add(announcement);
                        }
                    }

                    // Display the announcement or show "No announcements" text
                    if (!relevantAnnouncements.isEmpty()) {
                        displayLatestAnnouncement(relevantAnnouncements);
                    } else {
                        binding.textNoAnnouncements.setText("No announcements available");
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    binding.textNoAnnouncements.setText("Failed to load announcements");
                });
    }

    private void displayLatestAnnouncement(List<Announcement> announcements) {
        if (announcements.isEmpty()) return;

        // Get the most recent announcement
        Announcement latest = announcements.get(0);

        // Update the TextView with the announcement message
        binding.textNoAnnouncements.setText(latest.getMessage());

        // Make text marquee scrollable if it's longer than the view
        binding.textNoAnnouncements.setSelected(true);
        binding.textNoAnnouncements.setSingleLine(true);
        binding.textNoAnnouncements.setMarqueeRepeatLimit(-1); // -1 means forever
        binding.textNoAnnouncements.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

}