package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.AllianceUserMission;

import java.util.ArrayList;
import java.util.List;

public class AllianceUserMissionRepository {

    private final DatabaseHelper helper;

    public AllianceUserMissionRepository(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public void insert(AllianceUserMission mission) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", mission.getId());
        values.put("userId", mission.getUserId());
        values.put("missionId", mission.getMissionId());
        values.put("shopPurchases", mission.getShopPurchases());
        values.put("bossFightHits", mission.getBossFightHits());
        values.put("solvedTasks", mission.getSolvedTasks());
        values.put("solvedOtherTasks", mission.getSolvedOtherTasks());
        values.put("noUnresolvedTasks", mission.isNoUnresolvedTasks() ? 1 : 0); // boolean kao INTEGER
        values.put("messagesSentDays", mission.getMessagesSentDays());
        values.put("totalDamage", mission.getTotalDamage());

        db.insert(DatabaseHelper.T_ALLIANCE_USER_MISSIONS, null, values);
        db.close();
    }

    public List<AllianceUserMission> getAllByMissionId(String missionId) {
        List<AllianceUserMission> missions = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] columns = {
                "id","userId","missionId","shopPurchases","bossFightHits",
                "solvedTasks","solvedOtherTasks","noUnresolvedTasks","messagesSentDays","totalDamage"
        };
        String selection = "missionId = ?";
        String[] selectionArgs = { missionId };

        Cursor cursor = db.query(DatabaseHelper.T_ALLIANCE_USER_MISSIONS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                missions.add(mapCursorToAllianceUserMission(cursor));
            }
            cursor.close();
        }

        db.close();
        return missions;
    }

    private AllianceUserMission mapCursorToAllianceUserMission(Cursor cursor) {
        AllianceUserMission mission = new AllianceUserMission();
        mission.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        mission.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        mission.setMissionId(cursor.getString(cursor.getColumnIndexOrThrow("missionId")));
        mission.setShopPurchases(cursor.getInt(cursor.getColumnIndexOrThrow("shopPurchases")));
        mission.setBossFightHits(cursor.getInt(cursor.getColumnIndexOrThrow("bossFightHits")));
        mission.setSolvedTasks(cursor.getInt(cursor.getColumnIndexOrThrow("solvedTasks")));
        mission.setSolvedOtherTasks(cursor.getInt(cursor.getColumnIndexOrThrow("solvedOtherTasks")));
        mission.setNoUnresolvedTasks(cursor.getInt(cursor.getColumnIndexOrThrow("noUnresolvedTasks")) == 1);
        mission.setMessagesSentDays(cursor.getInt(cursor.getColumnIndexOrThrow("messagesSentDays")));
        mission.setTotalDamage(cursor.getInt(cursor.getColumnIndexOrThrow("totalDamage")));
        return mission;
    }

    public AllianceUserMission getByUserIdAndMissionId(String userId, String missionId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] columns = {
                "id","userId","missionId","shopPurchases","bossFightHits",
                "solvedTasks","solvedOtherTasks","noUnresolvedTasks","messagesSentDays","totalDamage"
        };
        String selection = "userId = ? AND missionId = ?";
        String[] selectionArgs = { userId, missionId };

        Cursor cursor = db.query(
                DatabaseHelper.T_ALLIANCE_USER_MISSIONS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        AllianceUserMission mission = null;
        if (cursor.moveToFirst()) {
            mission = mapCursorToAllianceUserMission(cursor);
        }
        cursor.close();

        db.close();
        return mission;
    }

    public void update(AllianceUserMission userMission) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("shopPurchases", userMission.getShopPurchases());
        values.put("bossFightHits", userMission.getBossFightHits());
        values.put("solvedTasks", userMission.getSolvedTasks());
        values.put("solvedOtherTasks", userMission.getSolvedOtherTasks());
        values.put("noUnresolvedTasks", userMission.isNoUnresolvedTasks() ? 1 : 0);
        values.put("messagesSentDays", userMission.getMessagesSentDays());
        values.put("totalDamage", userMission.getTotalDamage());

        String whereClause = "id = ?";
        String[] whereArgs = { userMission.getId() };

        db.update(DatabaseHelper.T_ALLIANCE_USER_MISSIONS, values, whereClause, whereArgs);
        db.close();
    }

}
