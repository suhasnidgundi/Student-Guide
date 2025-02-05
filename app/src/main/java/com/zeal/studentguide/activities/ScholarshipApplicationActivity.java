package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.zeal.studentguide.databinding.ActivityScholarshipBinding;

public class ScholarshipApplicationActivity extends AppCompatActivity {
    private ActivityScholarshipBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScholarshipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupScholarshipTypeSpinner();
        setupSubmitButton();
    }

    private void setupToolbar() {
        binding.toolbarScholarship.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupScholarshipTypeSpinner() {
        String[] scholarshipTypes = {
                "Merit-Based",
                "Need-Based",
                "Athletic",
                "Research",
                "Diversity"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                scholarshipTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerScholarshipType.setAdapter(adapter);
    }

    private void setupSubmitButton() {
        binding.btnSubmitScholarship.setOnClickListener(v -> {
            if (validateForm()) {
                // TODO: Implement actual scholarship submission logic
                Toast.makeText(this, "Scholarship Application Submitted!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateForm() {
        String name = binding.editTextName.getText().toString().trim();
        String gpa = binding.editTextGPA.getText().toString().trim();

        if (name.isEmpty()) {
            binding.editTextName.setError("Name is required");
            return false;
        }

        if (gpa.isEmpty()) {
            binding.editTextGPA.setError("GPA is required");
            return false;
        }

        try {
            double gpaValue = Double.parseDouble(gpa);
            if (gpaValue < 0 || gpaValue > 4.0) {
                binding.editTextGPA.setError("Invalid GPA");
                return false;
            }
        } catch (NumberFormatException e) {
            binding.editTextGPA.setError("Invalid GPA format");
            return false;
        }

        return true;
    }
}