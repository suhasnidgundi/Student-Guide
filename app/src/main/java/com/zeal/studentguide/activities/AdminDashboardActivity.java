package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityAdminDashboardBinding;
import com.zeal.studentguide.utils.FirebaseManager;
import com.zeal.studentguide.utils.PreferenceManager;

public class AdminDashboardActivity extends AppCompatActivity {
    private ActivityAdminDashboardBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.cardUserManagement.setOnClickListener(v ->
                startActivity(new Intent(this, UserManagementActivity.class)));
        binding.cardCourseManagement.setOnClickListener(v ->
                startActivity(new Intent(this, CourseManagementActivity.class)));
        binding.cardAnnouncements.setOnClickListener(v ->
                Toast.makeText(this, "Announcements Clicked", Toast.LENGTH_SHORT).show());
        binding.cardReports.setOnClickListener(v ->
                Toast.makeText(this, "Reports Clicked", Toast.LENGTH_SHORT).show());

        // Profile image click listener
        binding.imageProfile.setOnClickListener(v -> showProfileMenu());
    }

    private void showProfileMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.imageProfile);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menuEditProfile) {
                startActivity(new Intent(this, EditAdminProfileActivity.class));
                return true;
            } else if (itemId == R.id.menuLogout) {
                performLogout();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void performLogout() {
        FirebaseManager.getInstance().logout(this, new FirebaseManager.FirebaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Navigate to login screen
                Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminDashboardActivity.this,
                        "Logout failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}