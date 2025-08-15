package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.TaskFrequency;
import com.example.habitmaster.domain.models.TaskImportance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskRepository {
    private final DatabaseHelper dbHelper;

    public TaskRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public int getTasksCountByDifficultyAndImportance(@NonNull Enum difficulty, @NonNull Enum importance) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM tasks WHERE difficulty=? AND importance=?";
        Cursor cursor = db.rawQuery(query, new String[]{difficulty.name(), importance.name()});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }


    public void insert(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", task.getId());
        values.put("userId", task.getUserId());
        values.put("name", task.getName());
        values.put("description", task.getDescription());
        values.put("categoryId", task.getCategoryId());
        values.put("frequency", task.getFrequency().toString());
        values.put("repeatInterval", task.getRepeatInterval());
        if (task.getStartDate() != null) {
            values.put("startDate", task.getStartDate().toString());
        } else {
            values.putNull("startDate");
        }
        if (task.getEndDate() != null) {
            values.put("endDate", task.getEndDate().toString());
        } else {
            values.putNull("endDate");
        }
        values.put("difficulty", task.getDifficulty().name());
        values.put("importance", task.getImportance().name());
        values.put("xpValue", task.getXpValue());
        db.insert("tasks", null, values);
        db.close();
    }

    public List<Task> getAllUserTasks(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();

        String selection = "userId = ?";
        String[] selectionArgs = { userId };

        Cursor cursor = db.query(
                "tasks",
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = mapCursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return tasks;
    }


    public List<Task> getOneTimeUserTasks(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();

        String selection = "userId = ? AND frequency = ?";
        String[] selectionArgs = { userId, TaskFrequency.ONCE.name() };

        Cursor cursor = db.query(
                "tasks",
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = mapCursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    public List<Task> getRepeatingUserTasks(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Task> tasks = new ArrayList<>();

        String selection = "userId = ? AND frequency != ?";
        String[] selectionArgs = { userId, TaskFrequency.ONCE.name() };

        Cursor cursor = db.query(
                "tasks",
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = mapCursorToTask(cursor);
                tasks.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return tasks;
    }

    private Task mapCursorToTask(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        task.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        task.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
        task.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("categoryId")));
        task.setFrequency(TaskFrequency.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("frequency"))));
        task.setRepeatInterval(cursor.getInt(cursor.getColumnIndexOrThrow("repeatInterval")));

        String startDateStr = cursor.getString(cursor.getColumnIndexOrThrow("startDate"));
        task.setStartDate(startDateStr != null ? LocalDate.parse(startDateStr) : null);

        String endDateStr = cursor.getString(cursor.getColumnIndexOrThrow("endDate"));
        task.setEndDate(endDateStr != null ? LocalDate.parse(endDateStr) : null);

        task.setDifficulty(TaskDifficulty.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("difficulty"))));
        task.setImportance(TaskImportance.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("importance"))));
        task.setXpValue(cursor.getInt(cursor.getColumnIndexOrThrow("xpValue")));

        return task;
    }
}
