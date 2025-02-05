package com.zeal.studentguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import com.zeal.studentguide.R;

public class AdmissionStep2Fragment extends Fragment {

    public AdmissionStep2Fragment() { /* Required empty public constructor */ }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admission_step2, container, false);
    }
}
