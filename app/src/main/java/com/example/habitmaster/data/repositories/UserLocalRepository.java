package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.User;
import com.example.habitmaster.domain.models.UserLevelProgress;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
        cv.put("levelStartDate", u.getLevelStartDate().toString());
        cv.put("title", u.getTitle());
        cv.put("powerPoints", u.getPowerPoints());
        cv.put("xp", u.getXp());
        cv.put("coins", u.getCoins());
        cv.put("badgesCount", u.getBadgesCount());
        cv.put("badges", u.getBadges());
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

    public User findById(String id) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.T_USERS, null, "id=?", new String[]{id}, null, null, null);
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
        String levelStartDateStr = c.getString(c.getColumnIndexOrThrow("levelStartDate"));
        if (levelStartDateStr != null) {
            u.setLevelStartDate(LocalDate.parse(levelStartDateStr));
        } else {
            u.setLevelStartDate(LocalDate.now());
        }
        u.setTitle(c.getString(c.getColumnIndexOrThrow("title")));
        u.setPowerPoints(c.getInt(c.getColumnIndexOrThrow("powerPoints")));
        u.setXp(c.getInt(c.getColumnIndexOrThrow("xp")));
        u.setCoins(c.getInt(c.getColumnIndexOrThrow("coins")));
        u.setBadgesCount(c.getInt(c.getColumnIndexOrThrow("badgesCount")));
        u.setBadges(c.getString(c.getColumnIndexOrThrow("badges")));
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

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query("users", null, null, null, null, null, null); // sve kolone i svi redovi
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User u = cursorToUser(cursor);
                    users.add(u);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return users;
    }

    public boolean exists(String email, String username) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) || u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
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
            userValues.put("levelStartDate", LocalDate.now().toString());

            // dodavanje novcica prvi 200 pa za svaki sledeci 20% vise nego prethodnog

            db.update(DatabaseHelper.T_USERS, userValues, "id = ?", new String[]{userId});

            userLevelProgressRepository.updateUserLevelProgress(progress);
        }
    }

    public User findUserByUsername(String username) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.query(DatabaseHelper.T_USERS, null, "username=?", new String[]{username}, null, null, null);
        if (c.moveToFirst()) {
            User u = cursorToUser(c);
            c.close();
            return u;
        }
        c.close();
        return null;
    }

    public LocalDate getUserLevelStartDate(String userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        LocalDate startDate = null;

        try {
            cursor = db.query(
                    DatabaseHelper.T_USERS,
                    new String[]{"levelStartDate"},
                    "id = ?",
                    new String[]{userId},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("levelStartDate"));
                if (dateStr != null) {
                    startDate = LocalDate.parse(dateStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return startDate;
    }

    public int getUserLevel(String userId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = null;
        int level = -1;

        try {
            cursor = db.query(
                    DatabaseHelper.T_USERS,
                    new String[]{"level"},
                    "id = ?",
                    new String[]{userId},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                level = cursor.getInt(cursor.getColumnIndexOrThrow("level"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }

        return level;
    }

}
