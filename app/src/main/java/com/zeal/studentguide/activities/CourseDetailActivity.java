package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.adapters.CourseContentAdapter;
import com.zeal.studentguide.databinding.ActivityCourseDetailBinding;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.CourseContent;

import java.util.ArrayList;
import java.util.List;

public class CourseDetailActivity extends AppCompatActivity {
    private ActivityCourseDetailBinding binding;
    private FirebaseFirestore database;
    private String courseId;
    private CourseContentAdapter contentAdapter;
    private List<CourseContent> courseContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseId = getIntent().getStringExtra("courseId");
        if (courseId == null) {
            showToast("Invalid course");
            finish();
            return;
        }

        init();
        setupToolbar();
        loadCourseDetails();
        loadCourseContent();
    }

    private void init() {
        database = FirebaseFirestore.getInstance();
        courseContents = new ArrayList<>();
        contentAdapter = new CourseContentAdapter(courseContents);
        binding.recyclerViewContent.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewContent.setAdapter(contentAdapter);
    }

    private void setupToolbar() {
        binding.toolbarCourseDetail.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadCourseDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);

        database.collection("courses")
                .document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Course course = documentSnapshot.toObject(Course.class);
                    if (course != null) {
                        displayCourseDetails(course);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showToast("Error loading course details");
                });
    }

    private void loadCourseContent() {
        database.collection("courses")
                .document(courseId)
                .collection("content")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    courseContents.clear();
                    for (DocumentSnapshot document : querySnapshot) {
                        CourseContent content = document.toObject(CourseContent.class);
                        if (content != null) {
                            courseContents.add(content);
                        }
                    }
                    contentAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showToast("Error loading course content"));
    }

    private void displayCourseDetails(Course course) {
        binding.textCourseName.setText(course.getCourseName());
        binding.textCourseCode.setText(course.getCourseCode());
        binding.textCredits.setText(String.valueOf(course.getCredits()));
        binding.textDescription.setText(course.getDescription());
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}