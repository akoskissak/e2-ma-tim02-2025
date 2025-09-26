package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.Boss;

public class BossRepository {
    private final DatabaseHelper dbHelper;

    public BossRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void insert(Boss boss) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", boss.getId());
        values.put("userId", boss.getUserId());
        values.put("level", boss.getLevel());
        values.put("maxHp", boss.getMaxHp());
        values.put("currentHp", boss.getCurrentHp());
        values.put("remainingAttacks", boss.getRemainingAttacks());
        values.put("maxAttacks", boss.getMaxAttacks());
        values.put("rewardCoins", boss.getRewardCoins());
        db.insert(DatabaseHelper.T_BOSSES, null, values);
        db.close();
    }

    public Boss findByUserId(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Boss boss = null;

        String selection = "userId = ?";
        String[] selectionArgs = { String.valueOf(userId) };

        Cursor cursor = db.query(
                DatabaseHelper.T_BOSSES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                "level DESC",
                "1"
        );

        if (cursor.moveToFirst()) {
            boss = mapCursorToBoss(cursor);
        }

        cursor.close();
        db.close();
        return boss;
    }

    private Boss mapCursorToBoss(Cursor cursor) {
        Boss boss = new Boss();
        boss.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        boss.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        boss.setLevel(cursor.getInt(cursor.getColumnIndexOrThrow("level")));
        boss.setMaxHp(cursor.getDouble(cursor.getColumnIndexOrThrow("maxHp")));
        boss.setCurrentHp(cursor.getDouble(cursor.getColumnIndexOrThrow("currentHp")));
        boss.setRemainingAttacks(cursor.getInt(cursor.getColumnIndexOrThrow("remainingAttacks")));
        boss.setMaxAttacks(cursor.getInt(cursor.getColumnIndexOrThrow("maxAttacks")));
        boss.setRewardCoins(cursor.getDouble(cursor.getColumnIndexOrThrow("rewardCoins")));
        return boss;
    }

    public void update(Boss boss) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", boss.getUserId());
        values.put("level", boss.getLevel());
        values.put("maxHp", boss.getMaxHp());
        values.put("currentHp", boss.getCurrentHp());
        values.put("remainingAttacks", boss.getRemainingAttacks());
        values.put("rewardCoins", boss.getRewardCoins());
        values.put("maxAttacks", boss.getMaxAttacks());

        String whereClause = "id = ?";
        String[] whereArgs = { boss.getId() };

        db.update(DatabaseHelper.T_BOSSES, values, whereClause, whereArgs);
        db.close();
    }

    public void updateBossStats(String bossId, int remainingAttacks, int rewardCoins) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("remainingAttacks", remainingAttacks);
        values.put("rewardCoins", rewardCoins);
        values.put("maxAttacks", remainingAttacks);

        String whereClause = "id = ?";
        String[] whereArgs = { bossId };

        db.update(DatabaseHelper.T_BOSSES, values, whereClause, whereArgs);
        db.close();
    }


}
