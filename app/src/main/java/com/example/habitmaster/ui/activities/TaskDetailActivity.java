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
import com.example.habitmaster.services.ICallback;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.utils.Prefs;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "extra_task";
    TextView nameText, descriptionText, categoryText, difficultyText, importanceText, startDateText, endDateText, frequencyText, xpText;
    Prefs prefs;
    private EditText editName, editDescription;
    private Spinner spinnerDifficulty, spinnerImportance;
    private LinearLayout editExecutionTimeLayout, bottomButtonsLayouts;
    private Button btnEdit, btnEditExecutionTime, btnSave, btnCancelEdit, btnDelete, btnPause, btnDone, btnCancelTask;
    private LocalTime executionTime;
    private TaskService taskService;
    private String taskId;
    private TaskInstanceDTO task;
    private TextView taskEndedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);

        taskService = new TaskService(this);
        task = (TaskInstanceDTO) getIntent().getSerializableExtra(EXTRA_TASK);
        taskId = task.getTaskId();

        if (task == null) {
            finish();
            return;
        }

        prefs = new Prefs(this);

        nameText = findViewById(R.id.textTaskName);
        descriptionText = findViewById(R.id.textTaskDescription);
        categoryText = findViewById(R.id.textTaskCategory);
        difficultyText = findViewById(R.id.textTaskDifficulty);
        importanceText = findViewById(R.id.textTaskImportance);
        startDateText = findViewById(R.id.textTaskStartDate);
        endDateText = findViewById(R.id.textTaskEndDate);
        frequencyText = findViewById(R.id.textTaskFrequency);
        xpText = findViewById(R.id.textTaskXp);
        btnPause = findViewById(R.id.btnPause);
        taskEndedText = findViewById(R.id.textTaskEnded);

        Log.d("Task != null", "Task is not null");
        nameText.setText(task.getName());
        descriptionText.setText("Description: " + task.getDescription());
        categoryText.setText("Category: " + task.getCategory().getName());
        difficultyText.setText("Difficulty: " + task.getDifficulty().getDisplayName());
        importanceText.setText("Importance: " + task.getImportance().getDisplayName());
        startDateText.setText("Start: " + (task.getDate() != null ? task.getDate().toString() : "-"));
