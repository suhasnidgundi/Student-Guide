package com.zeal.studentguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zeal.studentguide.R;

public class ProcessStepFragment extends Fragment {
    private static final String ARG_CONTENT = "content";
    
    private String content;
    
    public static ProcessStepFragment newInstance(String content) {
        ProcessStepFragment fragment = new ProcessStepFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONTENT, content);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            content = getArguments().getString(ARG_CONTENT);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_process_step, container, false);
        TextView txtContent = view.findViewById(R.id.text_step_content);
        txtContent.setText(content);
        return view;
    }
}