package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityFacultyDashboardBinding;
import com.zeal.studentguide.utils.FirebaseManager;
import com.zeal.studentguide.utils.PreferenceManager;

public class FacultyDashboardActivity extends AppCompatActivity {
    private ActivityFacultyDashboardBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if user is logged in, if not redirect to login
        if (!preferenceManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        setupProfile();
        setupClickListeners();
    }

    private void setupProfile() {
        binding.imageProfile.setOnClickListener(this::showProfileMenu);
    }

    private void showProfileMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menuEditProfile) {
                startActivity(new Intent(this, FacultyProfileActivity.class));
                return true;
            } else if (itemId == R.id.menuLogout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void logout() {
        firebaseAuth.signOut();
        preferenceManager.clear();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(FacultyDashboardActivity.this, LoginActivity.class);
        // Clear the back stack so user can't go back after logout
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void setupClickListeners() {
        // Your existing click listeners for other dashboard items
//        binding.cardMyCourses.setOnClickListener(v ->
//                startActivity(new Intent(this, MyCoursesActivity.class)));
//        binding.cardAttendance.setOnClickListener(v ->
//                startActivity(new Intent(this, AttendanceActivity.class)));
//        binding.cardAssignments.setOnClickListener(v ->
//                startActivity(new Intent(this, AssignmentsActivity.class)));
//        binding.cardSchedule.setOnClickListener(v ->
//                startActivity(new Intent(this, ScheduleActivity.class)));
    }
}