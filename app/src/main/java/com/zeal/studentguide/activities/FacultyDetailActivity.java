package com.zeal.studentguide.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.zeal.studentguide.databinding.ActivityFacultyDetailBinding;
import com.zeal.studentguide.viewmodels.FacultyViewModel;

public class FacultyDetailActivity extends AppCompatActivity {
    private ActivityFacultyDetailBinding binding;
    private FacultyViewModel viewModel;
    private String facultyId;

    public static Intent newIntent(Context context, String facultyId) {
        Intent intent = new Intent(context, FacultyDetailActivity.class);
        intent.putExtra("faculty_id", facultyId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        facultyId = getIntent().getStringExtra("faculty_id");
        setupViewModel();
        loadFacultyDetails();
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(FacultyViewModel.class);
    }

    private void loadFacultyDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        viewModel.getFacultyData(facultyId).observe(this, faculty -> {
            binding.progressBar.setVisibility(View.GONE);
            if (faculty != null) {
//                binding.textName.setText(faculty.getName());
                binding.textDepartment.setText(faculty.getDepartment());
                binding.textDesignation.setText(faculty.getDesignation());
                binding.textSpecialization.setText(faculty.getSpecialization());
                binding.textExperience.setText(String.format("%d years", faculty.getExperienceYears()));
                binding.textQualifications.setText(faculty.getQualifications());
            }
        });
    }
}
