package com.example.habitmaster.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "habitmaster.db";
    public static final int DB_VERSION = 1;

    public static final String T_USERS = "users";
    public static final String T_TASKS = "tasks";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + T_USERS + " (" +
                "id TEXT PRIMARY KEY, " +
                "email TEXT NOT NULL UNIQUE, " +
                "username TEXT NOT NULL UNIQUE, " +
                "avatarName TEXT NOT NULL, " +
                "activated INTEGER NOT NULL DEFAULT 0, " +
                "createdAt INTEGER NOT NULL," +
                "level INTEGER DEFAULT 1," +
                "title TEXT," +
                "powerPoints INTEGER DEFAULT 0," +
                "xp INTEGER DEFAULT 0," +
                "coins INTEGER DEFAULT 0," +
                "badgesCount INTEGER DEFAULT 0," +
                "badges TEXT," +
                "equipment TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + T_TASKS + " (" +
                "id TEXT PRIMARY KEY, " +
                "userId TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "categoryId INTEGER NOT NULL, " +
                "frequency TEXT, " +
                "repeatInterval INTEGER, " +
                "startDate TEXT, " +
                "endDate TEXT, " +
                "difficulty TEXT NOT NULL, " +
                "importance TEXT NOT NULL, " +
                "xpValue INTEGER NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + T_TASKS);
        onCreate(db);
    }
}
