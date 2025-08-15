package com.example.habitmaster.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.habitmaster.R;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.ui.adapters.TaskPagerAdapter;
import com.example.habitmaster.ui.adapters.TasksAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class TaskListFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public static TaskListFragment newInstance() {
        return new TaskListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        tabLayout = view.findViewById(R.id.taskTabLayout);
        viewPager = view.findViewById(R.id.taskViewPager);

        TaskPagerAdapter adapter = new TaskPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "One time tasks" : "Repeating tasks")
        ).attach();

        return view;
    }
}
