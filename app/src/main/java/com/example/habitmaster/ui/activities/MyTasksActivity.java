package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.habitmaster.R;
import com.example.habitmaster.ui.fragments.TaskCalendarFragment;
import com.example.habitmaster.ui.fragments.TaskListFragment;

public class MyTasksActivity extends AppCompatActivity {
    private Button btnNewTask;
    private Button btnCalendarView, btnListView;
    private int mostRecentFragment; // 0 = Calendar, 1 = List

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_tasks);

        mostRecentFragment = 0;
        btnCalendarView = findViewById(R.id.btnCalendarView);

        btnListView = findViewById(R.id.btnListView);

        btnNewTask = findViewById(R.id.btnNewTask);
        btnNewTask.setOnClickListener(view -> {
            startActivity(new Intent(MyTasksActivity.this, CreateTaskActivity.class));
        });

        btnCalendarView.setOnClickListener(v -> {
            mostRecentFragment = 0;
            loadFragment(new TaskCalendarFragment());
        });

        btnListView.setOnClickListener(v -> {
            mostRecentFragment = 1;
            loadFragment(TaskListFragment.newInstance());
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mostRecentFragment == 0) {
            loadFragment(new TaskCalendarFragment());
        } else {
            loadFragment(TaskListFragment.newInstance());
        }
    }
}