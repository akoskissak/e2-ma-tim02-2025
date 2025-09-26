package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.Alliance;
import com.example.habitmaster.domain.models.AllianceInvitation;
import com.example.habitmaster.domain.models.AllianceInviteStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllianceRepository {
    private final DatabaseHelper helper;

    public AllianceRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
    }

    public void createAlliance(Alliance alliance) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", alliance.getId());
        values.put("name", alliance.getName());
        values.put("leaderId", alliance.getLeaderId());
        values.put("missionStarted", alliance.isMissionStarted() ? 1 : 0);
        db.insert(DatabaseHelper.T_ALLIANCES, null, values);
        db.close();
    }

    public void deleteAlliance(String allianceId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.T_ALLIANCES, "id = ?", new String[]{allianceId});
        db.delete(DatabaseHelper.T_ALLIANCE_INVITES, "allianceId = ?", new String[]{allianceId});
        db.delete(DatabaseHelper.T_ALLIANCE_MEMBERS, "allianceId = ?", new String[]{allianceId});
        db.close();
    }

    public Alliance getAllianceByUserId(String userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT a.id, a.name, a.leaderId, a.missionStarted " +
                        "FROM " + DatabaseHelper.T_ALLIANCES + " a " +
                        "JOIN " + DatabaseHelper.T_ALLIANCE_MEMBERS + " m ON a.id = m.allianceId " +
                        "WHERE m.userId = ?",
                new String[]{userId}
        );

        if (cursor.moveToFirst()) {
            boolean missionStarted = cursor.getInt(cursor.getColumnIndexOrThrow("missionStarted")) == 1;
            Alliance alliance = new Alliance(
                    cursor.getString(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("leaderId")),
                    missionStarted
            );
            cursor.close();
            db.close();
            return alliance;
        }

        cursor.close();
        db.close();
        return null;
    }

    public Alliance getAllianceByLeaderId(String leaderId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query(
                    DatabaseHelper.T_ALLIANCES,
                    null,
                    "leaderId = ?",
                    new String[]{leaderId},
                    null,
                    null,
                    null
        );

        if (cursor.moveToFirst()) {
            boolean missionStarted = cursor.getInt(cursor.getColumnIndexOrThrow("missionStarted")) == 1;
            Alliance alliance = new Alliance(cursor.getString(cursor.getColumnIndexOrThrow("id")), cursor.getString(cursor.getColumnIndexOrThrow("name")), cursor.getString(cursor.getColumnIndexOrThrow("leaderId")), missionStarted);
            cursor.close();
            db.close();
            return alliance;
        }

        cursor.close();
        db.close();
        return null;
    }

    public void addMemberToAlliance(String allianceId, String userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("allianceId", allianceId);
        values.put("userId", userId);
        values.put("joinedAt", System.currentTimeMillis());

        db.insert(DatabaseHelper.T_ALLIANCE_MEMBERS, null, values);
        db.close();
    }

    public void addInvitation(AllianceInvitation invitation) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", invitation.getId());
        values.put("allianceId", invitation.getAllianceId());
        values.put("fromUserId", invitation.getFromUserId());
        values.put("toUserId", invitation.getToUserId());
        values.put("status", invitation.getStatus().name());
        values.put("createdAt", System.currentTimeMillis());
        db.insert(DatabaseHelper.T_ALLIANCE_INVITES, null, values);
        db.close();
    }

    public void acceptInvitation(String invitationId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", AllianceInviteStatus.ACCEPTED.name());
        db.update(DatabaseHelper.T_ALLIANCE_INVITES, values, "id = ?", new String[]{invitationId});
        db.close();
    }

    public void declineInvite(String invitationId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "DECLINED");
        db.update(DatabaseHelper.T_ALLIANCE_INVITES,
                values,
                "id = ?",
                new String[]{invitationId});
        db.close();
    }


    public List<String> getMemberIdsByAllianceId(String allianceId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT m.userId FROM " + DatabaseHelper.T_ALLIANCE_MEMBERS + " m " +
                        "WHERE m.allianceId = ?",
                new String[]{allianceId}
        );

        List<String> userIds = new ArrayList<>();
        while (cursor.moveToNext()) {
            userIds.add(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        }

        cursor.close();
        db.close();
        return userIds;
    }


    public void leaveAlliance(String userId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete(DatabaseHelper.T_ALLIANCE_MEMBERS,
                "userId = ?",
                new String[]{userId});
        db.close();
    }

    public void declineOtherInvites(String userId, String acceptedInviteId) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", "DECLINED");
        db.update(DatabaseHelper.T_ALLIANCE_INVITES,
                cv,
                "toUserId = ? AND id != ?",
                new String[]{userId, acceptedInviteId});
        db.close();
    }

    public void updateMissionStarted(String allianceId, boolean missionStarted) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("missionStarted", missionStarted ? 1 : 0);

        db.update(
                DatabaseHelper.T_ALLIANCES,
                values,
                "id = ?",
                new String[]{allianceId}
        );

        db.close();
    }

}
