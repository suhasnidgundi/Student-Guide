package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.databinding.ActivityAcademicsBinding;
import com.zeal.studentguide.models.Student;
import com.zeal.studentguide.utils.PreferenceManager;

public class AcademicsActivity extends AppCompatActivity {
    private ActivityAcademicsBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Student studentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAcademicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        database = FirebaseFirestore.getInstance();

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
        binding.textDepartment.setText(studentProfile.getDepartment());
        binding.textSemester.setText(studentProfile.getSemester());
        binding.textBatch.setText(studentProfile.getBatch());
        binding.textRollNumber.setText(studentProfile.getRollNumber());

        // Load academic details like courses, attendance, etc.
        loadCourses();
        loadAttendance();
    }

    private void loadCourses() {
        database.collection("academics")
                .document(studentProfile.getDepartment())
                .collection("semesters")
                .document(studentProfile.getSemester())
                .collection("courses")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Process and display courses
                    StringBuilder coursesText = new StringBuilder();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        coursesText.append("• ").append(document.getString("name")).append("\n");
                    }
                    binding.textCourses.setText(coursesText.toString());
                });
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