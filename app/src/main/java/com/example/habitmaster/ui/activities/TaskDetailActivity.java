package com.example.habitmaster.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.services.TaskService;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";

    private TaskService taskService;
    private String taskId;
    private TaskInstanceDTO task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);

        taskService = new TaskService(this);
        taskId = getIntent().getStringExtra(EXTRA_TASK_ID);

        if (taskId == null) {
            finish();
            return;
        }

        TextView nameText = findViewById(R.id.textTaskName);
        TextView descriptionText = findViewById(R.id.textTaskDescription);
        TextView categoryText = findViewById(R.id.textTaskCategory);
        TextView difficultyText = findViewById(R.id.textTaskDifficulty);
        TextView importanceText = findViewById(R.id.textTaskImportance);
        TextView startDateText = findViewById(R.id.textTaskStartDate);
        TextView endDateText = findViewById(R.id.textTaskEndDate);
        TextView frequencyText = findViewById(R.id.textTaskFrequency);
        TextView xpText = findViewById(R.id.textTaskXp);

        new Thread(() -> {
            task = taskService.getTaskById(taskId);
            runOnUiThread(() -> {
                if (task != null) {
                    Log.d("Task != null", "Task is not null");
                    nameText.setText(task.getName());
                    descriptionText.setText("Description: " + task.getDescription());
                    categoryText.setText("Category: " + task.getCategoryId());
                    difficultyText.setText("Difficulty: " + task.getDifficulty().name());
                    importanceText.setText("Importance: " + task.getImportance().name());
                    startDateText.setText("Start: " + (task.getDate() != null ? task.getDate().toString() : "-"));
//                    endDateText.setText("End: " + (task.getEndDate() != null ? task.getEndDate().toString() : "-"));
                    frequencyText.setText("Frequency: " + task.getFrequency().name());
                    xpText.setText("XP: " + task.getXpValue());
                } else {
                    Log.d("Task == null", "Task is null");
                }
            });
        }).start();
    }
}