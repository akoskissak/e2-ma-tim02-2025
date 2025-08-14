package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.Task;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.TaskImportance;

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
}
