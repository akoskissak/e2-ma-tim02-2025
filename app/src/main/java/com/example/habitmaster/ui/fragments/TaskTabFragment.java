package com.example.habitmaster.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.ui.activities.TaskDetailActivity;
import com.example.habitmaster.ui.adapters.TasksAdapter;

import java.time.LocalDate;
import java.util.List;

public class TaskTabFragment extends Fragment {

    private static final String ARG_REPEATING = "arg_repeating";
    private boolean repeating;
    private RecyclerView recyclerView;
    private TasksAdapter adapter;
    private TaskService taskService;

    public static TaskTabFragment newInstance(boolean repeating) {
        TaskTabFragment fragment = new TaskTabFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_REPEATING, repeating);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repeating = getArguments() != null && getArguments().getBoolean(ARG_REPEATING);
        taskService = new TaskService(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_tab, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadTasks();
        return view;
    }

    private void loadTasks() {
        new Thread(() -> {
            List<TaskInstanceDTO> tasks;
            if (repeating) {
                tasks = taskService.getRepeatingTasks(LocalDate.now());
            } else {
                tasks = taskService.getOneTimeTasks(LocalDate.now());
            }

            getActivity().runOnUiThread(() -> {
                adapter = new TasksAdapter(tasks, task -> {
                    // Launch TaskDetailActivity with taskId
                    Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                    intent.putExtra(TaskDetailActivity.EXTRA_TASK, task);
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}