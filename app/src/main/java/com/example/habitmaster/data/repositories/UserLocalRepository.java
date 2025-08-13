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
        return u;
    }
}
