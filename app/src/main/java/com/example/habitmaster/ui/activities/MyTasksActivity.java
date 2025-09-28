package com.example.habitmaster.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.habitmaster.R;
import com.example.habitmaster.services.CategoryService;
import com.example.habitmaster.ui.fragments.TaskCalendarFragment;
import com.example.habitmaster.ui.fragments.TaskListFragment;

public class MyTasksActivity extends AppCompatActivity {
    private static final String EXTRA_USER_ID = "extra_user_id";
    private Button btnNewTask, btnCategories;
    private Button btnCalendarView, btnListView;
    private int mostRecentFragment; // 0 = Calendar, 1 = List
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_tasks);

        userId = getIntent().getStringExtra(EXTRA_USER_ID);

        mostRecentFragment = 0;
        btnCalendarView = findViewById(R.id.btnCalendarView);

        btnListView = findViewById(R.id.btnListView);

        btnCategories = findViewById(R.id.btnCategories);
        btnCategories.setOnClickListener(v -> startActivity(new Intent(MyTasksActivity.this, CategoriesActivity.class)));

        btnNewTask = findViewById(R.id.btnNewTask);
        btnNewTask.setOnClickListener(view -> {
            if (checkIfCategoryExist()) {
                startActivity(new Intent(MyTasksActivity.this, CreateTaskActivity.class));
            } else {
                Toast.makeText(this, "Create category first", Toast.LENGTH_SHORT).show();
            }
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

    private boolean checkIfCategoryExist() {
        CategoryService service = new CategoryService(this);
        return service.existUserCategory(userId);
    }
}