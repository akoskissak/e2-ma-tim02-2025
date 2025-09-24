package com.example.habitmaster.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import com.example.habitmaster.domain.models.Category;
import com.example.habitmaster.services.CategoryService;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.utils.HorizontalNumberPicker;
import com.example.habitmaster.utils.Prefs;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.util.Log;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText taskNameEdit, taskDescEdit;
    private Spinner categorySpinner, difficultySpinner, importanceSpinner, repeatFrequencySpinner;
    private RadioGroup repeatRadioGroup;
    private LinearLayout taskDatePickerLayout, oneTimeDatePickerLayout;
    private Button startDateButton, endDateButton, oneTimeDateButton, btnCreateTask, btnTaskExecutionTime;
    private HorizontalNumberPicker repeatingIntervalNumberPicker;

    private int startYear, startMonth, startDay;
    private int endYear, endMonth, endDay;
    private LocalTime executionTime;
    private boolean isRepeating;

    private TaskService taskService;
    private CategoryService categoryService;
    private List<Category> myCategories;

    private Prefs prefs;

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

        prefs = new Prefs(this);

        taskNameEdit = findViewById(R.id.taskName);
        taskDescEdit = findViewById(R.id.taskDescription);
        categorySpinner = findViewById(R.id.categorySpinner);
        difficultySpinner = findViewById(R.id.difficultySpinner);
        importanceSpinner = findViewById(R.id.importanceSpinner);
        repeatRadioGroup = findViewById(R.id.radio_group_create_task);
        taskDatePickerLayout = findViewById(R.id.taskDatePickerLayout);
        oneTimeDatePickerLayout = findViewById(R.id.taskOneTimeDatePickerLayout);
        startDateButton = findViewById(R.id.startDateButton);
        endDateButton = findViewById(R.id.endDateButton);
        oneTimeDateButton = findViewById(R.id.btnOneTimeDate);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnTaskExecutionTime = findViewById(R.id.btnSelectTaskExecutionTime);
        repeatFrequencySpinner = findViewById(R.id.taskFrequencySpinner);
        repeatingIntervalNumberPicker = findViewById(R.id.repeatingIntervalNumberPicker);

        taskDatePickerLayout.setVisibility(LinearLayout.GONE);
        oneTimeDatePickerLayout.setVisibility(LinearLayout.GONE);

        Calendar calendar = Calendar.getInstance();
        startYear = endYear = calendar.get(Calendar.YEAR);
        startMonth = endMonth = calendar.get(Calendar.MONTH);
        startDay = endDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Set button text to current date initially
        startDateButton.setText((startMonth + 1) + "/" + startDay + "/" + startYear);
        endDateButton.setText((endMonth + 1) + "/" + endDay + "/" + endYear);
        oneTimeDateButton.setText((startMonth + 1) + "/" + startDay + "/" + startYear);

        taskService = new TaskService(this);

        repeatRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton selectedRadio = findViewById(checkedId);
                if (selectedRadio.getText().toString().equals("Repeating")) {
                    oneTimeDatePickerLayout.setVisibility(View.GONE);
                    taskDatePickerLayout.setVisibility(View.VISIBLE);
                } else {
                    taskDatePickerLayout.setVisibility(View.GONE);
                    oneTimeDatePickerLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        populateCategorySpinner();

        startDateButton.setOnClickListener(view -> showDatePicker(true));
        endDateButton.setOnClickListener(view -> showDatePicker(false));
        oneTimeDateButton.setOnClickListener(view -> showDatePicker(true));

        btnTaskExecutionTime.setOnClickListener(view -> {
            LocalTime currentTime = LocalTime.now();
            int hour = currentTime.getHour();
            int minute = currentTime.getMinute();

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (v, hourOfDay, minuteOfHour) -> {
                        executionTime = LocalTime.of(hourOfDay, minuteOfHour);
                        btnTaskExecutionTime.setText(executionTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                    },
                    hour,
                    minute,
                    true
            );

            timePickerDialog.show();
        });

        btnCreateTask.setOnClickListener(view -> createTask());
    }

    private void populateCategorySpinner() {
        categoryService = new CategoryService(this);
        myCategories = categoryService.getUserCategories(prefs.getUid());
        List<String> categoryNames = new ArrayList<>();
        for (Category c : myCategories) {
            categoryNames.add(c.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
    }

    private void showDatePicker(boolean isStart) {
        int year = isStart ? startYear : endYear;
        int month = isStart ? startMonth : endMonth;
        int day = isStart ? startDay : endDay;

        isRepeating = repeatRadioGroup.getCheckedRadioButtonId() != -1 &&
                ((RadioButton) findViewById(repeatRadioGroup.getCheckedRadioButtonId()))
                        .getText().toString().equals("Repeating");

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                CreateTaskActivity.this,
                (view, y, m, d) -> {
                    if (isStart) {
                        startYear = y; startMonth = m; startDay = d;
                        if (isRepeating) {
                            startDateButton.setText((m + 1) + "/" + d + "/" + y);
                        } else {
                            oneTimeDateButton.setText((m + 1) + "/" + d + "/" + y);
                        }

                        // Ako je endDate već pre startDate, postavi ga na startDate
                        Calendar startCal = Calendar.getInstance();
                        startCal.set(startYear, startMonth, startDay);
                        Calendar endCal = Calendar.getInstance();
                        endCal.set(endYear, endMonth, endDay);

                        if (endCal.before(startCal)) {
                            endYear = startYear;
                            endMonth = startMonth;
                            endDay = startDay;
                            endDateButton.setText((endMonth + 1) + "/" + endDay + "/" + endYear);
                        }

                    } else {
                        endYear = y; endMonth = m; endDay = d;
                        endDateButton.setText((m + 1) + "/" + d + "/" + y);
                    }
                }, year, month, day
        );

        // Spreči odabir datuma u prošlosti
        Calendar minDate = Calendar.getInstance(); // danas
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        // Ako biramo endDate, setuj minimalni datum na startDate
        if (!isStart) {
            Calendar startDateCal = Calendar.getInstance();
            startDateCal.set(startYear, startMonth, startDay);
            datePickerDialog.getDatePicker().setMinDate(startDateCal.getTimeInMillis());
        }

        datePickerDialog.show();
    }


    private void createTask() {
        String name = taskNameEdit.getText().toString().trim();
        String description = taskDescEdit.getText().toString().trim();
        int position = categorySpinner.getSelectedItemPosition();
        String categoryId = myCategories.get(position).getId();
        String difficulty = difficultySpinner.getSelectedItem().toString();
        String importance = importanceSpinner.getSelectedItem().toString();
        isRepeating = repeatRadioGroup.getCheckedRadioButtonId() != -1 &&
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
        if (executionTime == null) {
            executionTime = LocalTime.now();
        }
        String executionTimeStr = executionTime.format(DateTimeFormatter.ISO_LOCAL_TIME);

        Log.d("CreateTask", "startDate=" + startDate + ", endDate=" + endDate);
        taskService.createTask(name, description, categoryId, repeatingFrequency, repeatingInterval, startDate, endDate, executionTimeStr, difficulty, importance,
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