package com.zeal.studentguide.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityCollegeVirtualTourBinding;

public class VirtualCollegeTourActivity extends AppCompatActivity {

    private ActivityCollegeVirtualTourBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCollegeVirtualTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
    }

    private void setupToolbar() {
        binding.toolbarVirtualTour.setNavigationOnClickListener(v -> onBackPressed());
    }
}
