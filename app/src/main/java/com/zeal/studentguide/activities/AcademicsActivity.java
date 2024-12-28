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
import com.zeal.studentguide.databinding.ActivityAcademicsBinding;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.Student;
import com.zeal.studentguide.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class AcademicsActivity extends AppCompatActivity {
    private ActivityAcademicsBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Student studentProfile;

    private CourseListAdapter courseAdapter;
    private List<Course> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAcademicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        database = FirebaseFirestore.getInstance();

        coursesList = new ArrayList<>();
        courseAdapter = new CourseListAdapter(coursesList, course -> {
            // Handle course click
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("courseId", course.getCourseId());
            startActivity(intent);
        });

        // Set up the RecyclerView
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCourses.setAdapter(courseAdapter);

        setupToolbar();
        loadStudentProfile();
    }

    private void setupToolbar() {
        binding.toolbarAcademics.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadStudentProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);

        database.collection("students")
                .document(preferenceManager.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful() && task.getResult() != null) {
                        studentProfile = task.getResult().toObject(Student.class);
                        if (studentProfile != null) {
                            displayAcademicInfo();
                        }
                    } else {
                        showToast("Unable to load academic information");
                    }
                });
    }

    private void displayAcademicInfo() {
        binding.textDepartment.setText(studentProfile.getBranch());
        binding.textSemester.setText(studentProfile.getSemester());
        binding.textBatch.setText(studentProfile.getBatch());
        binding.textRollNumber.setText(studentProfile.getRollNumber());

        // Load academic details like courses, attendance, etc.
        loadCourses();
        loadAttendance();
    }

    private void loadCourses() {
        database.collection("academics")
                .document(studentProfile.getBranch())
                .collection("semesters")
                .document(studentProfile.getSemester())
                .collection("courses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    coursesList.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Course course = document.toObject(Course.class);
                        if (course != null) {
                            coursesList.add(course);
                        }
                    }
                    courseAdapter.notifyDataSetChanged();

                    if (coursesList.isEmpty()) {
                        binding.textNoCourses.setVisibility(View.VISIBLE);
                    } else {
                        binding.textNoCourses.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> showToast("Error loading courses"));
    }

    private void loadAttendance() {
        database.collection("attendance")
                .document(preferenceManager.getUserId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double attendance = documentSnapshot.getDouble("percentage");
                        if (attendance != null) {
                            binding.textAttendance.setText(String.format("%.1f%%", attendance));
                        }
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}