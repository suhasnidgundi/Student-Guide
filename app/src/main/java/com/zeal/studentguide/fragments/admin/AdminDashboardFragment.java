package com.zeal.studentguide.fragments.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.zeal.studentguide.databinding.FragmentAdminDashboardBinding;

public class AdminDashboardFragment extends Fragment {
    private FragmentAdminDashboardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false);
        setupUI();
        return binding.getRoot();
    }

    private void setupUI() {
        setupUserManagementCard();
        setupSystemStatsCard();
        setupReportsCard();
        setupSettingsCard();
    }

    // Implementation methods...
    private void setupUserManagementCard() {
        binding.cardUserManagement.setOnClickListener(v -> {
            // Handle user management card click
        });
    }

    private void setupSystemStatsCard() {
        binding.textUserStats.setOnClickListener(v -> {
            // Handle system stats card click
        });
    }

    private void setupReportsCard() {
        // Setup reports card
    }

    private void setupSettingsCard() {
        // Setup settings card
    }
}