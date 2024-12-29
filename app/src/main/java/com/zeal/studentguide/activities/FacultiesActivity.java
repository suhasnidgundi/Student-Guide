package com.zeal.studentguide.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.zeal.studentguide.adapters.FacultyAdapter;
import com.zeal.studentguide.databinding.ActivityFacultiesBinding;
import com.zeal.studentguide.models.FacultyWithUser;
import com.zeal.studentguide.utils.PreferenceManager;
import com.zeal.studentguide.viewmodels.FacultiesViewModel;
import java.util.ArrayList;

public class FacultiesActivity extends AppCompatActivity {
    private ActivityFacultiesBinding binding;
    private FacultiesViewModel viewModel;
    private FacultyAdapter adapter;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultiesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        setupViewModel();
        setupRecyclerView();
        loadFaculties();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FacultiesViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new FacultyAdapter(new ArrayList<>(), this::onFacultyClicked);
        binding.recyclerViewFaculties.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewFaculties.setAdapter(adapter);
    }

    @SuppressLint("SetTextI18n")
    private void loadFaculties() {
        binding.progressBar.setVisibility(View.VISIBLE);

        // First try to get department from intent
        String studentDepartment = getIntent().getStringExtra("branch");

        // If null, try to get from preferences
        if (studentDepartment == null || studentDepartment.isEmpty()) {
            studentDepartment = preferenceManager.getUserDepartment();
        }

        // If still null, show error message
        if (studentDepartment == null || studentDepartment.isEmpty()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.textNoFaculties.setText("Department information not found. Please try again.");
            binding.textNoFaculties.setVisibility(View.VISIBLE);
            return;
        }

        // Save department to preferences for future use
        preferenceManager.setUserDepartment(studentDepartment);

        String finalStudentDepartment = studentDepartment;
        viewModel.getFacultiesByDepartment(studentDepartment).observe(this, faculties -> {
            binding.progressBar.setVisibility(View.GONE);
            if (faculties != null && !faculties.isEmpty()) {
                adapter.updateFaculties(faculties);
                binding.textNoFaculties.setVisibility(View.GONE);
            } else {
                binding.textNoFaculties.setText("No faculty members found for " + finalStudentDepartment);
                binding.textNoFaculties.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onFacultyClicked(FacultyWithUser faculty) {
        startActivity(FacultyDetailActivity.newIntent(this, faculty.getFacultyId()));
    }
}