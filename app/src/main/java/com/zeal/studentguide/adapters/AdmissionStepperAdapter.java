package com.zeal.studentguide.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zeal.studentguide.fragments.AdmissionStep1Fragment;
import com.zeal.studentguide.fragments.AdmissionStep2Fragment;
import com.zeal.studentguide.fragments.AdmissionStep3Fragment;
import com.zeal.studentguide.fragments.AdmissionStep4Fragment;

public class AdmissionStepperAdapter extends FragmentStateAdapter {
    public AdmissionStepperAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AdmissionStep1Fragment();
            case 1:
                return new AdmissionStep2Fragment();
            case 2:
                return new AdmissionStep3Fragment();
            case 3:
                return new AdmissionStep4Fragment();
            default:
                return new AdmissionStep1Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
