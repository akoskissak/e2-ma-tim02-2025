package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.Badge;

import java.util.ArrayList;
import java.util.List;

public class BadgeRepository {
    private final DatabaseHelper helper;

    public BadgeRepository(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public void insert(Badge badge) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", badge.getId());
        values.put("userId", badge.getUserId());
        values.put("missionId", badge.getMissionId());
        values.put("imageName", badge.getImageName());
        values.put("shopPurchases", badge.getShopPurchases());
        values.put("bossFightHits", badge.getBossFightHits());
        values.put("solvedTasks", badge.getSolvedTasks());
        values.put("solvedOtherTasks", badge.getSolvedOtherTasks());
        values.put("noUnresolvedTasks", badge.isNoUnresolvedTasks() ? 1 : 0);
        values.put("messagesSentDays", badge.getMessagesSentDays());
        values.put("totalDamage", badge.getTotalDamage());

        db.insert(DatabaseHelper.T_BADGES, null, values);
        db.close();
    }

    public List<Badge> getAllByUserId(String userId) {
        List<Badge> badges = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.T_BADGES,
                null, // sve kolone
                "userId = ?",
                new String[]{userId},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Badge badge = new Badge(
                        cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("userId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("missionId")),
                        cursor.getString(cursor.getColumnIndexOrThrow("imageName")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("shopPurchases")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("bossFightHits")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("solvedTasks")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("solvedOtherTasks")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("noUnresolvedTasks")) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow("messagesSentDays")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("totalDamage"))
                );
                badges.add(badge);
            } while (cursor.moveToNext());
            cursor.close();
        }

        db.close();
        return badges;
    }
}
