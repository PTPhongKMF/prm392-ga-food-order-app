package com.example.foodorderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodorderapp.model.Category;

import java.util.ArrayList;

public class CategoryDatabaseHelper {
    private static final String TAG = "CategoryDatabaseHelper";

    // Category Table
    private static final String TABLE_CATEGORY = "category";
    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_IMAGE_PATH = "image_path";
    private static final String CATEGORY_NAME = "name";

    private final Context context;
    private final DatabaseHelper dbHelper;

    public CategoryDatabaseHelper(@Nullable Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    protected void createTables(SQLiteDatabase db) {
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CATEGORY_IMAGE_PATH + " TEXT,"
                + CATEGORY_NAME + " TEXT"
                + ")";
        db.execSQL(CREATE_CATEGORY_TABLE);
    }

    protected void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
    }

    protected void initializeData(SQLiteDatabase db) {
        insertDefaultCategories(db);
    }

    private void insertDefaultCategories(SQLiteDatabase db) {
        ContentValues[] categories = new ContentValues[] {
                createCategory(0, "cat1", "Pizza"),
                createCategory(1, "cat2", "Burger"),
                createCategory(2, "cat3", "Chicken"),
                createCategory(3, "cat4", "Sushi"),
                createCategory(4, "cat5", "Meat"),
                createCategory(5, "cat6", "Hotdog"),
                createCategory(6, "cat7", "Drink"),
                createCategory(7, "cat8", "Coffie"),
                createCategory(8, "cat9", "More")
        };

        for (ContentValues values : categories) {
            db.insert(TABLE_CATEGORY, null, values);
        }

        Log.d(TAG, "Inserted default categories");
    }

    private ContentValues createCategory(int id, String imagePath, String name) {
        ContentValues values = new ContentValues();
        values.put(CATEGORY_ID, id);
        values.put(CATEGORY_IMAGE_PATH, imagePath);
        values.put(CATEGORY_NAME, name);
        return values;
    }

    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDb();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
        if (cursor.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(cursor.getInt(0));
                cat.setImagePath(cursor.getString(1));
                cat.setName(cursor.getString(2));
                list.add(cat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Total categories: " + list.size());
        return list;
    }
} 