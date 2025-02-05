package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityAdmissionBinding;

public class AdmissionActivity extends AppCompatActivity {
    private ActivityAdmissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdmissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupDegreeSpinner();
        setupSubmitApplicationButton();
    }

    private void setupToolbar() {
        binding.toolbarAdmission.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupDegreeSpinner() {
        String[] degrees = {
                "Bachelor's",
                "Master's",
                "PhD",
                "Associate's"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                degrees
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerDegreeType.setAdapter(adapter);
    }

    private void setupSubmitApplicationButton() {
        binding.btnSubmitApplication.setOnClickListener(v -> {
            if (validateAdmissionForm()) {
                // TODO: Implement actual admission application submission
                Toast.makeText(
                        this,
                        "Admission Application Submitted!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private boolean validateAdmissionForm() {
        String name = binding.editTextFullName.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();

        if (name.isEmpty()) {
            binding.editTextFullName.setError("Full Name is required");
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.editTextEmail.setError("Valid email is required");
            return false;
        }

        return true;
    }
}