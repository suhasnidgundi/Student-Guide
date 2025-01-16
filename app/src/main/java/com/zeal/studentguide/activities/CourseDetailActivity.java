package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.CourseContentAdapter;
import com.zeal.studentguide.adapters.CourseMaterialAdapter;
import com.zeal.studentguide.databinding.ActivityCourseDetailBinding;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.CourseContent;
import com.zeal.studentguide.models.CourseMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        database.collection("courses")
                .document(courseId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Course course = documentSnapshot.toObject(Course.class);
                    if (course != null) {
                        binding.textCourseName.setText(course.getCourseName());
                        binding.textCourseCode.setText(course.getCourseCode());
                        binding.textCredits.setText(String.format("%d Credits", course.getCredits()));
                    }
                })
                .addOnFailureListener(e -> showToast("Error loading course details"));
    }

    private void loadCourseMaterials() {
        database.collection("courses")
                .document(courseId)
                .collection("materials")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, List<CourseMaterial>> materialsBySection = new HashMap<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        CourseMaterial material = doc.toObject(CourseMaterial.class);
                        if (material != null) {
                            String section = material.getSection();
                            materialsBySection.computeIfAbsent(section, k -> new ArrayList<>())
                                    .add(material);
                        }
                    }

                    displayMaterialSections(materialsBySection);
                })
                .addOnFailureListener(e -> showToast("Error loading course materials"));
    }

    private void displayMaterialSections(Map<String, List<CourseMaterial>> materialsBySection) {
        binding.containerMaterials.removeAllViews();

        for (Map.Entry<String, List<CourseMaterial>> entry : materialsBySection.entrySet()) {
            View sectionView = getLayoutInflater().inflate(
                    R.layout.item_course_material_section, binding.containerMaterials, false);

            TextView textSectionTitle = sectionView.findViewById(R.id.textSectionTitle);
            RecyclerView recyclerViewMaterials = sectionView.findViewById(R.id.recyclerViewMaterials);

            textSectionTitle.setText(capitalizeFirst(entry.getKey()));
            recyclerViewMaterials.setLayoutManager(new LinearLayoutManager(this));

            CourseMaterialAdapter adapter = new CourseMaterialAdapter(
                    entry.getValue(),
                    this::openMaterial
            );
            recyclerViewMaterials.setAdapter(adapter);

            binding.containerMaterials.addView(sectionView);
        }
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

    private void openMaterial(CourseMaterial material) {
        // Implement material opening logic here
        // Could open PDF viewer, download file, etc.
        showToast("Opening: " + material.getName());
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}