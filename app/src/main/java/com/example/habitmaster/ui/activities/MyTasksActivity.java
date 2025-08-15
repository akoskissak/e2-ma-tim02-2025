package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.habitmaster.R;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.ui.adapters.TasksAdapter;
import com.example.habitmaster.ui.fragments.TaskCalendarFragment;
import com.example.habitmaster.ui.fragments.TaskListFragment;

public class MyTasksActivity extends AppCompatActivity {
    private Button btnNewTask;
    private Button btnCalendarView, btnListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_tasks);

        btnCalendarView = findViewById(R.id.btnCalendarView);
        btnListView = findViewById(R.id.btnListView);

        btnNewTask = findViewById(R.id.btnNewTask);
        btnNewTask.setOnClickListener(view -> {
            Intent intent = new Intent(MyTasksActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });

        loadFragment(new TaskCalendarFragment());

        btnCalendarView.setOnClickListener(v -> loadFragment(new TaskCalendarFragment()));
        btnListView.setOnClickListener(v -> loadFragment(new TaskListFragment()));
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}