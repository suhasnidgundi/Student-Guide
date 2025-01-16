package com.zeal.studentguide.activities;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.zeal.studentguide.databinding.ActivityAddCourseMaterialBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.CourseMaterialDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddCourseMaterialActivity extends AppCompatActivity {
    private ActivityAddCourseMaterialBinding binding;
    private FirebaseFirestore db;
    private List<Course> facultyCourses;
    private ArrayAdapter<String> courseAdapter;
    private ArrayAdapter<String> typeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCourseMaterialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        setupViews();
        loadFacultyCourses();
    }

    private String getCurrentFacultyId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setupViews() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.buttonSubmit.setOnClickListener(v -> submitMaterial());

        String[] materialTypes = {"Textbook", "Reference", "Article", "Video", "Other"};
        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, materialTypes);
        binding.spinnerType.setAdapter(typeAdapter);

        facultyCourses = new ArrayList<>();
        courseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        binding.spinnerCourse.setAdapter(courseAdapter);
    }

    private void submitMaterial() {
        if (!validateInput()) return;

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonSubmit.setEnabled(false);

        CourseMaterialDTO material = new CourseMaterialDTO();
        material.setTitle(binding.editTitle.getText().toString());
        material.setLink(binding.editLink.getText().toString());
        material.setDescription(binding.editDescription.getText().toString());
        material.setType(binding.spinnerType.getText().toString());

        int selectedCoursePosition = courseAdapter.getPosition(binding.spinnerCourse.getText().toString());
        if (selectedCoursePosition >= 0 && selectedCoursePosition < facultyCourses.size()) {
            material.setCourseId(facultyCourses.get(selectedCoursePosition).getCourseId());
        }

        String materialId = UUID.randomUUID().toString();

        db.collection("course_materials")
                .document(materialId)
                .set(material)
                .addOnSuccessListener(aVoid -> {
                    showSuccessMessage();
                    finish();
                })
                .addOnFailureListener(e -> {
                    showErrorMessage(e.getMessage());
                    binding.buttonSubmit.setEnabled(true);
                    binding.progressBar.setVisibility(View.GONE);
                });
    }

    private void showSuccessMessage() {
        Toast.makeText(this, "Course material added successfully", Toast.LENGTH_SHORT).show();
    }

    private void showErrorMessage(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    private boolean validateInput() {
        if (binding.editTitle.getText().toString().trim().isEmpty()) {
            binding.editTitle.setError("Title is required");
            return false;
        }
        if (binding.editLink.getText().toString().trim().isEmpty()) {
            binding.editLink.setError("Link is required");
            return false;
        }
        if (binding.spinnerCourse.getText().toString().trim().isEmpty()) {
            binding.spinnerCourse.setError("Please select a course");
            return false;
        }
        if (binding.spinnerType.getText().toString().trim().isEmpty()) {
            binding.spinnerType.setError("Please select material type");
            return false;
        }
        return true;
    }

    private void loadFacultyCourses() {
        String facultyId = getCurrentFacultyId();

        db.collection("courses")
                .whereEqualTo("faculty_id", facultyId)
                .whereEqualTo("is_active", true)
                .get()
                .addOnSuccessListener(queryDocuments -> {
                    facultyCourses.clear();
                    List<String> courseNames = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocuments) {
                        Course course = document.toObject(Course.class);
                        if (course != null) {
                            facultyCourses.add(course);
                            courseNames.add(course.getCourseName());
                        }
                    }

                    courseAdapter.clear();
                    courseAdapter.addAll(courseNames);
                    courseAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading courses: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}