package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

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
        values.put("createdAt", instance.getCreatedAt().toString()); // ISO format YYYY-MM-DD
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

    public List<TaskInstance> getByTaskIdFromDate(String taskId, LocalDate fromDate) {
        if (taskId == null || taskId.isEmpty()) {
            return new ArrayList<>();
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<TaskInstance> instances = new ArrayList<>();

        String selection = "taskId = ? AND date >= ?";
        String[] selectionArgs = { taskId, fromDate.toString() }; // yyyy-MM-dd

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

        String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"));
        if (createdAt != null) {
            instance.setCreatedAt(LocalDate.parse(createdAt));
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

    public TaskInstance findById(String id) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            String[] columns = {"id", "taskId", "date", "createdAt", "status"};
            String selection = "id = ?";
            String[] selectionArgs = {id};

            cursor = db.query("task_instances", columns, selection, selectionArgs,
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return mapCursorToTaskInstance(cursor); // reuse your mapping method
            } else {
                return null; // not found
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public boolean updateStatus(String taskInstanceId, TaskStatus newStatus) {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put("status", newStatus.name());

            String threeDaysAgo = LocalDate.now().minusDays(3).toString();

            int rowsUpdated = db.update(
                    "task_instances",
                    values,
                    "id = ? AND date >= ?",
                    new String[]{taskInstanceId, threeDaysAgo}
            );

            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public void updateAll(List<TaskInstance> instances) {
        if (instances == null || instances.isEmpty()) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        try {
            StringBuilder inClause = new StringBuilder();
            String taskId = instances.get(0).getTaskId();

            for (int i = 0; i < instances.size(); i++) {
                inClause.append("?");
                if (i < instances.size() - 1) inClause.append(",");
            }

            String sql = "UPDATE task_instances SET taskId = ? WHERE id IN (" + inClause + ")";
            SQLiteStatement stmt = db.compileStatement(sql);

            stmt.bindString(1, taskId);

            for (int i = 0; i < instances.size(); i++) {
                stmt.bindString(i + 2, instances.get(i).getId());
            }

            stmt.executeUpdateDelete();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }


    public List<TaskInstance> getValuableUserTaskInstances(String userId, LocalDate from, LocalDate to) {
        List<TaskInstance> instances = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            // JOIN task_instances sa tasks da bi se došao do userId
            String sql = "SELECT ti.id, ti.taskId, ti.date, ti.createdAt, ti.status " +
                    "FROM task_instances ti " +
                    "JOIN tasks t ON ti.taskId = t.id " +
                    "WHERE t.userId = ? AND t.xpValue > 0 AND ti.createdAt BETWEEN ? AND ? AND ti.date <= ?" +
                    "ORDER BY ti.date ASC";

            String[] selectionArgs = {
                    userId,
                    from.toString(), // yyyy-MM-dd
                    to.toString(),
                    LocalDate.now().toString()
            };

            cursor = db.rawQuery(sql, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    TaskInstance instance = mapCursorToTaskInstance(cursor);
                    instances.add(instance);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return instances;
    }

}
