package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.CourseAdapter;
import com.zeal.studentguide.databinding.ActivityCourseManagementBinding;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.Faculty;import com.zeal.studentguide.viewmodels.CourseViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.UUID;

public class CourseManagementActivity extends AppCompatActivity {
    private ActivityCourseManagementBinding binding;
    private CourseViewModel courseViewModel;
    private CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCourseManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        setupRecyclerView();
        setupClickListeners();
        observeCourses();
    }

    private void setupRecyclerView() {
        courseAdapter = new CourseAdapter(
                course -> showCourseDialog(course, true),
                this::confirmDeleteCourse
        );
        binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCourses.setAdapter(courseAdapter);
    }

    private void setupClickListeners() {
        binding.fabAddCourse.setOnClickListener(v -> showCourseDialog(null, false));
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void observeCourses() {
        courseViewModel.getAllCourses().observe(this, courses -> {
            courseAdapter.submitList(courses);
            binding.progressBar.setVisibility(View.GONE);
            binding.textNoCourses.setVisibility(courses.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void showCourseDialog(Course course, boolean isEdit) {
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_course, null);
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

    TextInputEditText editCourseName = dialogView.findViewById(R.id.editCourseName);
    TextInputEditText editCourseCode = dialogView.findViewById(R.id.editCourseCode);
    TextInputEditText editCredits = dialogView.findViewById(R.id.editCredits);
    TextInputEditText editDescription = dialogView.findViewById(R.id.editDescription);
    TextInputEditText editSemester = dialogView.findViewById(R.id.editSemester);
    
    Spinner facultySpinner = dialogView.findViewById(R.id.spinnerFaculty);
    ArrayList<Faculty> facultyList = new ArrayList<>();
    
    // Add a placeholder faculty for empty state
    Faculty placeholderFaculty = new Faculty("", "No faculty available", "");
    facultyList.add(placeholderFaculty);
    
    ArrayAdapter<Faculty> facultyAdapter = new ArrayAdapter<Faculty>(this,
            android.R.layout.simple_spinner_item, facultyList) {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            Faculty faculty = getItem(position);
            TextView textView = view.findViewById(android.R.id.text1);
            if (faculty != null && !faculty.getFacultyId().isEmpty()) {
                textView.setText(faculty.getDesignation() + " - " + faculty.getDepartment());
            } else {
                textView.setText("No faculty available");
            }
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            Faculty faculty = getItem(position);
            TextView textView = view.findViewById(android.R.id.text1);
            if (faculty != null && !faculty.getFacultyId().isEmpty()) {
                textView.setText(faculty.getDesignation() + " - " + faculty.getDepartment());
            } else {
                textView.setText("No faculty available");
            }
            return view;
        }
    };

    facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    facultySpinner.setAdapter(facultyAdapter);

    courseViewModel.getAllFaculty().observe(this, facultyMembers -> {
        facultyList.clear();
        facultyList.add(placeholderFaculty); // Always keep placeholder
        
        if (facultyMembers != null && !facultyMembers.isEmpty()) {
            facultyList.addAll(facultyMembers);
        }
        facultyAdapter.notifyDataSetChanged();
        
        if (isEdit && course != null) {
            int position = 0; // Default to placeholder
            for (int i = 0; i < facultyList.size(); i++) {
                if (facultyList.get(i).getFacultyId().equals(course.getFacultyId())) {
                    position = i;
                    break;
                }
            }
            facultySpinner.setSelection(position);
        }
    });

    // Set existing values for edit
    if (isEdit && course != null) {
        editCourseName.setText(course.getCourseName());
        editCourseCode.setText(course.getCourseCode());
        editCredits.setText(String.valueOf(course.getCredits()));
        editDescription.setText(course.getDescription());
        editSemester.setText(course.getSemester());
    }

    builder.setView(dialogView)
            .setTitle(isEdit ? "Edit Course" : "Add New Course")
            .setPositiveButton(isEdit ? "Update" : "Add", (dialog, which) -> {
                String courseName = editCourseName.getText().toString();
                String courseCode = editCourseCode.getText().toString();
                String creditsStr = editCredits.getText().toString();
                String description = editDescription.getText().toString();
                String semester = editSemester.getText().toString();
                
                Faculty selectedFaculty = (Faculty) facultySpinner.getSelectedItem();
                if (selectedFaculty == null || selectedFaculty.getFacultyId().isEmpty()) {
                    Toast.makeText(this, "Please select a valid faculty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (validateInput(courseName, courseCode, creditsStr)) {
                    int credits = Integer.parseInt(creditsStr);
                    if (isEdit) {
                        course.setCourseName(courseName);
                        course.setCourseCode(courseCode);
                        course.setCredits(credits);
                        course.setDescription(description);
                        course.setSemester(semester);
                        course.setFacultyId(selectedFaculty.getFacultyId());
                        courseViewModel.updateCourse(course);
                    } else {
                        Course newCourse = new Course(
                                UUID.randomUUID().toString(),
                                courseName,
                                courseCode,
                                credits,
                                selectedFaculty.getFacultyId()
                        );
                        newCourse.setDescription(description);
                        newCourse.setSemester(semester);
                        courseViewModel.insertCourse(newCourse);
                    }
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
}

    private boolean validateInput(String name, String code, String credits) {
        if (name.trim().isEmpty() || code.trim().isEmpty() || credits.trim().isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            Integer.parseInt(credits);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Credits must be a valid number", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void confirmDeleteCourse(Course course) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Course")
                .setMessage("Are you sure you want to delete " + course.getCourseName() + "?")
                .setPositiveButton("Delete", (dialog, which) ->
                        courseViewModel.deleteCourse(course))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void observeViewModel() {
        courseViewModel.getAllCourses().observe(this, courses -> {
            courseAdapter.submitList(courses);
            binding.textNoCourses.setVisibility(courses.isEmpty() ? View.VISIBLE : View.GONE);
        });

        courseViewModel.getIsLoading().observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        courseViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                showErrorSnackbar(errorMessage);
            }
        });
    }

    private void showErrorSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setAction("Retry", v -> courseViewModel.syncWithFirebase())
                .show();
    }
}