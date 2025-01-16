package com.zeal.studentguide.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.CourseAdapter;
import com.zeal.studentguide.databinding.ActivityCourseManagementBinding;
import com.zeal.studentguide.models.Course;
import com.zeal.studentguide.models.Departments;
import com.zeal.studentguide.models.FacultyWithUser;
import com.zeal.studentguide.viewmodels.CourseViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class CourseManagementActivity extends AppCompatActivity {
    private ActivityCourseManagementBinding binding;
    private CourseViewModel courseViewModel;
    private CourseAdapter courseAdapter;
    private static final String TAG = "CourseManagement";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            binding = ActivityCourseManagementBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Initialize ViewModel
            courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
            courseViewModel.syncWithFirebase();

            // Setup RecyclerView first
            setupRecyclerView();

            // Setup toolbar
            binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

            // Setup FAB
            binding.fabAddCourse.setOnClickListener(v -> showCourseDialog(null, false));

            // Start observing ViewModel
            observeViewModel();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            showErrorSnackbar("Failed to initialize course management");
        }
    }
    private void setupRecyclerView() {
        try {
            courseAdapter = new CourseAdapter(
                    course -> showCourseDialog(course, true),
                    course -> confirmDeleteCourse(course)
            );

            binding.recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewCourses.addItemDecoration(
                    new DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            );
            binding.recyclerViewCourses.setAdapter(courseAdapter);

        } catch (Exception e) {
            Log.e(TAG, "Error in setupRecyclerView: " + e.getMessage());
            showErrorSnackbar("Failed to setup course list");
        }
    }

    private void showCourseDialog(Course course, boolean isEdit) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_course, null);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);

        // Initialize views
        TextInputEditText editCourseName = dialogView.findViewById(R.id.editCourseName);
        TextInputEditText editCourseCode = dialogView.findViewById(R.id.editCourseCode);
        TextInputEditText editCredits = dialogView.findViewById(R.id.editCredits);
        TextInputEditText editDescription = dialogView.findViewById(R.id.editDescription);
        TextInputEditText editSemester = dialogView.findViewById(R.id.editSemester);
        Spinner facultySpinner = dialogView.findViewById(R.id.spinnerFaculty);
        Spinner departmentSpinner = dialogView.findViewById(R.id.spinnerDepartment);

        // Set up faculty adapter with custom display
        ArrayAdapter<FacultyWithUser> facultyAdapter = new ArrayAdapter<FacultyWithUser>(this,
                android.R.layout.simple_spinner_item) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                FacultyWithUser faculty = getItem(position);
                if (faculty != null) {
                    text.setText(faculty.getName() + " - " + faculty.getDepartment());
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                FacultyWithUser faculty = getItem(position);
                if (faculty != null) {
                    text.setText(faculty.getName() + " - " + faculty.getDepartment());
                }
                return view;
            }
        };
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        facultySpinner.setAdapter(facultyAdapter);

        // Set up department spinner
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        departmentAdapter.addAll(Arrays.stream(Departments.values())
                .map(Departments::getDepartmentName)
                .collect(Collectors.toList()));
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(departmentAdapter);

        // Create the dialog
        AlertDialog dialog = builder.setView(dialogView)
                .setTitle(isEdit ? "Edit Course" : "Add New Course")
                .setPositiveButton(isEdit ? "Update" : "Add", null) // We'll override this later
                .setNegativeButton("Cancel", null)
                .create();

        // Show dialog
        dialog.show();

        // Get the positive button after dialog is shown
        Button positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setEnabled(false); // Initially disabled until faculty data loads

        // Show loading indicator
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            facultySpinner.setVisibility(View.GONE);
        }

        // Observe faculty data
        courseViewModel.getAllFacultyWithUsers().observe(this, facultyMembers -> {
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }
            facultySpinner.setVisibility(View.VISIBLE);

            if (facultyMembers == null || facultyMembers.isEmpty()) {
                showErrorDialog("No Faculty Available",
                        "Please ensure there are active faculty members before creating courses.");
                dialog.dismiss();
                return;
            }

            facultyAdapter.clear();
            facultyAdapter.addAll(facultyMembers);
            positiveButton.setEnabled(true);

            // Pre-select faculty if editing
            if (isEdit && course != null) {
                for (int i = 0; i < facultyMembers.size(); i++) {
                    if (facultyMembers.get(i).getFacultyId().equals(course.getFacultyId())) {
                        facultySpinner.setSelection(i);
                        break;
                    }
                }
                // Pre-fill other fields
                editCourseName.setText(course.getCourseName());
                editCourseCode.setText(course.getCourseCode());
                editCredits.setText(String.valueOf(course.getCredits()));
                editDescription.setText(course.getDescription());
                editSemester.setText(course.getSemester());

                // Pre-select department
                String courseDepartment = course.getDepartment();
                for (int i = 0; i < departmentAdapter.getCount(); i++) {
                    if (departmentAdapter.getItem(i).equals(courseDepartment)) {
                        departmentSpinner.setSelection(i);
                        break;
                    }
                }
            }
        });

        // Set click listener for positive button
        positiveButton.setOnClickListener(v -> {
            FacultyWithUser selectedFaculty = (FacultyWithUser) facultySpinner.getSelectedItem();
            if (selectedFaculty == null) {
                showToast("Please select a faculty member");
                return;
            }

            String selectedDepartment = departmentSpinner.getSelectedItem().toString();
            String courseName = editCourseName.getText().toString();
            String courseCode = editCourseCode.getText().toString();
            String creditsStr = editCredits.getText().toString();
            String description = editDescription.getText().toString();
            String semester = editSemester.getText().toString();

            if (validateInput(courseName, courseCode, creditsStr)) {
                int credits = Integer.parseInt(creditsStr);
                if (isEdit && course != null) {
                    course.setCourseName(courseName);
                    course.setCourseCode(courseCode);
                    course.setCredits(credits);
                    course.setDescription(description);
                    course.setSemester(semester);
                    course.setDepartment(selectedDepartment);
                    course.setFacultyId(selectedFaculty.getFacultyId());
                    courseViewModel.updateCourse(course);
                } else {
                    Course newCourse = new Course(
                            UUID.randomUUID().toString(),
                            courseName,
                            courseCode,
                            credits,
                            selectedFaculty.getFacultyId(),
                            selectedDepartment
                    );
                    newCourse.setDescription(description);
                    newCourse.setSemester(semester);
                    courseViewModel.insertCourse(newCourse);
                }
                dialog.dismiss();
            }
        });
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
        try {
            // Observe courses
            courseViewModel.getAllCourses().observe(this, courses -> {
                if (courses != null) {
                    courseAdapter.submitList(courses);
                    binding.textNoCourses.setVisibility(courses.isEmpty() ? View.VISIBLE : View.GONE);
                }
            });

            // Observe loading state
            courseViewModel.getIsLoading().observe(this, isLoading -> {
                if (isLoading != null) {
                    binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                }
            });

            // Observe errors
            courseViewModel.getErrorMessage().observe(this, error -> {
                if (error != null && !error.isEmpty()) {
                    showErrorSnackbar(error);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in observeViewModel: " + e.getMessage());
            showErrorSnackbar("Failed to load course data");
        }
    }

    private void showErrorSnackbar(String message) {
        if (!isFinishing()) {
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                    .setAction("Retry", v -> courseViewModel.syncWithFirebase())
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showErrorDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Retry", (dialog, which) -> {
                    courseViewModel.syncWithFirebase();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}