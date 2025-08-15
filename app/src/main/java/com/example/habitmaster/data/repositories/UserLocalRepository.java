package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.User;

public class UserLocalRepository {
    private final DatabaseHelper helper;

    public UserLocalRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
    }

    public void insert(User u) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("id", u.getId());
        cv.put("email", u.getEmail());
        cv.put("username", u.getUsername());
        cv.put("avatarName", u.getAvatarName());
        cv.put("activated", u.isActivated() ? 1 : 0);
        cv.put("createdAt", u.getCreatedAt());
        cv.put("level", u.getLevel());
        cv.put("title", u.getTitle());
        cv.put("powerPoints", u.getPowerPoints());
        cv.put("xp", u.getXp());
        cv.put("coins", u.getCoins());
        cv.put("badgesCount", u.getBadgesCount());
        cv.put("badges", u.getBadges());
        cv.put("equipment", u.getEquipment());
        db.insert(DatabaseHelper.T_USERS, null, cv);
    }

    public User findByEmail(String email) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.T_USERS, null, "email=?", new String[]{email}, null, null, null);
        if (c != null && c.moveToFirst()) {
            User u = cursorToUser(c);
            c.close();
            return u;
        }
        if (c != null) c.close();
        return null;
    }

    private User cursorToUser(Cursor c) {
        User u = new User();
        u.setId(c.getString(c.getColumnIndexOrThrow("id")));
        u.setEmail(c.getString(c.getColumnIndexOrThrow("email")));
        u.setUsername(c.getString(c.getColumnIndexOrThrow("username")));
        u.setAvatarName(c.getString(c.getColumnIndexOrThrow("avatarName")));
        u.setActivated(c.getInt(c.getColumnIndexOrThrow("activated")) == 1);
        u.setCreatedAt(c.getLong(c.getColumnIndexOrThrow("createdAt")));
        u.setLevel(c.getInt(c.getColumnIndexOrThrow("level")));
        u.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
        u.setPowerPoints(c.getInt(c.getColumnIndexOrThrow("powerPoints")));
        u.setXp(c.getInt(c.getColumnIndexOrThrow("xp")));
        u.setCoins(c.getInt(c.getColumnIndexOrThrow("coins")));
        u.setBadgesCount(c.getInt(c.getColumnIndexOrThrow("badgesCount")));
        u.setBadges(c.getString(c.getColumnIndexOrThrow("badges")));
        u.setEquipment(c.getString(c.getColumnIndexOrThrow("equipment")));
        return u;
    }

    public void updateActivateFlag(String uid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("activated", 1);
        db.update(DatabaseHelper.T_USERS, values, "id = ?", new String[]{uid});
    }

    public void delete(String userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.T_USERS, "id = ?", new String[]{userId});
        db.close();
    }
}
