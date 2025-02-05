package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityInternshipBinding;

public class InternshipOpportunitiesActivity extends AppCompatActivity {
    private ActivityInternshipBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInternshipBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupIndustrySpinner();
        setupSearchButton();
    }

    private void setupToolbar() {
        binding.toolbarInternship.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupIndustrySpinner() {
        String[] industries = {
                "Technology",
                "Finance",
                "Healthcare",
                "Marketing",
                "Engineering"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                industries
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerIndustry.setAdapter(adapter);
    }

    private void setupSearchButton() {
        binding.btnSearchInternships.setOnClickListener(v -> {
            String selectedIndustry = binding.spinnerIndustry.getSelectedItem().toString();
            String location = binding.editTextLocation.getText().toString().trim();

            // TODO: Implement actual internship search logic
            Toast.makeText(
                    this,
                    "Searching Internships in " + selectedIndustry + " at " + location,
                    Toast.LENGTH_SHORT
            ).show();
        });
    }
}