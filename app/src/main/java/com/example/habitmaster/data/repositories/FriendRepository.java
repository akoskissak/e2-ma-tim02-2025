package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.Friend;
import com.google.android.gms.tasks.OnCompleteListener;

import java.util.ArrayList;
import java.util.List;

public class FriendRepository {
    private final DatabaseHelper helper;

    public FriendRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
    }

    public void addFriend(Friend friend, String currentUserId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", currentUserId);
        values.put("friendUserId", friend.getFriendUserId());
        values.put("friendUsername", friend.getFriendUsername());
        values.put("friendAvatarName", friend.getFriendAvatarName());

        db.insert(DatabaseHelper.T_FRIENDS, null, values);
        db.close();
    }

    public void removeFriend(String friendUserId, String currentUserId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.T_FRIENDS, "userId=? AND friendUserId=?", new String[]{ currentUserId, friendUserId });
        db.close();
    }

    public List<Friend> getAllFriends(String userId) {
        List<Friend> friends = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.T_FRIENDS,
                new String[]{"friendUserId", "friendUsername", "friendAvatarName"},
                "userId=?",
                new String[]{ userId },
                null, null, null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Friend f = new Friend();
                f.setFriendUserId(cursor.getString(cursor.getColumnIndexOrThrow("friendUserId")));
                f.setFriendUsername(cursor.getString(cursor.getColumnIndexOrThrow("friendUsername")));
                f.setFriendAvatarName(cursor.getString(cursor.getColumnIndexOrThrow("friendAvatarName")));
                friends.add(f);
            }
            cursor.close();
        }

        db.close();
        return friends;
    }

    public boolean isAlreadyFriend(String currentUserId, String viewedUserId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.T_FRIENDS, new String[]{"id"}, "userId=? AND friendUserId=?", new String[]{ currentUserId, viewedUserId }, null, null, null
        );

        boolean exists = (cursor != null && cursor.moveToFirst());
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

}
