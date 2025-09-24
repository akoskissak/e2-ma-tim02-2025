package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.AllianceMission;
import com.example.habitmaster.domain.models.AllianceMissionStatus;

import java.time.LocalDateTime;

public class AllianceMissionRepository {
    private final DatabaseHelper helper;

    public AllianceMissionRepository(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public void insert(AllianceMission mission) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("id", mission.getId());
        values.put("allianceId", mission.getAllianceId());
        values.put("startDateTime", mission.getStartDateTime().toString()); // LocalDateTime kao TEXT
        values.put("endDateTime", mission.getEndDateTime().toString());
        values.put("status", mission.getStatus().name()); // Enum u string
        values.put("bossMaxHp", mission.getBossMaxHp());
        values.put("bossCurrentHp", mission.getBossCurrentHp());

        db.insert(DatabaseHelper.T_ALLIANCE_MISSIONS, null, values);
        db.close();
    }

    public AllianceMission getOngoingByAllianceId(String allianceId) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.T_ALLIANCE_MISSIONS,
                null,  // sve kolone
                "allianceId = ? AND status = ?",
                new String[]{allianceId, AllianceMissionStatus.ONGOING.name()},
                null,
                null,
                null
        );

        AllianceMission mission = null;
        if (cursor.moveToFirst()) {
            mission = mapCursorToAllianceMission(cursor);
        }

        cursor.close();
        db.close();
        return mission;
    }

    public AllianceMission mapCursorToAllianceMission(Cursor cursor) {
        AllianceMission mission = new AllianceMission();

        mission.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        mission.setAllianceId(cursor.getString(cursor.getColumnIndexOrThrow("allianceId")));
        mission.setStartDateTime(LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow("startDateTime"))));
        mission.setEndDateTime(LocalDateTime.parse(cursor.getString(cursor.getColumnIndexOrThrow("endDateTime"))));
        mission.setStatus(AllianceMissionStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status"))));
        mission.setBossMaxHp(cursor.getInt(cursor.getColumnIndexOrThrow("bossMaxHp")));
        mission.setBossCurrentHp(cursor.getInt(cursor.getColumnIndexOrThrow("bossCurrentHp")));

        return mission;
    }

    public void update(AllianceMission allianceMission) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("allianceId", allianceMission.getAllianceId());
        values.put("startDateTime", allianceMission.getStartDateTime().toString());
        values.put("endDateTime", allianceMission.getEndDateTime().toString());
        values.put("status", allianceMission.getStatus().name());
        values.put("bossMaxHp", allianceMission.getBossMaxHp());
        values.put("bossCurrentHp", allianceMission.getBossCurrentHp());

        String whereClause = "id = ?";
        String[] whereArgs = { allianceMission.getId() };

        db.update(DatabaseHelper.T_ALLIANCE_MISSIONS, values, whereClause, whereArgs);
        db.close();
    }

    public AllianceMission getById(String id) {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.T_ALLIANCE_MISSIONS,
                null,  // sve kolone
                "id = ?",
                new String[]{id},
                null,
                null,
                null
        );

        AllianceMission mission = null;
        if (cursor.moveToFirst()) {
            mission = mapCursorToAllianceMission(cursor);
        }

        cursor.close();
        db.close();
        return mission;
    }
}
