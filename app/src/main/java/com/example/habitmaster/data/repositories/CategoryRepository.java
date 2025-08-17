package com.example.habitmaster.data.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.habitmaster.data.database.DatabaseHelper;
import com.example.habitmaster.domain.models.Category;
import com.example.habitmaster.utils.exceptions.ColorNotUniqueException;
import com.example.habitmaster.utils.exceptions.NameNotUniqueException;

import java.util.ArrayList;
import java.util.List;

public class CategoryRepository {
    private final DatabaseHelper dbHelper;

    public CategoryRepository(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public List<Category> getUserCategories(String userId) {
        List<Category> categories = new ArrayList<>();

        try (SQLiteDatabase db = dbHelper.getReadableDatabase();
             Cursor cursor = db.query(
                     DatabaseHelper.T_CATEGORIES,
                     new String[]{"id", "userId", "name", "color"},
                     "userId = ?",
                     new String[]{userId},
                     null, null, null)) {

            if (cursor.moveToFirst()) {
                do {
                    categories.add(mapCursorToCategory(cursor));
                } while (cursor.moveToNext());
            }
        }

        return categories;
    }

    private Category mapCursorToCategory(Cursor cursor) {
        Category category = new Category();
        category.setId(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        category.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        category.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        category.setColor(cursor.getInt(cursor.getColumnIndexOrThrow("color")));

        return category;
    }

    public void addCategory(Category category) throws NameNotUniqueException, ColorNotUniqueException {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            validateUniqueNameAndColor(category, db, false);

            ContentValues values = new ContentValues();
            values.put("id", category.getId());
            values.put("userId", category.getUserId());
            values.put("name", category.getName());
            values.put("color", category.getColor());

            db.insert(DatabaseHelper.T_CATEGORIES, null, values);
        }
    }

    public void updateCategory(Category category) throws NameNotUniqueException, ColorNotUniqueException, Exception {
        try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            validateUniqueNameAndColor(category, db, true);

            ContentValues values = new ContentValues();
            values.put("name", category.getName());
            values.put("color", category.getColor());

            int rowsAffected = db.update(
                    DatabaseHelper.T_CATEGORIES,
                    values,
                    "id = ?",
                    new String[]{category.getId()}
            );

            if (rowsAffected == 0) {
                throw new Exception("Category not found or not updated");
            }
        }
    }

    public int getCategoryColorById(String categoryId) {
        int color = -1; // default value if not found
        try (SQLiteDatabase db = dbHelper.getReadableDatabase()) {
            Cursor cursor = db.query(
                    DatabaseHelper.T_CATEGORIES,
                    new String[]{"color"},
                    "id = ?",
                    new String[]{categoryId},
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    color = cursor.getInt(cursor.getColumnIndexOrThrow("color"));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }

    private void validateUniqueNameAndColor(Category category, SQLiteDatabase db, boolean isUpdate)
            throws NameNotUniqueException, ColorNotUniqueException {

        String idExclusion = isUpdate ? " AND id != ?" : "";
        String[] nameArgs = isUpdate
                ? new String[]{category.getUserId(), category.getName(), category.getId()}
                : new String[]{category.getUserId(), category.getName()};

        // Check name
        Cursor cursorName = db.query(
                DatabaseHelper.T_CATEGORIES,
                new String[]{"id"},
                "userId = ? AND name = ?" + idExclusion,
                nameArgs,
                null, null, null
        );
        if (cursorName.moveToFirst()) {
            cursorName.close();
            throw new NameNotUniqueException();
        }
        cursorName.close();

        // Check color
        String[] colorArgs = isUpdate
                ? new String[]{category.getUserId(), String.valueOf(category.getColor()), category.getId()}
                : new String[]{category.getUserId(), String.valueOf(category.getColor())};

        Cursor cursorColor = db.query(
                DatabaseHelper.T_CATEGORIES,
                new String[]{"id"},
                "userId = ? AND color = ?" + idExclusion,
                colorArgs,
                null, null, null
        );
        if (cursorColor.moveToFirst()) {
            cursorColor.close();
            throw new ColorNotUniqueException();
        }
        cursorColor.close();
    }

}
