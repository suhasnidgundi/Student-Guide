package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeal.studentguide.adapters.CourseAdapter;
import com.zeal.studentguide.databinding.ActivityFacultyCoursesBinding;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.utils.PreferenceManager;
import com.zeal.studentguide.viewmodels.CourseViewModel;

import java.util.ArrayList;

public class FacultyCoursesActivity extends AppCompatActivity {
    private ActivityFacultyCoursesBinding binding;
    private PreferenceManager preferenceManager;
    private CourseViewModel courseViewModel;
    private CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyCoursesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setupToolbar();
        setupRecyclerView();
        loadFacultyCourses();
    }

    private void init() {
        preferenceManager = new PreferenceManager(this);
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        // Initialize adapter with edit and delete listeners
        courseAdapter = new CourseAdapter(
                course -> startActivity(new Intent(this, CourseDetailActivity.class)
                        .putExtra("courseId", course.getCourseId())),
                null  // No delete functionality for faculty
        );
    }

    private void setupToolbar() {
        binding.toolbarFacultyCourses.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCourses.setAdapter(courseAdapter);
    }

    private void loadFacultyCourses() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String facultyId = preferenceManager.getUserId();

        courseViewModel.getCoursesByFaculty(facultyId).observe(this, courses -> {
            binding.progressBar.setVisibility(View.GONE);

            if (courses != null && !courses.isEmpty()) {
                courseAdapter.submitList(courses);
                binding.recyclerViewCourses.setVisibility(View.VISIBLE);
                binding.textNoCourses.setVisibility(View.GONE);
            } else {
                courseAdapter.submitList(new ArrayList<>()); // Submit empty list instead of null
                binding.recyclerViewCourses.setVisibility(View.GONE);
                binding.textNoCourses.setVisibility(View.VISIBLE);
            }
        });

        courseViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                showToast(error);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}