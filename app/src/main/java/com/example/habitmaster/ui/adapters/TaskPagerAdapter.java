package com.example.habitmaster.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.habitmaster.ui.fragments.TaskTabFragment;

public class TaskPagerAdapter extends FragmentStateAdapter {
    public TaskPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return TaskTabFragment.newInstance(false); // jednokratni
        } else {
            return TaskTabFragment.newInstance(true); // ponavljajući
        }
    }

    @Override
    public int getItemCount() {
        return 2; // dva taba
    }
}
