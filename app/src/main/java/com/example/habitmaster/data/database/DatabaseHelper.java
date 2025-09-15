package com.example.habitmaster.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "habitmaster.db";
    public static final int DB_VERSION = 3;

    public static final String T_USERS = "users";
    public static final String T_USER_LEVEL_PROGRESS = "user_level_progress";
    public static final String T_TASKS = "tasks";
    public static final String T_TASK_INSTANCES = "task_instances";
    public static final String T_CATEGORIES = "categories";
    public static final String T_EQUIPMENT = "equipment";
    public static final String T_FRIENDS = "friends";
    public static final String T_ALLIANCES = "alliances";
    public static final String T_ALLIANCE_INVITES = "alliance_invites";
    public static final String T_ALLIANCE_MEMBERS = "alliance_members";
    public static final String T_FOLLOW_REQUESTS = "follow_requests";

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
                "badges TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + T_USER_LEVEL_PROGRESS + " (" +
                "userId TEXT PRIMARY KEY, " +
                "requiredXp INTEGER DEFAULT 200," +
                "veryEasyXp INTEGER DEFAULT 1," +
                "easyXp INTEGER DEFAULT 3," +
                "hardXp INTEGER DEFAULT 7," +
                "extremelyHardXp INTEGER DEFAULT 20," +
                "normalXp INTEGER DEFAULT 1," +
                "importantXp INTEGER DEFAULT 3," +
                "extremelyImportantXp INTEGER DEFAULT 10," +
                "specialXp INTEGER DEFAULT 100," +
                "FOREIGN KEY(userId) REFERENCES " + T_USERS + "(id) ON DELETE CASCADE" +
                ")");

        db.execSQL("CREATE TABLE " + T_TASKS + " (" +
                "id TEXT PRIMARY KEY, " +
                "userId TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "description TEXT, " +
                "categoryId TEXT NOT NULL, " +
                "frequency TEXT, " +
                "repeatInterval INTEGER, " +
                "startDate TEXT, " +
                "endDate TEXT, " +
                "executionTime TEXT, " +
                "difficulty TEXT NOT NULL, " +
                "importance TEXT NOT NULL, " +
                "xpValue INTEGER NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + T_TASK_INSTANCES + " (" +
            "id TEXT PRIMARY KEY, " +
            "taskId TEXT NOT NULL, " +
            "date TEXT NOT NULL, " +
            "status TEXT NOT NULL, " +
            "FOREIGN KEY(taskId) REFERENCES " + T_TASKS + "(id) ON DELETE CASCADE, " +
            "UNIQUE(taskId, date)" +
            ")");

        db.execSQL("CREATE TABLE " + T_CATEGORIES + " (" +
                "id TEXT PRIMARY KEY, " +
                "userId TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "color INTEGER NOT NULL, " +
                "FOREIGN KEY(userId) REFERENCES " + T_USERS + "(id) ON DELETE CASCADE, " +
                "UNIQUE(userId, name, color)" +
                ")");

        db.execSQL("CREATE TABLE " + T_EQUIPMENT + " (" +
                "id TEXT PRIMARY KEY, " +
                "userId TEXT NOT NULL, " +
                "equipmentId TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "type TEXT NOT NULL, " +
                "activated INTEGER NOT NULL, " +
                "duration INTEGER NOT NULL, " +
                "bonusValue REAL NOT NULL, " +
                "bonusType TEXT NOT NULL, " +
                "upgradeLevel INTEGER DEFAULT 0," +
                "FOREIGN KEY(userId) REFERENCES " + T_USERS + "(id) ON DELETE CASCADE" +
                ")");

        db.execSQL("CREATE TABLE " + T_FRIENDS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId TEXT NOT NULL," +
                "friendUserId TEXT NOT NULL," +
                "friendUsername TEXT," +
                "friendAvatarName TEXT," +
                "FOREIGN KEY(userId) REFERENCES "  + T_USERS + "(id) ON DELETE CASCADE," +
                "UNIQUE(userId, friendUserId) ON CONFLICT REPLACE" +
                ");");

        db.execSQL("CREATE TABLE " + T_ALLIANCES + " (" +
                "id TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "leaderId TEXT NOT NULL," +
                "missionStarted INTEGER NOT NULL DEFAULT 0," +
                "FOREIGN KEY(leaderId) REFERENCES " + T_USERS + "(id) ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + T_ALLIANCE_INVITES + " (" +
                "id TEXT PRIMARY KEY," +
                "allianceId TEXT NOT NULL," +
                "fromUserId TEXT NOT NULL," +
                "toUserId TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "FOREIGN KEY(allianceId) REFERENCES " + T_ALLIANCES + "(id) ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + T_ALLIANCE_MEMBERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "allianceId TEXT NOT NULL," +
                "userId TEXT NOT NULL," +
                "UNIQUE(allianceId, userId) ON CONFLICT REPLACE," +
                "FOREIGN KEY(allianceId) REFERENCES " + T_ALLIANCES + "(id) ON DELETE CASCADE," +
                "FOREIGN KEY(userId) REFERENCES " + T_USERS + "(id) ON DELETE CASCADE" +
                ");");

        db.execSQL("CREATE TABLE " + T_FOLLOW_REQUESTS + " (" +
                "id TEXT PRIMARY KEY," +
                "fromUserId TEXT NOT NULL," +
                "toUserId TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "FOREIGN KEY(fromUserId) REFERENCES " + T_USERS + "(id) ON DELETE CASCADE," +
                "FOREIGN KEY(toUserId) REFERENCES " + T_USERS + "(id) ON DELETE CASCADE" +
                ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + T_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + T_USER_LEVEL_PROGRESS);
        db.execSQL("DROP TABLE IF EXISTS " + T_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + T_TASK_INSTANCES);
        db.execSQL("DROP TABLE IF EXISTS " + T_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + T_EQUIPMENT);
        db.execSQL("DROP TABLE IF EXISTS " + T_ALLIANCES);
        db.execSQL("DROP TABLE IF EXISTS " + T_ALLIANCE_INVITES);
        db.execSQL("DROP TABLE IF EXISTS " + T_ALLIANCE_MEMBERS);
        db.execSQL("DROP TABLE IF EXISTS " + T_FOLLOW_REQUESTS);
        onCreate(db);
    }
}
