package com.zeal.studentguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zeal.studentguide.databinding.FragmentVoiceBinding;

public class VoiceFragment extends Fragment {

    private FragmentVoiceBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVoiceBinding.inflate(inflater, container, false);

        binding.micButton.setOnClickListener(v -> {
            // Implement voice-related logic here
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
