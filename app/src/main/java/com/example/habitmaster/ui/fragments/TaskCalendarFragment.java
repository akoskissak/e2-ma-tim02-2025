package com.example.habitmaster.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
            intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, task.getTaskId());
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
            allTasks = taskService.getAllTasks(); // Now returns List<TaskInstanceDTO>
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

            List<Integer> icons = new ArrayList<>();
            for (int i = 0; i < Math.min(tasksForDay.size(), 3); i++) {
                // TODO: replace with category color drawable
                icons.add(R.drawable.avatar1);
            }
            if (tasksForDay.size() > 3) {
                icons.add(R.drawable.ic_plus);
            }

            Drawable combinedDrawable = combineIcons(requireContext(), icons);
            CalendarDay day = new CalendarDay(cal);
            day.setImageDrawable(combinedDrawable);
            calendarDays.add(day);
        }

        calendarView.setCalendarDays(calendarDays);
    }

    private Drawable combineIcons(Context context, List<Integer> iconResIds) {
        int size = 48;
        Bitmap combined = Bitmap.createBitmap(size * iconResIds.size(), size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combined);

        for (int i = 0; i < iconResIds.size(); i++) {
            Drawable d = ContextCompat.getDrawable(context, iconResIds.get(i));
            d.setBounds(i * size, 0, (i + 1) * size, size);
            d.draw(canvas);
        }

        return new BitmapDrawable(context.getResources(), combined);
    }
}
