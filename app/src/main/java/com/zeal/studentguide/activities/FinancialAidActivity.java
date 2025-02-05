package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zeal.studentguide.databinding.ActivityFinancialaidBinding;

public class FinancialAidActivity extends AppCompatActivity {
    private ActivityFinancialaidBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFinancialaidBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupAidTypeSpinner();
        setupApplyButton();
    }

    private void setupToolbar() {
        binding.toolbarFinancialAid.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupAidTypeSpinner() {
        String[] aidTypes = {
                "Government Grants",
                "University Scholarships",
                "Work-Study",
                "Private Loans"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                aidTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerAidType.setAdapter(adapter);
    }

    private void setupApplyButton() {
        binding.btnApplyFinancialAid.setOnClickListener(v -> {
            if (validateFinancialAidForm()) {
                // TODO: Implement actual financial aid application submission
                Toast.makeText(
                        this,
                        "Financial Aid Application Submitted!",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private boolean validateFinancialAidForm() {
        String income = binding.editTextAnnualIncome.getText().toString().trim();

        if (income.isEmpty()) {
            binding.editTextAnnualIncome.setError("Annual Income is required");
            return false;
        }

        try {
            double incomeValue = Double.parseDouble(income);
            if (incomeValue < 0) {
                binding.editTextAnnualIncome.setError("Invalid Income");
                return false;
            }
        } catch (NumberFormatException e) {
            binding.editTextAnnualIncome.setError("Invalid Income Format");
            return false;
        }

        return true;
    }
}