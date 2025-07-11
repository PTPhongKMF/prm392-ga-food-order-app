package com.example.foodorderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodorderapp.model.Category;
import com.example.foodorderapp.model.Foods;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.model.UserRole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "FoodOrderApp.db";
    private static final int DATABASE_VERSION = 4;
    private final Context context;
    private final UserDatabaseHelper userHelper;
    private final CategoryDatabaseHelper categoryHelper;
    private final FoodDatabaseHelper foodHelper;
    private final CartDatabaseHelper cartHelper;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        userHelper = new UserDatabaseHelper(context, this);
        categoryHelper = new CategoryDatabaseHelper(context, this);
        foodHelper = new FoodDatabaseHelper(context, this);
        cartHelper = new CartDatabaseHelper(context, this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
        initializeData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables(db);
        onCreate(db);
    }

    protected void createTables(SQLiteDatabase db) {
        userHelper.createTables(db);
        categoryHelper.createTables(db);
        foodHelper.createTables(db);
        cartHelper.createTables(db);
    }

    protected void dropTables(SQLiteDatabase db) {
        cartHelper.dropTables(db);  // Drop in reverse order due to foreign key constraints
        foodHelper.dropTables(db);
        categoryHelper.dropTables(db);
        userHelper.dropTables(db);
    }

    protected void initializeData(SQLiteDatabase db) {
        categoryHelper.initializeData(db);
        foodHelper.initializeData(db);
    }

    protected void executeSqlFromAsset(SQLiteDatabase db, String fileName) {
        try {
            InputStream input = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            StringBuilder statement = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                statement.append(line);
                if (line.trim().endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement.setLength(0);
                }
            }
            reader.close();
        } catch (IOException | SQLException e) {
            Log.e("DB_INIT", "Error executing SQL file: " + e.getMessage());
        }
    }

    public void insertDefaultCategories(SQLiteDatabase db) {
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
            db.insert("category", null, values);
        }

        Log.d(TAG, "Inserted default categories");
    }

    public ArrayList<Foods> getFoodsByCategoryId(int categoryId) {
        return foodHelper.getFoodsByCategoryId(categoryId);
    }

    public ArrayList<Foods> getAllFoods() {
        return foodHelper.getAllFoods();
    }

    public ArrayList<Category> getAllCategories() {
        return categoryHelper.getAllCategories();
    }

    private ContentValues createCategory(int id, String imagePath, String name) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("image_path", imagePath);
        values.put("name", name);
        return values;
    }

    // User operations
    public long createUser(User user) {
        return userHelper.createUser(user);
    }

    public User getUserByEmail(String email) {
        return userHelper.getUserByEmail(email);
    }

    public List<User> getAllUsers() {
        return userHelper.getAllUsers();
    }

    public int updateUser(User user) {
        return userHelper.updateUser(user);
    }

    public void deleteUser(String userId) {
        userHelper.deleteUser(userId);
    }

    // Getter for database
    public SQLiteDatabase getWritableDb() {
        return this.getWritableDatabase();
    }

    public SQLiteDatabase getReadableDb() {
        return this.getReadableDatabase();
    }
}
