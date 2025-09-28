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

    public static final String EXTRA_TASK_INSTANCE_ID = "extra_task_instance_id";
    TextView nameText, descriptionText, categoryText, difficultyText, importanceText, startDateText, endDateText, frequencyText, xpText;
    Prefs prefs;
    private EditText editName, editDescription;
    private Spinner spinnerDifficulty, spinnerImportance;
    private LinearLayout editExecutionTimeLayout, bottomButtonsLayouts;
    private Button btnEdit, btnEditExecutionTime, btnSave, btnCancelEdit, btnDelete, btnPause, btnDone, btnCancelTask;
    private LocalTime executionTime;
    private TaskService taskService;
    private String taskInstanceId;
    private TaskInstanceDTO dto;
    private TextView taskEndedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);

        prefs = new Prefs(this);

        taskService = new TaskService(this);
        taskInstanceId = getIntent().getStringExtra(EXTRA_TASK_INSTANCE_ID);

        dto = taskService.getTaskInstanceByIdAndDate(taskInstanceId);
        if (dto == null) {
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
        btnPause = findViewById(R.id.btnPause);
        taskEndedText = findViewById(R.id.textTaskEnded);

        Log.d("Task != null", "Task is not null");
        nameText.setText(dto.getName());
        descriptionText.setText("Description: " + dto.getDescription());
        categoryText.setText("Category: " + dto.getCategory().getName());
        difficultyText.setText("Difficulty: " + dto.getDifficulty().getDisplayName());
        importanceText.setText("Importance: " + dto.getImportance().getDisplayName());
        startDateText.setText("Start: " + (dto.getDate() != null ? dto.getDate().toString() : "-"));
//        endDateText.setText("End: " + (task.getEndDate() != null ? task.getEndDate().toString() : "-"));
        frequencyText.setText("Frequency: " + dto.getFrequency().name());
        xpText.setText("XP: " + dto.getXpValue());

        btnPause.setText(dto.getStatus() == TaskStatus.PAUSED ? "Resume" : "Pause");

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
        if (dto == null) return;

        // Only allow editing if task is not done, canceled, or past date
        if (dto.getStatus() == TaskStatus.COMPLETED || dto.getStatus() == TaskStatus.CANCELLED) {
            Toast.makeText(this, "Cannot edit finished or canceled tasks", Toast.LENGTH_SHORT).show();
            return;
        }

        editName.setText(dto.getName());
        editDescription.setText(dto.getDescription());

        spinnerDifficulty.setSelection(dto.getDifficulty().ordinal());
        spinnerImportance.setSelection(dto.getImportance().ordinal());

        btnEdit.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);
        btnPause.setVisibility(View.GONE);
        btnCancelTask.setVisibility(View.GONE);

        showStaticFields(false);
        showEditFields(true);
    }

    private void saveEdits() {
        if (dto == null) return;

        dto.setName(editName.getText().toString());
        dto.setDescription(editDescription.getText().toString());
        if (executionTime != null) {
            dto.setExecutionTime(LocalTime.parse(executionTime.toString()));
        }
        dto.setDifficulty(TaskDifficulty.values()[spinnerDifficulty.getSelectedItemPosition()]);
        dto.setImportance(TaskImportance.values()[spinnerImportance.getSelectedItemPosition()]);

        taskService.updateTaskInfo(dto, new ICallback<TaskInstanceDTO>() {
            @Override
            public void onSuccess(TaskInstanceDTO result) {
                // Ensure UI updates happen on main thread
                runOnUiThread(() -> {
                    showEditFields(false);

                    nameText.setText(dto.getName());
                    descriptionText.setText("Description: " + dto.getDescription());
                    difficultyText.setText("Difficulty: " + dto.getDifficulty().getDisplayName());
                    importanceText.setText("Importance: " + dto.getImportance().getDisplayName());
                    xpText.setText("XP: " + dto.getXpValue());

                    showStaticFields(true);

                    Toast.makeText(TaskDetailActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
                    btnEdit.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDone.setVisibility(View.VISIBLE);
                    btnPause.setVisibility(View.VISIBLE);
                    btnCancelTask.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(TaskDetailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });

    }

    private void cancelEditing() {
        btnEdit.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        showEditFields(false);
        showStaticFields(true);

        btnDone.setVisibility(View.VISIBLE);
        btnPause.setVisibility(View.VISIBLE);
        btnCancelTask.setVisibility(View.VISIBLE);
    }

    private void showEditFields(boolean show) {
        editName.setVisibility(show ? View.VISIBLE : View.GONE);
        editDescription.setVisibility(show ? View.VISIBLE : View.GONE);
        editExecutionTimeLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        bottomButtonsLayouts.setVisibility(show ? View.VISIBLE : View.GONE);
        spinnerDifficulty.setVisibility(show ? View.VISIBLE : View.GONE);
        spinnerImportance.setVisibility(show ? View.VISIBLE : View.GONE);

        executionTime = LocalTime.now();
        btnEditExecutionTime.setText(dto.getExecutionTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void showStaticFields(boolean show) {
        nameText.setVisibility(show ? View.VISIBLE : View.GONE);
        descriptionText.setVisibility(show ? View.VISIBLE : View.GONE);
        difficultyText.setVisibility(show ? View.VISIBLE : View.GONE);
        importanceText.setVisibility(show ? View.VISIBLE : View.GONE);
        xpText.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void confirmDeleteTask() {
        new AlertDialog.Builder(this).setTitle("Delete Task").setMessage("Are you sure you want to delete this task?").setPositiveButton("Yes", (dialog, which) -> delete()).setNegativeButton("No", null).show();
    }

    private void delete() {
        if (dto == null) return;

        taskService.deleteTaskInstances(taskInstanceId, dto.getTaskId(), dto.getDate(), new TaskService.Callback() {
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
        if (dto == null) return;

        Log.d("Pause task", "Attempting to pause");
        var status = dto.getStatus();
        if (status == TaskStatus.ACTIVE) {
            status = TaskStatus.PAUSED;
        } else if (status == TaskStatus.PAUSED) {
            status = TaskStatus.ACTIVE;
        }

        TaskStatus newStatus = status;
        taskService.updateTaskStatus(prefs.getUid(), dto, newStatus, new TaskService.Callback() {
            @Override
            public void onSuccess() {
                Log.d("Pause success", "Attempting to pause");
                dto.setStatus(newStatus);
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
        if (dto == null) return;

        taskService.updateTaskStatus(prefs.getUid(), dto, TaskStatus.CANCELLED, new TaskService.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TaskDetailActivity.this, "Task cancelled", Toast.LENGTH_SHORT).show();
                dto.setStatus(TaskStatus.CANCELLED);
                updateButtonsVisibility();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(TaskDetailActivity.this, "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeTask() {
        if (dto == null) return;

        Log.d("COMPLETE TASK", "completeTask: " + dto.getId());
        taskService.completeTask(prefs.getUid(), dto, new TaskService.Callback() {
            @Override
            public void onSuccess() {
                Toast.makeText(TaskDetailActivity.this, "Task completed", Toast.LENGTH_SHORT).show();
                dto.setStatus(TaskStatus.COMPLETED);
                updateButtonsVisibility();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(TaskDetailActivity.this, "Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButtonsVisibility() {
        Log.d("TASK INSTANCE STATUS", dto.getStatus().name());
        if (dto.getStatus() == TaskStatus.CANCELLED || dto.getStatus() == TaskStatus.COMPLETED || dto.getStatus() == TaskStatus.MISSED) {
            btnDone.setVisibility(View.GONE);
            btnPause.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnCancelTask.setVisibility(View.GONE);
            btnEdit.setVisibility(View.GONE);
            taskEndedText.setText(dto.getStatus().name());
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