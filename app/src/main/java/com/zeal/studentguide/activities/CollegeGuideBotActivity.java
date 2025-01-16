package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.ViewPagerAdapter;
import com.zeal.studentguide.databinding.ActivityCollegeGuideBotBinding;

public class CollegeGuideBotActivity extends AppCompatActivity {

    private ActivityCollegeGuideBotBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCollegeGuideBotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupUI();
        setupViewPager();
    }

    private void setupUI() {
        setSupportActionBar(binding.toolbar);
    }

    private void setupViewPager() {
        // Set up the ViewPager2 with the adapter
        binding.viewPager.setAdapter(new ViewPagerAdapter(this));

        // Handle page selection changes
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Avoid memory leaks
    }
}
