package com.zeal.studentguide.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zeal.studentguide.R;
import com.zeal.studentguide.fragments.ProcessStepFragment;

import java.util.ArrayList;

public class ProcessStepperAdapter extends FragmentStateAdapter {
    private ArrayList<String[]> steps;

    public ProcessStepperAdapter(FragmentActivity activity, ArrayList<String[]> steps) {
        super(activity);
        this.steps = steps;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return ProcessStepFragment.newInstance(steps.get(position)[1]);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }
}