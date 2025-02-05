package com.zeal.studentguide.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.AdministrationAdapter;
import com.zeal.studentguide.databinding.ActivityAdmissionAdminBinding;
import com.zeal.studentguide.models.AdministrationItem;

import java.util.ArrayList;
import java.util.List;

public class AdmissionAdministrationDashboardActivity extends AppCompatActivity {

    private ActivityAdmissionAdminBinding binding;
    private AdministrationAdapter adapter;
    private List<AdministrationItem> administrationItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdmissionAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupStatistics();
        setupAdministrationItems();
        setupRecyclerView();
        setupToolbar();
    }

    private void setupStatistics() {
        // Mock statistics - replace with actual Firebase queries
        binding.cardTotalApplications.setText("Total Applications: 250");
        binding.cardProcessedApplications.setText("Processed: 180");
        binding.cardPendingApplications.setText("Pending: 70");
    }

    private void setupAdministrationItems() {
        administrationItems = new ArrayList<>();
        administrationItems.add(new AdministrationItem(
                "Scholarship Application",
                "Apply for various scholarships available for students",
                R.drawable.ic_scholarship
        ));
        administrationItems.add(new AdministrationItem(
                "Internship Opportunities",
                "Explore and apply for internships in your field",
                R.drawable.ic_internship // Temporary placeholder
        ));
        administrationItems.add(new AdministrationItem(
                "Admission Process",
                "Step-by-step guide to college admission",
                R.drawable.ic_admission // Temporary placeholder
        ));
        administrationItems.add(new AdministrationItem(
                "Financial Aid",
                "Information about financial assistance programs",
                R.drawable.ic_financialaid // Temporary placeholder
        ));
    }

    private void setupRecyclerView() {
        binding.recyclerAdministration.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdministrationAdapter(this, administrationItems);
        binding.recyclerAdministration.setAdapter(adapter);
    }

    private void setupToolbar() {
        binding.toolbarAdmissionAdmin.setNavigationOnClickListener(v -> onBackPressed());
    }
}