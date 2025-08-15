package com.example.habitmaster.ui.fragments;

import android.content.Context;
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
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.services.TaskService;
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
    private List<Task> allTasks = new ArrayList<>();
    private List<Task> filteredTasks = new ArrayList<>();
    private TaskService taskService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_calendar, container, false);

        calendarView = view.findViewById(R.id.materialCalendarView);
        recyclerView = view.findViewById(R.id.recyclerViewTasks);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tasksAdapter = new TasksAdapter(filteredTasks);
        recyclerView.setAdapter(tasksAdapter);

        taskService = new TaskService(getContext());
        loadTasksFromDatabase();

        calendarView.setOnDayClickListener(eventDay -> {
            Calendar clickedDayCalendar = eventDay.getCalendar();

            LocalDate selectedDate = LocalDate.of(
                clickedDayCalendar.get(Calendar.YEAR),
                clickedDayCalendar.get(Calendar.MONTH) + 1, // Month is 0-based
                clickedDayCalendar.get(Calendar.DAY_OF_MONTH)
            );

            filterTasksByDate(selectedDate);
        });


        return view;
    }

    private void loadTasksFromDatabase() {
        new Thread(() -> {
            allTasks = taskService.getAllTasks();
            requireActivity().runOnUiThread(() -> {
                LocalDate today = LocalDate.now();
                filterTasksByDate(today);

                AddTasksToCalendar();
            });
        }).start();
    }

    private void filterTasksByDate(LocalDate date) {
        filteredTasks.clear();
        for (Task task : allTasks) {
            LocalDate start = task.getStartDate();
            LocalDate end = task.getEndDate();

            if (start == null || end == null) {
                continue; // skip incomplete tasks
            }

            if ((date.isEqual(start) || date.isAfter(start)) &&
                    (date.isEqual(end) || date.isBefore(end))) {
                filteredTasks.add(task);
            }
        }
        tasksAdapter.notifyDataSetChanged();
    }

    private void AddTasksToCalendar() {
        Map<LocalDate, List<Task>> tasksByDate = new HashMap<>();
        for (Task task : allTasks) {
            if (task.getStartDate() != null) {
                LocalDate start = task.getStartDate();
                LocalDate end = (task.getEndDate() != null) ? task.getEndDate() : start;

                // Ensure start is before or equal to end
                if (end.isBefore(start)) {
                    LocalDate temp = start;
                    start = end;
                    end = temp;
                }

                // Add the task to every date in the range [start, end]
                LocalDate current = start;
                while (!current.isAfter(end)) {
                    tasksByDate
                        .computeIfAbsent(current, k -> new ArrayList<>())
                        .add(task);

                    current = current.plusDays(1);
                }
            }
        }

        List<CalendarDay> calendarDays = new ArrayList<>();

        for (Map.Entry<LocalDate, List<Task>> entry : tasksByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Task> tasksForDay = entry.getValue();

            Calendar cal = Calendar.getInstance();
            cal.set(date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

            // Build the icon list
            List<Integer> icons = new ArrayList<>();
            for (int i = 0; i < Math.min(tasksForDay.size(), 3); i++) {
                // TODO: Change avatar1 to corresponding color of category
                icons.add(R.drawable.avatar1);
            }

            // If day contains 4 or more tasks show ic_plus drawable
            if (tasksForDay.size() > 3) {
                icons.add(R.drawable.ic_plus);
            }

            Drawable combinedDrawable = combineIcons(requireContext(), icons);
            CalendarDay day = new CalendarDay(cal);
            day.setImageDrawable(combinedDrawable);
            calendarDays.add(day);

        }

        com.applandeo.materialcalendarview.CalendarView calendarView2 =
            requireView().findViewById(R.id.materialCalendarView);
        calendarView2.setCalendarDays(calendarDays);
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