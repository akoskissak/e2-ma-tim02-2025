package com.example.habitmaster.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.example.habitmaster.R;
import com.example.habitmaster.data.dtos.TaskInstanceDTO;
import com.example.habitmaster.services.TaskService;
import com.example.habitmaster.ui.activities.TaskDetailActivity;
import com.example.habitmaster.ui.adapters.TasksAdapter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskCalendarFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private TasksAdapter tasksAdapter;
    private List<TaskInstanceDTO> allTasks = new ArrayList<>();
    private List<TaskInstanceDTO> filteredTasks = new ArrayList<>();
    private TaskService taskService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_calendar, container, false);

        calendarView = view.findViewById(R.id.materialCalendarView);
        recyclerView = view.findViewById(R.id.recyclerViewTasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new TasksAdapter(filteredTasks, task -> {
            Intent intent = new Intent(getContext(), TaskDetailActivity.class);
            intent.putExtra(TaskDetailActivity.EXTRA_TASK, task);
            startActivity(intent);
        });
        recyclerView.setAdapter(tasksAdapter);

        taskService = new TaskService(getContext());

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();
            LocalDate selectedDate = LocalDate.of(
                    clickedDayCalendar.get(Calendar.YEAR),
                    clickedDayCalendar.get(Calendar.MONTH) + 1,
                    clickedDayCalendar.get(Calendar.DAY_OF_MONTH)
            );
            filterTasksByDate(selectedDate);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        new Thread(() -> {
            allTasks = taskService.getAllTasksInstances();
            requireActivity().runOnUiThread(() -> {
                LocalDate today = LocalDate.now();
                filterTasksByDate(today);
                addTasksToCalendar();
            });
        }).start();
    }

    private void filterTasksByDate(LocalDate date) {
        filteredTasks.clear();
        for (TaskInstanceDTO task : allTasks) {
            LocalDate taskDate = task.getDate();
            if (taskDate != null && taskDate.isEqual(date)) {
                filteredTasks.add(task);
            }
        }
        tasksAdapter.notifyDataSetChanged();
    }

    private void addTasksToCalendar() {
        Map<LocalDate, List<TaskInstanceDTO>> tasksByDate = new HashMap<>();
        for (TaskInstanceDTO task : allTasks) {
            LocalDate date = task.getDate();
            if (date != null) {
                tasksByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(task);
            }
        }

        List<CalendarDay> calendarDays = new ArrayList<>();
        for (Map.Entry<LocalDate, List<TaskInstanceDTO>> entry : tasksByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<TaskInstanceDTO> tasksForDay = entry.getValue();

            Calendar cal = Calendar.getInstance();
            cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

            List<Drawable> drawables = new ArrayList<>();
            for (int i = 0; i < Math.min(tasksForDay.size(), 3); i++) {
                TaskInstanceDTO task = tasksForDay.get(i);
                int color = task.getCategory().getColor(); // Assuming TaskInstanceDTO has categoryColor
                Drawable circle = createColoredCircleDrawable(color, 48); // 48px diameter
                drawables.add(circle);
            }
            if (tasksForDay.size() > 3) {
                drawables.add(ContextCompat.getDrawable(requireContext(), R.drawable.ic_plus));
            }

            Drawable combinedDrawable = combineIcons(requireContext(), drawables);
            CalendarDay day = new CalendarDay(cal);
            day.setImageDrawable(combinedDrawable);
            calendarDays.add(day);
        }

        calendarView.setCalendarDays(calendarDays);
    }

    private Drawable createColoredCircleDrawable(int color, int sizePx) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.setIntrinsicWidth(sizePx);
        drawable.setIntrinsicHeight(sizePx);
        drawable.getPaint().setColor(color);
        drawable.getPaint().setStyle(Paint.Style.FILL);
        return drawable;
    }

    private Drawable combineIcons(Context context, List<Drawable> drawables) {
        if (drawables.isEmpty()) return null;

        int circleSize = 48; // diameter of each circle in pixels
        int spacing = 8;     // spacing between circles

        int rowCount = 2;
        int colCount = 2; // max 2 items per row
        int width = colCount * circleSize + spacing * (colCount - 1);
        int height = rowCount * circleSize + spacing; // spacing between rows

        Bitmap combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);

        // Row 1: first 2 tasks
        for (int i = 0; i < Math.min(drawables.size(), colCount); i++) {
            Drawable d = drawables.get(i);
            int left = i * (circleSize + spacing);
            int top = 0;
            d.setBounds(left, top, left + circleSize, top + circleSize);
            d.draw(canvas);
        }

        // Row 2: 3rd task + plus sign if needed
        if (drawables.size() >= 3) {
            Drawable d = drawables.get(2);
            int left = 0;
            int top = circleSize + spacing;
            d.setBounds(left, top, left + circleSize, top + circleSize);
            d.draw(canvas);

            if (drawables.size() > 3) {
                String extraText = "+" + (drawables.size() - 3);
                Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(circleSize * 0.9f);
                textPaint.setTextAlign(Paint.Align.CENTER);
                textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                float x = circleSize + spacing + circleSize / 2f;
                float y = circleSize + spacing + circleSize / 2f - ((textPaint.descent() + textPaint.ascent()) / 2);

                canvas.drawText(extraText, x, y, textPaint);
            }
        }

        return new BitmapDrawable(context.getResources(), combinedBitmap);
    }

}
