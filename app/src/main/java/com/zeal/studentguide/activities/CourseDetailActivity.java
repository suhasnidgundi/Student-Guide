package com.zeal.studentguide.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Date;
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
        loadCourseMaterials();
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
        database.collection("course_materials")
                .whereEqualTo("courseId", courseId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Map<String, List<CourseMaterial>> materialsBySection = new HashMap<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        try {
                            // Try to get the data as a Map first
                            Map<String, Object> data = doc.getData();
                            if (data != null) {
                                // Create CourseMaterial manually from the map
                                CourseMaterial material = new CourseMaterial(
                                        doc.getId(),
                                        (String) data.get("title"), // Note: using "title" from DTO
                                        (String) data.get("type"),
                                        (String) data.get("link"),  // Note: using "link" from DTO
                                        "General" // Default section if not specified
                                );

                                materialsBySection.computeIfAbsent("General", k -> new ArrayList<>())
                                        .add(material);
                            }
                        } catch (Exception e) {
                            showToast("Error parsing material: " + e.getMessage());
                        }
                    }

                    if (materialsBySection.isEmpty()) {
                        showToast("No materials found for this course");
                    } else {
                        displayMaterialSections(materialsBySection);
                    }
                })
                .addOnFailureListener(e -> showToast("Error loading course materials: " + e.getMessage()));
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
        try {
            String url = material.getUrl();
            String type = material.getType();

            if (url == null || url.isEmpty()) {
                showToast("Material URL is not available");
                return;
            }

            Uri uri = Uri.parse(url);
            Intent intent;

            // Handle different material types
            switch (type.toLowerCase()) {
                case "pdf":
                    // For PDF files
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    // Try to open with installed PDF reader
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // If no PDF reader is installed, open in browser
                        openInBrowser(url);
                    }
                    break;

                case "video":
                    // For video files
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        openInBrowser(url);
                    }
                    break;

                case "audio":
                    // For audio files
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "audio/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        openInBrowser(url);
                    }
                    break;

                case "document":
                case "docx":
                case "doc":
                    // For Word documents
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/msword");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        openInBrowser(url);
                    }
                    break;

                case "spreadsheet":
                case "xlsx":
                case "xls":
                    // For Excel files
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        openInBrowser(url);
                    }
                    break;

                case "presentation":
                case "pptx":
                case "ppt":
                    // For PowerPoint files
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        openInBrowser(url);
                    }
                    break;

                case "textbook":
                case "article":
                case "reference":
                default:
                    // Default case: open in browser
                    openInBrowser(url);
                    break;
            }

            // Log material access (optional)
            logMaterialAccess(material.getId());

        } catch (Exception e) {
            showToast("Error opening material: " + e.getMessage());
        }
    }

    private void openInBrowser(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        try {
            startActivity(browserIntent);
        } catch (ActivityNotFoundException e) {
            showToast("No browser application found");
        }
    }

    private void logMaterialAccess(String materialId) {
        // Optional: Log that student accessed this material
        // You can implement analytics or tracking here
        if (materialId == null) return;

        // Example: Log to Firebase Analytics or Firestore
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> accessLog = new HashMap<>();
        accessLog.put("userId", userId);
        accessLog.put("materialId", materialId);
        accessLog.put("accessTimestamp", new Date());

        database.collection("material_access_logs")
                .add(accessLog)
                .addOnFailureListener(e -> Log.e("CourseDetail", "Failed to log material access", e));
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}