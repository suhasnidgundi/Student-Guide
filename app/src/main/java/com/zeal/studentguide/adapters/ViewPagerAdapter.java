package com.zeal.studentguide.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zeal.studentguide.fragments.ChatFragment;
import com.zeal.studentguide.fragments.VoiceFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return position == 0 ? new VoiceFragment() : new ChatFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
