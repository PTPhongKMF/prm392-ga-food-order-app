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

public class DatabaseHelper extends BaseDatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "FoodOrderApp.db";
    private static final int DATABASE_VERSION = 4;
    private final Context context;  // 👈 Thêm dòng này
    private final UserDatabaseHelper userHelper;
    private final CategoryDatabaseHelper categoryHelper;
    private final FoodDatabaseHelper foodHelper;

    // Table Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";
    private static final String COLUMN_PHONE = "phone";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_AVATAR_URL = "avatar_url";
    private static final String COLUMN_CREATED_AT = "created_at";

    // Category Table
    private static final String TABLE_CATEGORY = "category";
    private static final String CATEGORY_ID = "id";
    private static final String CATEGORY_IMAGE_PATH = "image_path";
    private static final String CATEGORY_NAME = "name";

    // Product Table
    private static final String TABLE_PRODUCT = "product";
    private static final String PRODUCT_ID = "id";
    private static final String PRODUCT_CATEGORY_ID = "category_id";
    private static final String PRODUCT_BEST_FOOD = "best_food";
    private static final String PRODUCT_TITLE = "title";
    private static final String PRODUCT_DESCRIPTION = "description";
    private static final String PRODUCT_IMAGE_PATH = "image_path";
    private static final String PRODUCT_LOCATION_ID = "location_id";
    private static final String PRODUCT_PRICE = "price";
    private static final String PRODUCT_PRICE_ID = "price_id";
    private static final String PRODUCT_STAR = "star";
    private static final String PRODUCT_TIME_ID = "time_id";
    private static final String PRODUCT_TIME_VALUE = "time_value";

    public DatabaseHelper(@Nullable Context context) {
        super(context);
        this.context = context;
        userHelper = new UserDatabaseHelper(context);
        categoryHelper = new CategoryDatabaseHelper(context);
        foodHelper = new FoodDatabaseHelper(context);
    }

    @Override
    protected void createTables(SQLiteDatabase db) {
        userHelper.createTables(db);
        categoryHelper.createTables(db);
        foodHelper.createTables(db);
    }

    @Override
    protected void dropTables(SQLiteDatabase db) {
        foodHelper.dropTables(db);  // Drop in reverse order due to foreign key constraints
        categoryHelper.dropTables(db);
        userHelper.dropTables(db);
    }

    @Override
    protected void initializeData(SQLiteDatabase db) {
        categoryHelper.initializeData(db);
        foodHelper.initializeData(db);
    }

    private void executeSqlFromAsset(SQLiteDatabase db, Context context, String fileName) {
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
} 