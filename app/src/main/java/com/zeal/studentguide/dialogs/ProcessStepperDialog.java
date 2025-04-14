package com.zeal.studentguide.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.zeal.studentguide.R;
import com.zeal.studentguide.adapters.ProcessStepperAdapter;

import java.util.ArrayList;

public class ProcessStepperDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_STEPS = "steps";

    private String title;
    private ArrayList<String[]> steps;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private Button btnNext;
    private Button btnPrevious;
    private TextView txtStepTitle;
    
    public static ProcessStepperDialog newInstance(String title, ArrayList<String[]> steps) {
        ProcessStepperDialog fragment = new ProcessStepperDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_STEPS, steps);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            steps = (ArrayList<String[]>) getArguments().getSerializable(ARG_STEPS);
        }
        
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_process_stepper, container, false);
        
        TextView txtDialogTitle = view.findViewById(R.id.text_dialog_title);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
        btnNext = view.findViewById(R.id.button_next);
        btnPrevious = view.findViewById(R.id.button_previous);
        txtStepTitle = view.findViewById(R.id.text_step_title);
        Button btnClose = view.findViewById(R.id.button_close);
        
        txtDialogTitle.setText(title);
        
        // Set up ViewPager with adapter
        ProcessStepperAdapter adapter = new ProcessStepperAdapter(requireActivity(), steps);
        viewPager.setAdapter(adapter);
        
        // Set up TabLayout with ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // No text for tabs, just indicators
        }).attach();
        
        updateButtons(0);
        updateStepTitle(0);
        
        // Set up button listeners
        btnNext.setOnClickListener(v -> {
            int currentPosition = viewPager.getCurrentItem();
            if (currentPosition < steps.size() - 1) {
                viewPager.setCurrentItem(currentPosition + 1);
            }
        });
        
        btnPrevious.setOnClickListener(v -> {
            int currentPosition = viewPager.getCurrentItem();
            if (currentPosition > 0) {
                viewPager.setCurrentItem(currentPosition - 1);
            }
        });
        
        btnClose.setOnClickListener(v -> dismiss());
        
        // Listen for page changes
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateButtons(position);
                updateStepTitle(position);
            }
        });
        
        return view;
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        }
    }
    
    private void updateButtons(int position) {
        btnPrevious.setEnabled(position > 0);
        btnPrevious.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        
        if (position == steps.size() - 1) {
            btnNext.setText("Finish");
            btnNext.setOnClickListener(v -> dismiss());
        } else {
            btnNext.setText("Next");
            btnNext.setOnClickListener(v -> viewPager.setCurrentItem(position + 1));
        }
    }
    
    private void updateStepTitle(int position) {
        if (position < steps.size()) {
            txtStepTitle.setText(String.format("Step %d: %s", position + 1, steps.get(position)[0]));
        }
    }
}