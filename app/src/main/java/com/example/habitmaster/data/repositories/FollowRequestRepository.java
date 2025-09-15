package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.data.dtos.FollowRequestWithUsername;
import com.example.habitmaster.domain.models.FollowRequest;
import com.example.habitmaster.domain.models.FollowRequestStatus;

import java.util.ArrayList;
import java.util.List;

public class FollowRequestRepository {
    private final DatabaseHelper helper;

    public FollowRequestRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
    }

    public void createFollowRequest(FollowRequest request) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", request.getId());
        values.put("fromUserId", request.getFromUserId());
        values.put("toUserId", request.getToUserId());
        values.put("status", request.getStatus().name());
        db.insert("follow_requests", null, values);
        db.close();
    }

    public List<FollowRequestWithUsername> getPendingRequests(String toUserId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT fr.id, fr.fromUserId, fr.toUserId, fr.status, u.username " +
                        "FROM " + DatabaseHelper.T_FOLLOW_REQUESTS + " fr " +
                        "JOIN " + DatabaseHelper.T_USERS + " u ON fr.fromUserId = u.id " +
                        "WHERE fr.toUserId = ? AND fr.status = 'PENDING'",
                new String[]{toUserId}
        );

        List<FollowRequestWithUsername> requests = new ArrayList<>();
        while(cursor.moveToNext()) {
            String statusStr = cursor.getString(cursor.getColumnIndexOrThrow("status"));
            FollowRequestStatus status = FollowRequestStatus.valueOf(statusStr);

            requests.add(new FollowRequestWithUsername(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("fromUserId")),
                    cursor.getString(cursor.getColumnIndexOrThrow("toUserId")),
                    status,
                    cursor.getString(cursor.getColumnIndexOrThrow("username"))
            ));
        }
        cursor.close();
        db.close();
        return requests;
    }

    public void updateRequestStatus(String requestId, String newStatus) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update(DatabaseHelper.T_FOLLOW_REQUESTS, values, "id = ?", new String[]{requestId});
        db.close();
    }

    public boolean isPending(String fromUserId, String toUserId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM follow_requests " +
                "WHERE fromUserId = ? AND toUserId = ? AND status = ?";
        Cursor cursor = db.rawQuery(query, new String[]{fromUserId, toUserId, FollowRequestStatus.PENDING.toString()});

        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }

        cursor.close();
        db.close();
        return exists;
    }

}
