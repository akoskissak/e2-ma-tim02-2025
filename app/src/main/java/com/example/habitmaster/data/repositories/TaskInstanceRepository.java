package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.TaskInstance;
import com.example.habitmaster.domain.models.TaskStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TaskInstanceRepository {
    private final DatabaseHelper dbHelper;

    public TaskInstanceRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public void insert(TaskInstance instance) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", instance.getId());
        values.put("taskId", instance.getTaskId());
        values.put("date", instance.getDate().toString()); // ISO format YYYY-MM-DD
        values.put("status", instance.getStatus().name());
        db.insert("task_instances", null, values);
        db.close();
    }

    public List<TaskInstance> getByTaskIds(List<String> taskIds) {
        if (taskIds == null || taskIds.isEmpty()) {
            return new ArrayList<>();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TaskInstance> instances = new ArrayList<>();

        StringBuilder inClause = new StringBuilder();
        String[] selectionArgs = new String[taskIds.size()];
        for (int i = 0; i < taskIds.size(); i++) {
            inClause.append("?");
            if (i < taskIds.size() - 1) inClause.append(",");
            selectionArgs[i] = taskIds.get(i);
        }

        String selection = "taskId IN (" + inClause.toString() + ")";

        Cursor cursor = db.query(
                "task_instances",
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                TaskInstance instance = mapCursorToTaskInstance(cursor);
                instances.add(instance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return instances;
    }

    public List<TaskInstance> getByTaskIdsFromDate(List<String> taskIds, LocalDate fromDate) {
        if (taskIds == null || taskIds.isEmpty()) {
            return new ArrayList<>();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TaskInstance> instances = new ArrayList<>();

        StringBuilder inClause = new StringBuilder();
        String[] selectionArgs = new String[taskIds.size() + 1]; // +1 for date
        for (int i = 0; i < taskIds.size(); i++) {
            inClause.append("?");
            if (i < taskIds.size() - 1) inClause.append(",");
            selectionArgs[i] = taskIds.get(i);
        }

        String selection = "taskId IN (" + inClause + ") AND date >= ?";
        selectionArgs[taskIds.size()] = fromDate.toString(); // yyyy-MM-dd

        Cursor cursor = db.query(
                "task_instances",
                null,
                selection,
                selectionArgs,
                null,
                null,
                "date ASC"
        );

        if (cursor.moveToFirst()) {
            do {
                TaskInstance instance = mapCursorToTaskInstance(cursor);
                instances.add(instance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return instances;
    }


    private TaskInstance mapCursorToTaskInstance(Cursor cursor) {
        TaskInstance instance = new TaskInstance();

        instance.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        instance.setTaskId(cursor.getString(cursor.getColumnIndexOrThrow("taskId")));

        String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        if (dateStr != null) {
            instance.setDate(LocalDate.parse(dateStr));
        } else {
            instance.setDate(null);
        }

        String statusStr = cursor.getString(cursor.getColumnIndexOrThrow("status"));
        if (statusStr != null) {
            try {
                instance.setStatus(TaskStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Cannot parse TaskInstance status");
            }
        } else {
            throw new RuntimeException("Cannot parse TaskInstance status");
        }

        return instance;
    }

    public boolean deleteFutureTaskInstances(String taskId) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {

            String whereClause = "taskId = ? AND date >= ? AND status != ?";
            String[] whereArgs = new String[]{taskId, LocalDate.now().toString(), TaskStatus.COMPLETED.name()};

            int rowsDeleted = db.delete("task_instances", whereClause, whereArgs);

            return rowsDeleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
