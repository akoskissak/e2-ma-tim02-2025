package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.data.firebases.FirebaseUserLevelProgressRepository;
import com.example.habitmaster.domain.models.UserLevelProgress;

public class UserLevelProgressRepository {

    private final DatabaseHelper helper;
    private final FirebaseUserLevelProgressRepository firebaseRepo;

    public UserLevelProgressRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
        this.firebaseRepo = new FirebaseUserLevelProgressRepository();
    }

    public UserLevelProgress getUserLevelProgress(String userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        UserLevelProgress progress = null;
        try {
            Cursor cursor = db.query(DatabaseHelper.T_USER_LEVEL_PROGRESS,
                    new String[]{"requiredXp", "veryEasyXp", "easyXp", "hardXp", "extremelyHardXp",
                            "normalXp", "importantXp", "extremelyImportantXp", "specialXp"},
                    "userId = ?",
                    new String[]{userId},
                    null, null, null);

            if (cursor.moveToFirst()) {
                progress = new UserLevelProgress(userId);
                progress.setRequiredXp(cursor.getInt(cursor.getColumnIndexOrThrow("requiredXp")));
                progress.setVeryEasyXp(cursor.getInt(cursor.getColumnIndexOrThrow("veryEasyXp")));
                progress.setEasyXp(cursor.getInt(cursor.getColumnIndexOrThrow("easyXp")));
                progress.setHardXp(cursor.getInt(cursor.getColumnIndexOrThrow("hardXp")));
                progress.setExtremelyHardXp(cursor.getInt(cursor.getColumnIndexOrThrow("extremelyHardXp")));
                progress.setNormalXp(cursor.getInt(cursor.getColumnIndexOrThrow("normalXp")));
                progress.setImportantXp(cursor.getInt(cursor.getColumnIndexOrThrow("importantXp")));
                progress.setExtremelyImportantXp(cursor.getInt(cursor.getColumnIndexOrThrow("extremelyImportantXp")));
                progress.setSpecialXp(cursor.getInt(cursor.getColumnIndexOrThrow("specialXp")));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return progress;
    }

    public void updateUserLevelProgress(UserLevelProgress progress) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("requiredXp", progress.getRequiredXp());
            values.put("veryEasyXp", progress.getVeryEasyXp());
            values.put("easyXp", progress.getEasyXp());
            values.put("hardXp", progress.getHardXp());
            values.put("extremelyHardXp", progress.getExtremelyHardXp());
            values.put("normalXp", progress.getNormalXp());
            values.put("importantXp", progress.getImportantXp());
            values.put("extremelyImportantXp", progress.getExtremelyImportantXp());
            values.put("specialXp", progress.getSpecialXp());

            db.update(DatabaseHelper.T_USER_LEVEL_PROGRESS, values, "userId = ?", new String[]{progress.getUserId()});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void createUserLevelProgress(UserLevelProgress progress) {
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("userId", progress.getUserId());
            values.put("veryEasyXp", progress.getVeryEasyXp());
            values.put("easyXp", progress.getEasyXp());
            values.put("hardXp", progress.getHardXp());
            values.put("extremelyHardXp", progress.getExtremelyHardXp());
            values.put("normalXp", progress.getNormalXp());
            values.put("importantXp", progress.getImportantXp());
            values.put("extremelyImportantXp", progress.getExtremelyImportantXp());
            values.put("specialXp", progress.getSpecialXp());
            db.insert(DatabaseHelper.T_USER_LEVEL_PROGRESS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }

        firebaseRepo.saveUserLevelProgress(progress, task -> {
            if(task.isSuccessful()) {
                Log.d("Firebase", "Progress saved successfully");
            } else {
                Log.e("Firebase", "Error saving progress", task.getException());
            }
        });
    }
}
