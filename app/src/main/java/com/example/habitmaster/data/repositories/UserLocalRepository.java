package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserLevelProgress;

public class UserLocalRepository {
    private final DatabaseHelper helper;
    private final UserLevelProgressRepository userLevelProgressRepository;

    public UserLocalRepository(Context ctx) {
        this.helper = new DatabaseHelper(ctx);
        this.userLevelProgressRepository = new UserLevelProgressRepository(ctx);
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

        // kreiranje i userLevelProgress
        userLevelProgressRepository.createUserLevelProgress(new UserLevelProgress(u.getId()));
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

    public void updateUserCoins(String userId, int coins) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("coins", coins);
        db.update(DatabaseHelper.T_USERS, values, "id = ?", new String[]{userId});
    }

    public void addXp(String userId, int xp) {
        try (SQLiteDatabase db = helper.getWritableDatabase(); Cursor cursor = db.query(DatabaseHelper.T_USERS,
                new String[]{"xp"},
                "id = ?",
                new String[]{userId},
                null, null, null)) {

            int currentXp = 0;
            if (cursor != null && cursor.moveToFirst()) {
                currentXp = cursor.getInt(cursor.getColumnIndexOrThrow("xp"));
            }

            int newXp = currentXp + xp;

            ContentValues values = new ContentValues();
            values.put("xp", newXp);

            db.update(DatabaseHelper.T_USERS, values, "id = ?", new String[]{userId});

            // provera levelUp-a
            checkAndLevelUp(userId, db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndLevelUp(String userId, SQLiteDatabase db) {
        Cursor userCursor = db.query(DatabaseHelper.T_USERS,
                new String[]{"level", "title", "powerPoints", "xp"},
                "id = ?",
                new String[]{userId},
                null, null, null);

        int level = 0;
        String title;
        int powerPoints = 0;
        int currentXp = 0;

        if (userCursor.moveToFirst()) {
            level = userCursor.getInt(userCursor.getColumnIndexOrThrow("level"));
            title = userCursor.getString(userCursor.getColumnIndexOrThrow("title"));
            powerPoints = userCursor.getInt(userCursor.getColumnIndexOrThrow("powerPoints"));
            currentXp = userCursor.getInt(userCursor.getColumnIndexOrThrow("xp"));
        }
        userCursor.close();

        UserLevelProgress progress = userLevelProgressRepository.getUserLevelProgress(userId);
        if(progress == null ) return;

        while(currentXp >= progress.getRequiredXp()) {
            level++;

            currentXp -= progress.getRequiredXp();

            int prevRequired = progress.getRequiredXp();
            progress.updateRequiredXp(prevRequired);

            if(level == 1) {
                powerPoints = 40;
            } else {
                powerPoints = (int) Math.round(powerPoints + (3.0 / 4.0) * powerPoints);
            }

            progress.updateXpValuesOnLevelUp();

            switch (level) {
                case 0:
                    title = "Rookie";
                    break;
                case 1:
                    title = "Adventurer";
                    break;
                case 2:
                    title = "Hero";
                    break;
                default:
                    title = "Hero lvl" + level;
                    break;
            }

            ContentValues userValues = new ContentValues();
            userValues.put("level", level);
            userValues.put("title", title);
            userValues.put("powerPoints", powerPoints);
            userValues.put("xp", currentXp);

            // dodavanje novcica prvi 200 pa za svaki sledeci 20% vise nego prethodnog

            db.update(DatabaseHelper.T_USERS, userValues, "id = ?", new String[]{userId});

            userLevelProgressRepository.updateUserLevelProgress(progress);
        }
    }
}
