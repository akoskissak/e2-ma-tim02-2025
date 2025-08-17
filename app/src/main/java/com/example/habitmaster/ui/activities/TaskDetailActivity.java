package com.example.habitmaster.ui.activities;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.TaskImportance;
import com.example.habitmaster.domain.models.TaskStatus;
import com.example.habitmaster.services.TaskService;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskDetailActivity extends AppCompatActivity {

    private EditText editName, editDescription;
    private Spinner spinnerDifficulty, spinnerImportance;
    private LinearLayout editExecutionTimeLayout, bottomButtonslayouts;
    private Button btnEdit, btnEditExecutionTime, btnSave, btnCancelEdit, btnDelete;
    private LocalTime executionTime;
    TextView nameText, descriptionText, categoryText, difficultyText, importanceText, startDateText, endDateText, frequencyText, xpText;
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

        nameText = findViewById(R.id.textTaskName);
        descriptionText = findViewById(R.id.textTaskDescription);
        categoryText = findViewById(R.id.textTaskCategory);
        difficultyText = findViewById(R.id.textTaskDifficulty);
        importanceText = findViewById(R.id.textTaskImportance);
        startDateText = findViewById(R.id.textTaskStartDate);
        endDateText = findViewById(R.id.textTaskEndDate);
        frequencyText = findViewById(R.id.textTaskFrequency);
        xpText = findViewById(R.id.textTaskXp);

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

        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnDelete = findViewById(R.id.btnDelete);
        editName = findViewById(R.id.editTaskName);
        editDescription = findViewById(R.id.editTaskDescription);
        btnEditExecutionTime = findViewById(R.id.btnEditTaskExecutionTime);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        spinnerImportance = findViewById(R.id.spinnerImportance);
        editExecutionTimeLayout = findViewById(R.id.editExecutionTimeLayout);
        bottomButtonslayouts = findViewById(R.id.layoutSaveAndCancelEditTask);

        spinnerDifficulty.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                TaskDifficulty.values()));
        spinnerImportance.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                TaskImportance.values()));

        btnEditExecutionTime.setOnClickListener(view -> {
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            int minute = currentTime.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (v, hourOfDay, minuteOfHour) -> {
                        executionTime = LocalTime.of(hourOfDay, minuteOfHour);
                        btnEditExecutionTime.setText(executionTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    },
                    hour,
                    minute,
                    true
            );

            timePickerDialog.show();
        });

        btnEdit.setOnClickListener(v -> enableEditing());
        btnSave.setOnClickListener(v -> saveEdits());
        btnCancelEdit.setOnClickListener(v -> cancelEditing());
        btnDelete.setOnClickListener(v -> confirmDeleteTask());
    }

    private void enableEditing() {
        if (task == null) return;

        // Only allow editing if task is not done, canceled, or past date
        if (task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.CANCELLED) {
            Toast.makeText(this, "Cannot edit finished or canceled tasks", Toast.LENGTH_SHORT).show();
            return;
        }

        editName.setText(task.getName());
        editDescription.setText(task.getDescription());

        spinnerDifficulty.setSelection(task.getDifficulty().ordinal());
        spinnerImportance.setSelection(task.getImportance().ordinal());

        showStaticFields(false);
        showEditFields(true);
    }

    private void saveEdits() {
        if (task == null) return;

        task.setName(editName.getText().toString());
        task.setDescription(editDescription.getText().toString());
        if (executionTime != null) {
            task.setExecutionTime(LocalTime.parse(executionTime.toString()));
        }
        task.setDifficulty(TaskDifficulty.values()[spinnerDifficulty.getSelectedItemPosition()]);
        task.setImportance(TaskImportance.values()[spinnerImportance.getSelectedItemPosition()]);

        task.setXpValue(task.getDifficulty().getXpValue() + task.getImportance().getXpValue());

        new Thread(() -> {
            taskService.updateTask(task);

            runOnUiThread(() -> {
                showEditFields(false);

                nameText.setText(task.getName());
                descriptionText.setText("Description: " + task.getDescription());
                difficultyText.setText("Difficulty: " + task.getDifficulty().name());
                importanceText.setText("Importance: " + task.getImportance().name());
                xpText.setText("XP: " + task.getXpValue());

                showStaticFields(true);

                Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void cancelEditing() {
        showEditFields(false);
        showStaticFields(true);
    }

    private void showEditFields(boolean show) {
        editName.setVisibility(show ? View.VISIBLE : View.GONE);
        editDescription.setVisibility(show ? View.VISIBLE : View.GONE);
        editExecutionTimeLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        bottomButtonslayouts.setVisibility(show ? View.VISIBLE : View.GONE);
        spinnerDifficulty.setVisibility(show ? View.VISIBLE : View.GONE);
        spinnerImportance.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showStaticFields(boolean show) {
        nameText.setVisibility(show ? View.VISIBLE : View.GONE);
        descriptionText.setVisibility(show ? View.VISIBLE : View.GONE);
        difficultyText.setVisibility(show ? View.VISIBLE : View.GONE);
        importanceText.setVisibility(show ? View.VISIBLE : View.GONE);
        xpText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void confirmDeleteTask() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes", (dialog, which) -> deleteTask())
            .setNegativeButton("No", null)
            .show();
    }

    private void deleteTask() {
        if (task == null) return;

        taskService.deleteTask(taskId, new TaskService.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TaskDetailActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after deletion
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(TaskDetailActivity.this, "Failed to delete task: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}