package com.zeal.studentguide.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.UserListAdapter;
import com.zeal.studentguide.databinding.ActivityUserManagementBinding;
import com.zeal.studentguide.models.User;
import com.zeal.studentguide.models.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity implements UserListAdapter.UserClickListener {
    private ActivityUserManagementBinding binding;
    private FirebaseFirestore db;
    private UserListAdapter userAdapter;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            binding = ActivityUserManagementBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            init();
            setupToolbar();
            setupRecyclerView();
            loadUsers();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        users = new ArrayList<>();
        userAdapter = new UserListAdapter(users, this);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbarUserManagement);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("User Management");
        }
    }

    private void setupRecyclerView() {
        binding.recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewUsers.setAdapter(userAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadUsers() {
        try {
            binding.progressBar.setVisibility(View.VISIBLE);

            db.collection("users")
                    .get()
                    .addOnCompleteListener(task -> {
                        try {
                            runOnUiThread(() -> {
                                binding.progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful() && task.getResult() != null) {
                                    users.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        try {
                                            User user = document.toObject(User.class);
                                            if (user != null) {
                                                users.add(user);
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    userAdapter.notifyDataSetChanged();
                                    binding.textNoUsers.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
                                } else {
                                    showToast("Failed to load users: " +
                                            (task.getException() != null ? task.getException().getMessage()
                                                    : "Unknown error"));
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            showToast("Error processing users: " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error loading users: " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_user) {
            startActivity(new Intent(this, AddUserActivity.class));
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
    }

    @Override
    public void onUserLongClick(User user) {
        showUserOptionsDialog(user);
    }

    private void showUserOptionsDialog(User user) {
        String[] options = { "Edit", "Delete", "Cancel" };

        new AlertDialog.Builder(this)
                .setTitle("User Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Edit
                            Intent intent = new Intent(this, EditUserActivity.class);
                            intent.putExtra("userId", user.getUserId());
                            startActivity(intent);
                            break;
                        case 1: // Delete
                            showDeleteConfirmationDialog(user);
                            break;
                    }
                })
                .show();
    }

    private void showDeleteConfirmationDialog(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUser(user))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(User user) {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // First delete from Firestore
        String roleCollection = user.getRole() == UserRole.STUDENT ? "students"
                : user.getRole() == UserRole.FACULTY ? "faculty" : null;

        // Create a batch operation
        WriteBatch batch = db.batch();

        // Add role-specific delete
        if (roleCollection != null) {
            batch.delete(db.collection(roleCollection).document(user.getUserId()));
        }

        // Add user delete
        batch.delete(db.collection("users").document(user.getUserId()));

        // Execute batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    users.remove(user);
                    userAdapter.notifyDataSetChanged();
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("User deleted successfully");
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Failed to delete user: " + e.getMessage());
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}