//        endDateText.setText("End: " + (task.getEndDate() != null ? task.getEndDate().toString() : "-"));
        frequencyText.setText("Frequency: " + task.getFrequency().name());
        xpText.setText("XP: " + task.getXpValue());

        btnPause.setText(task.getStatus() == TaskStatus.PAUSED ? "Resume" : "Pause");

        btnEdit = findViewById(R.id.btnEdit);
        btnSave = findViewById(R.id.btnSave);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnDone = findViewById(R.id.btnDone);
        btnCancelTask = findViewById(R.id.btnCancelTask);
        editName = findViewById(R.id.editTaskName);
        editDescription = findViewById(R.id.editTaskDescription);
        btnEditExecutionTime = findViewById(R.id.btnEditTaskExecutionTime);
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty);
        spinnerImportance = findViewById(R.id.spinnerImportance);
        editExecutionTimeLayout = findViewById(R.id.editExecutionTimeLayout);
        bottomButtonsLayouts = findViewById(R.id.layoutSaveAndCancelEditTask);

        spinnerDifficulty.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TaskDifficulty.values()));
        spinnerImportance.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TaskImportance.values()));

        updateButtonsVisibility();

        btnEditExecutionTime.setOnClickListener(view -> {
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            int minute = currentTime.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (v, hourOfDay, minuteOfHour) -> {
                executionTime = LocalTime.of(hourOfDay, minuteOfHour);
                btnEditExecutionTime.setText(executionTime.format(DateTimeFormatter.ofPattern("HH:mm")));
            }, hour, minute, true);

            timePickerDialog.show();
        });

        btnEdit.setOnClickListener(v -> enableEditing());
        btnSave.setOnClickListener(v -> saveEdits());
        btnCancelEdit.setOnClickListener(v -> cancelEditing());
        btnDelete.setOnClickListener(v -> confirmDeleteTask());
        btnPause.setOnClickListener(v -> pauseTask());
        btnDone.setOnClickListener(v -> completeTask());
        btnCancelTask.setOnClickListener(v -> cancelTask());
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

        btnEdit.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        showStaticFields(false);
        showEditFields(true);
    }

    private void saveEdits() {
        btnEdit.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        if (task == null) return;

        task.setName(editName.getText().toString());
        task.setDescription(editDescription.getText().toString());
        if (executionTime != null) {
            task.setExecutionTime(LocalTime.parse(executionTime.toString()));
        }
        task.setDifficulty(TaskDifficulty.values()[spinnerDifficulty.getSelectedItemPosition()]);
        task.setImportance(TaskImportance.values()[spinnerImportance.getSelectedItemPosition()]);

        taskService.updateTaskInfo(task, new ICallback<TaskInstanceDTO>() {
            @Override
            public void onSuccess(TaskInstanceDTO result) {
                // Ensure UI updates happen on main thread
                runOnUiThread(() -> {
                    showEditFields(false);

                    nameText.setText(task.getName());
                    descriptionText.setText("Description: " + task.getDescription());
                    difficultyText.setText("Difficulty: " + task.getDifficulty().getDisplayName());
                    importanceText.setText("Importance: " + task.getImportance().getDisplayName());
                    xpText.setText("XP: " + task.getXpValue());

                    showStaticFields(true);

                    Toast.makeText(TaskDetailActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(TaskDetailActivity.this, "Failed to update task: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void cancelEditing() {
        btnEdit.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        showEditFields(false);
        showStaticFields(true);
    }

    private void showEditFields(boolean show) {
        editName.setVisibility(show ? View.VISIBLE : View.GONE);
        editDescription.setVisibility(show ? View.VISIBLE : View.GONE);
        editExecutionTimeLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        bottomButtonsLayouts.setVisibility(show ? View.VISIBLE : View.GONE);
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
        new AlertDialog.Builder(this).setTitle("Delete Task").setMessage("Are you sure you want to delete this task?").setPositiveButton("Yes", (dialog, which) -> deleteTask()).setNegativeButton("No", null).show();
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

    private void pauseTask() {
        if (task == null) return;

        Log.d("Pause task", "Attempting to pause");
        var status = task.getStatus();
        if (status == TaskStatus.ACTIVE) {
            status = TaskStatus.PAUSED;
        } else if (status == TaskStatus.PAUSED) {
            status = TaskStatus.ACTIVE;
        }

        TaskStatus newStatus = status;
        taskService.updateTaskStatus(prefs.getUid(), task.getId(), newStatus, new TaskService.Callback() {
            @Override
            public void onSuccess() {
                Log.d("Pause success", "Attempting to pause");
                task.setStatus(newStatus);
                btnPause.setText(newStatus == TaskStatus.PAUSED ? "Resume" : "Pause");
                String toastMessage = newStatus == TaskStatus.PAUSED ? "paused" : "resumed";
                Toast.makeText(TaskDetailActivity.this, "Task " + toastMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String errorMessage) {
                Log.d("Pause error", "Attempting to pause");
                String toastMessage = newStatus == TaskStatus.PAUSED ? "pause" : "resume";
                Toast.makeText(TaskDetailActivity.this, "Failed to " + toastMessage + " task: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelTask() {
        if (task == null) return;

        taskService.updateTaskStatus(prefs.getUid(), task.getId(), TaskStatus.CANCELLED, new TaskService.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TaskDetailActivity.this, "Task cancelled", Toast.LENGTH_SHORT).show();
                task.setStatus(TaskStatus.CANCELLED);
                updateButtonsVisibility();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(TaskDetailActivity.this, "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeTask() {
        if (task == null) return;

        Log.d("COMPLETE TASK", "completeTask: " + task.getId());
        taskService.completeTask(prefs.getUid(), task, new TaskService.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TaskDetailActivity.this, "Task completed", Toast.LENGTH_SHORT).show();
                task.setStatus(TaskStatus.COMPLETED);
                updateButtonsVisibility();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(TaskDetailActivity.this, "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonsVisibility() {
        Log.d("TASK INSTANCE STATUS", task.getStatus().name());
        if (task.getStatus() == TaskStatus.CANCELLED || task.getStatus() == TaskStatus.COMPLETED || task.getStatus() == TaskStatus.MISSED) {
            btnDone.setVisibility(View.GONE);
            btnPause.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnCancelTask.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            taskEndedText.setText(task.getStatus().name());
            taskEndedText.setVisibility(View.VISIBLE);
        } else {
            btnDone.setVisibility(View.VISIBLE);
            btnPause.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnCancelTask.setVisibility(View.VISIBLE);
            taskEndedText.setVisibility(View.GONE);
        }
    }

}