package com.example.habitmaster.ui.fragments;

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
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.ui.adapters.TasksAdapter;

import java.util.List;

public class TaskListFragment extends Fragment {

    private TaskService taskService;
    private RecyclerView recyclerView;
    private TasksAdapter adapter;

    public static TaskListFragment newInstance() {
        return new TaskListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskService = new TaskService(getContext());
        loadTasks();

        return view;
    }

    private void loadTasks() {
        new Thread(() -> {
            List<Task> tasks = taskService.getAllTasks();
            getActivity().runOnUiThread(() -> {
                adapter = new TasksAdapter(tasks);
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}