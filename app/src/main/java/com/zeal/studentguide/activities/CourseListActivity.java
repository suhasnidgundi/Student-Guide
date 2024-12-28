package com.zeal.studentguide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.adapters.CourseListAdapter;
import com.zeal.studentguide.databinding.ActivityCourseListBinding;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity implements CourseListAdapter.CourseClickListener {
    private ActivityCourseListBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private CourseListAdapter courseAdapter;
    private List<Course> courses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setupToolbar();
        loadCourses();
    }

    private void init() {
        preferenceManager = new PreferenceManager(this);
        database = FirebaseFirestore.getInstance();
        courses = new ArrayList<>();
        courseAdapter = new CourseListAdapter(courses, this);
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCourses.setAdapter(courseAdapter);
    }

    private void setupToolbar() {
        binding.toolbarCourses.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadCourses() {
        binding.progressBar.setVisibility(View.VISIBLE);

        // First get the student's branch and semester
        database.collection("students")
                .document(preferenceManager.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String branch = documentSnapshot.getString("branch");
                    String semester = documentSnapshot.getString("semester");

                    // Then load courses for that branch and semester
                    database.collection("academics")
                            .document(branch)
                            .collection("semesters")
                            .document(semester)
                            .collection("courses")
                            .get()
                            .addOnCompleteListener(task -> {
                                binding.progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful() && task.getResult() != null) {
                                    courses.clear();
                                    for (DocumentSnapshot document : task.getResult()) {
                                        Course course = document.toObject(Course.class);
                                        if (course != null) {
                                            courses.add(course);
                                        }
                                    }
                                    courseAdapter.notifyDataSetChanged();

                                    if (courses.isEmpty()) {
                                        binding.textNoCourses.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    showToast("Unable to load courses");
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Error loading student information");
                });
    }

    @Override
    public void onCourseClicked(Course course) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("courseId", course.getCourseId());
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}