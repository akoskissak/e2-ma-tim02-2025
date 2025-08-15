package com.example.habitmaster.ui.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.habitmaster.R;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.utils.HorizontalNumberPicker;

import java.util.Calendar;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText taskNameEdit, taskDescEdit;
    private Spinner categorySpinner, difficultySpinner, importanceSpinner, repeatFrequencySpinner;
    private RadioGroup repeatRadioGroup;
    private LinearLayout taskDatePickerLayout;
    private Button startDateButton, endDateButton, btnCreateTask;
    private HorizontalNumberPicker repeatingIntervalNumberPicker;

    private int startYear, startMonth, startDay;
    private int endYear, endMonth, endDay;

    private TaskService taskService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_create_task), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        taskNameEdit = findViewById(R.id.taskName);
        taskDescEdit = findViewById(R.id.taskDescription);
        categorySpinner = findViewById(R.id.categorySpinner);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        importanceSpinner = findViewById(R.id.importanceSpinner);
        repeatRadioGroup = findViewById(R.id.radio_group_create_task);
        taskDatePickerLayout = findViewById(R.id.taskDatePickerLayout);
        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        repeatFrequencySpinner = findViewById(R.id.taskFrequencySpinner);
        repeatingIntervalNumberPicker = findViewById(R.id.repeatingIntervalNumberPicker);

        taskDatePickerLayout.setVisibility(LinearLayout.GONE);

        Calendar calendar = Calendar.getInstance();
        startYear = endYear = calendar.get(Calendar.YEAR);
        startMonth = endMonth = calendar.get(Calendar.MONTH);
        startDay = endDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Set button text to current date initially
        startDateButton.setText((startMonth + 1) + "/" + startDay + "/" + startYear);
        endDateButton.setText((endMonth + 1) + "/" + endDay + "/" + endYear);

        taskService = new TaskService(this);

        repeatRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadio = findViewById(checkedId);
                if (selectedRadio.getText().toString().equals("Repeating")) {
                    taskDatePickerLayout.setVisibility(View.VISIBLE);
                } else {
                    taskDatePickerLayout.setVisibility(View.GONE);
                }
            }
        });

        startDateButton.setOnClickListener(view -> showDatePicker(true));
        endDateButton.setOnClickListener(view -> showDatePicker(false));

        btnCreateTask.setOnClickListener(view -> createTask());
    }

    private void showDatePicker(boolean isStart) {
        int year = isStart ? startYear : endYear;
        int month = isStart ? startMonth : endMonth;
        int day = isStart ? startDay : endDay;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateTaskActivity.this,
                (view, y, m, d) -> {
                    if (isStart) {
                        startYear = y; startMonth = m; startDay = d;
                        startDateButton.setText((m+1) + "/" + d + "/" + y);
                    } else {
                        endYear = y; endMonth = m; endDay = d;
                        endDateButton.setText((m+1) + "/" + d + "/" + y);
                    }
                }, year, month, day
        );
        datePickerDialog.show();
    }

    private void createTask() {
        String name = taskNameEdit.getText().toString().trim();
        String description = taskDescEdit.getText().toString().trim();
        int categoryId = categorySpinner.getSelectedItemPosition();
        String difficulty = difficultySpinner.getSelectedItem().toString();
        String importance = importanceSpinner.getSelectedItem().toString();
        boolean isRepeating = repeatRadioGroup.getCheckedRadioButtonId() != -1 &&
                ((RadioButton) findViewById(repeatRadioGroup.getCheckedRadioButtonId()))
                        .getText().toString().equals("Repeating");

        int repeatingInterval;
        String repeatingFrequency;
        if (isRepeating) {
            repeatingInterval = repeatingIntervalNumberPicker.getValue();
            repeatingFrequency = repeatFrequencySpinner.getSelectedItem().toString();
        } else {
            repeatingInterval = 0;
            repeatingFrequency = "ONCE";
        }
        String startDate = String.format("%04d-%02d-%02d", startYear, startMonth + 1, startDay);
        String endDate = String.format("%04d-%02d-%02d", endYear, endMonth + 1, endDay);

        taskService.createTask(name, description, categoryId, repeatingFrequency, repeatingInterval, startDate, endDate, difficulty, importance,
            new TaskService.Callback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(CreateTaskActivity.this, "Task created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CreateTaskActivity.this, MyTasksActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(CreateTaskActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

        Toast.makeText(CreateTaskActivity.this, "Clicked on task button", Toast.LENGTH_SHORT).show();
    }
}