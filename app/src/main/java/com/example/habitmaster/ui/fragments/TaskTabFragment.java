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
import android.widget.Toast;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.ui.activities.TaskDetailActivity;
import com.example.habitmaster.ui.adapters.TasksAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        if (repeating) {
            taskService.getRepeatingTasks(LocalDate.now(), new ICallback<List<TaskInstanceDTO>>() {
                @Override
                public void onSuccess(List<TaskInstanceDTO> tasks) {
                    Map<String, TaskInstanceDTO> uniqueTasks = new LinkedHashMap<>();
                    for (TaskInstanceDTO t : tasks) {
                        if (!uniqueTasks.containsKey(t.getTaskId())) {
                            uniqueTasks.put(t.getTaskId(), t);
                        }
                    }
                    List<TaskInstanceDTO> displayTasks = new ArrayList<>(uniqueTasks.values());

                    adapter = new TasksAdapter(displayTasks, task -> showRepeatingTaskPopup(task.getTaskId(), tasks));
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            taskService.getOneTimeTasks(LocalDate.now(), new ICallback<List<TaskInstanceDTO>>() {
                @Override
                public void onSuccess(List<TaskInstanceDTO> tasks) {
                    adapter = new TasksAdapter(tasks, task -> {
                        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                        intent.putExtra(TaskDetailActivity.EXTRA_TASK, task);
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showRepeatingTaskPopup(String taskId, List<TaskInstanceDTO> allTaskInstances) {
        List<TaskInstanceDTO> instancesForTask = new ArrayList<>();
        for (TaskInstanceDTO t : allTaskInstances) {
            if (t.getTaskId().equals(taskId)) {
                instancesForTask.add(t);
            }
        }

        String[] dates = new String[instancesForTask.size()];
        for (int i = 0; i < instancesForTask.size(); i++) {
            dates[i] = instancesForTask.get(i).getDate().toString();
        }

        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Task dates")
                .setItems(dates, (dialog, which) -> {
                    TaskInstanceDTO clickedInstance = instancesForTask.get(which);
                    Intent intent = new Intent(getContext(), TaskDetailActivity.class);
                    intent.putExtra(TaskDetailActivity.EXTRA_TASK, clickedInstance);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}