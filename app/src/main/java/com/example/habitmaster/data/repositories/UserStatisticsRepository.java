package com.example.habitmaster.data.repositories;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.TaskDifficulty;
import com.example.habitmaster.domain.models.UserStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserStatisticsRepository {
    private final DatabaseHelper helper;

    public UserStatisticsRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
    }

    public UserStatistics getUserStatistics(String userId) {
        UserStatistics stats = new UserStatistics();

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM task_instances ti JOIN tasks t ON ti.taskId = t.id WHERE t.userId = ?", new String[]{userId});
        if(c1.moveToFirst()){
            stats.setTotalCreated(c1.getInt(0));
        }
        c1.close();

        Cursor c2 = db.rawQuery(
                "SELECT status, COUNT(*) FROM task_instances ti " +
                    "JOIN tasks t ON ti.taskId = t.id " +
                    "WHERE t.userId = ? " +
                    "GROUP BY status",
                    new String[]{userId});
        int completed = 0, cancelled = 0, missed = 0;
        while(c2.moveToNext()){
            String status = c2.getString(0);
            int count = c2.getInt(1);
            switch (status) {
                case "COMPLETED": completed = count;
                    break;
                case "CANCELLED": cancelled = count;
                    break;
                case "MISSED": missed = count;
                    break;
                default:
                    break;
            }
        }
        stats.setTotalCompleted(completed);
        stats.setTotalMissed(missed);
        stats.setTotalCancelled(cancelled);
        c2.close();

        Cursor c3 = db.rawQuery(
                "SELECT COUNT(DISTINCT date) FROM task_instances ti " +
                    "JOIN tasks t ON ti.taskId = t.id " +
                    "WHERE t.userId = ?",
                    new String[]{userId});
        if(c3.moveToFirst()){
            stats.setActiveDays(c3.getInt(0));
        }
        c3.close();

        Cursor c4 = db.rawQuery(
                "SELECT date, " +
                    "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed, " +
                    "SUM(CASE WHEN status = 'MISSED' THEN 1 ELSE 0 END) as missed " +
                    "FROM task_instances ti " +
                    "JOIN tasks t ON ti.taskId = t.id " +
                    "WHERE t.userId = ? " +
                    "GROUP BY date ORDER BY date",
                    new String[]{userId});
        int longestStreak = 0, currentStreak = 0;
        while(c4.moveToNext()){
            int streakCompleted = c4.getInt(1);
            int streakMissed = c4.getInt(2);

            if(streakCompleted > 0 && streakMissed == 0){
                currentStreak++;
                if(currentStreak > longestStreak){
                    longestStreak = currentStreak;
                }
            } else {
                currentStreak = 0;
            }
        }
        stats.setLongestStreak(longestStreak);
        c4.close();

        Cursor c5 = db.rawQuery(
                "SELECT c.name, COUNT(*) FROM task_instances ti " +
                    "JOIN tasks t ON ti.taskId = t.id " +
                    "JOIN categories c ON t.categoryId = c.id " +
                    "WHERE t.userId = ? AND ti.status = 'COMPLETED' " +
                    "GROUP BY c.name",
                    new String[]{userId});
        Map<String, Integer> byCategory = new HashMap<>();
        while(c5.moveToNext()){
            String categoryName = c5.getString(0);
            int categoryCount = c5.getInt(1);
            byCategory.put(categoryName, categoryCount);
        }
        stats.setCompletedTasksByCategory(byCategory);
        c5.close();

        Cursor c6 = db.rawQuery(
                "SELECT date, SUM(t.xpValue) FROM task_instances ti " +
                    "JOIN tasks t ON ti.taskId = t.id " +
                    "WHERE t.userId = ? AND ti.status = 'COMPLETED' " +
                    "AND date >= date('now', '-6 days') " +
                    "GROUP BY date ORDER BY date",
                    new String[]{userId});
        List<Integer> xp7 = new ArrayList<>();
        while (c6.moveToNext()){
            xp7.add(c6.getInt(1));
        }
        stats.setXpLast7Days(xp7);
        c6.close();

        Cursor c7 = db.rawQuery(
                "SELECT t.difficulty, COUNT(*) FROM task_instances ti " +
                    "JOIN tasks t ON ti.taskId = t.id " +
                    "WHERE t.userId = ? AND ti.status = 'COMPLETED' " +
                    "GROUP BY t.difficulty",
                    new String[]{userId});

        Map<TaskDifficulty, Float> percentByDifficulty = new HashMap<>();
        for(TaskDifficulty d : TaskDifficulty.values()){
            percentByDifficulty.put(d, 0f);
        }

        int totalCompleted = 0;
        Map<TaskDifficulty, Integer> counts = new HashMap<>();

        while(c7.moveToNext()){
            String diffString = c7.getString(0);
            int count = c7.getInt(1);
            TaskDifficulty difficulty = TaskDifficulty.valueOf(diffString);

            counts.put(difficulty, count);
            totalCompleted += count;
        }
        c7.close();

        for(TaskDifficulty d : TaskDifficulty.values()) {
            int count = counts.getOrDefault(d, 0);
            float percent = totalCompleted > 0 ? (count * 100f / totalCompleted) : 0f;
            percentByDifficulty.put(d, percent);
        }
        stats.setDifficultyPercent(percentByDifficulty);

        return stats;
    }
}
