package com.zeal.studentguide.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.databinding.ActivityFacultyDashboardBinding;
import com.zeal.studentguide.utils.PreferenceManager;

public class FacultyDashboardActivity extends AppCompatActivity {
    private ActivityFacultyDashboardBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFacultyDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);
        setupClickListeners();
    }

    private void setupClickListeners() {
//        binding.cardMyCourses.setOnClickListener(v ->
//                startActivity(new Intent(this, MyCoursesActivity.class)));
//        binding.cardAttendance.setOnClickListener(v ->
//                startActivity(new Intent(this, AttendanceActivity.class)));
//        binding.cardAssignments.setOnClickListener(v ->
//                startActivity(new Intent(this, AssignmentsActivity.class)));
//        binding.cardSchedule.setOnClickListener(v ->
//                startActivity(new Intent(this, ScheduleActivity.class)));
    }
}