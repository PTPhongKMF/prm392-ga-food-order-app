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
    private final Context context;  // 👈 Thêm dòng này


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
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_ROLE + " TEXT,"
                + COLUMN_PHONE + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_AVATAR_URL + " TEXT,"
                + COLUMN_CREATED_AT + " TEXT"
                + ")";
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CATEGORY_IMAGE_PATH + " TEXT,"
                + CATEGORY_NAME + " TEXT"
                + ")";

        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + PRODUCT_CATEGORY_ID + " INTEGER,"
                + PRODUCT_TITLE + " TEXT,"
                + PRODUCT_DESCRIPTION + " TEXT,"
                + PRODUCT_IMAGE_PATH + " TEXT,"
                + PRODUCT_LOCATION_ID + " INTEGER,"
                + PRODUCT_PRICE + " REAL,"
                + PRODUCT_PRICE_ID + " INTEGER,"
                + PRODUCT_STAR + " REAL,"
                + PRODUCT_TIME_ID + " INTEGER,"
                + PRODUCT_TIME_VALUE + " TEXT,"
                + PRODUCT_BEST_FOOD + " INTEGER,"
                + "FOREIGN KEY(" + PRODUCT_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + CATEGORY_ID + ")"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_CATEGORY_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        insertDefaultCategories(db);
        executeSqlFromAsset(db, this.context, "init_products.sql");

        Log.d(TAG, "Created users table: " + CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        onCreate(db);

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
        ArrayList<Foods> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, category_id, title, description, image_path, location_id, price, price_id, star, time_id, time_value, best_food " +
                "FROM product WHERE category_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});

        if (cursor.moveToFirst()) {
            do {
                Foods food = new Foods();
                food.setId(cursor.getInt(0));
                food.setCategoryId(cursor.getInt(1));
                food.setTitle(cursor.getString(2));
                food.setDescription(cursor.getString(3));
                food.setImagePath(cursor.getString(4));
                food.setLocationId(cursor.getInt(5));
                food.setPrice(cursor.getDouble(6));
                food.setStar(cursor.getDouble(8));
                food.setTimeId(cursor.getInt(9));
                food.setTimeValue(Integer.parseInt(cursor.getString(10)));
                food.setBestFood(cursor.getInt(11) == 1);

                list.add(food);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<Foods> getAllFoods() {
        ArrayList<Foods> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT id, category_id, title, description, image_path, location_id, price, price_id, star, time_id, time_value, best_food FROM product";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Foods food = new Foods();
                food.setId(cursor.getInt(0));
                food.setCategoryId(cursor.getInt(1));
                food.setTitle(cursor.getString(2));
                food.setDescription(cursor.getString(3));
                food.setImagePath(cursor.getString(4));
                food.setLocationId(cursor.getInt(5));
                food.setPrice(cursor.getDouble(6));
                // price_id is skipped unless needed
                food.setStar(cursor.getDouble(8));
                food.setTimeId(cursor.getInt(9));
                food.setTimeValue(Integer.parseInt(cursor.getString(10)));
                food.setBestFood(cursor.getInt(11) == 1); // SQLite stores booleans as int

                list.add(food);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }


    public ArrayList<Category> getAllCategories() {
        ArrayList<Category> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM category", null);
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
        Log.d("CATEGORY_SIZE", "Total: " + list.size());
        return list;

    }

    private ContentValues createCategory(int id, String imagePath, String name) {
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("image_path", imagePath);
        values.put("name", name);
        return values;
    }

    // Create User
    public long createUser(User user) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            
            values.put(COLUMN_NAME, user.getName());
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(COLUMN_PASSWORD, user.getPassword());
            values.put(COLUMN_ROLE, user.getRole().toString());
            values.put(COLUMN_PHONE, user.getPhone());
            values.put(COLUMN_ADDRESS, user.getAddress());
            values.put(COLUMN_AVATAR_URL, user.getAvatarUrl());
            values.put(COLUMN_CREATED_AT, user.getCreatedAt());

            long id = db.insert(TABLE_USERS, null, values);
            db.close();
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error creating user: " + e.getMessage());
            return -1;
        }
    }

    // Get User by Email
    public User getUserByEmail(String email) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            
            Cursor cursor = db.query(TABLE_USERS,
                    new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD,
                            COLUMN_ROLE, COLUMN_PHONE, COLUMN_ADDRESS, COLUMN_AVATAR_URL,
                            COLUMN_CREATED_AT},
                    COLUMN_EMAIL + "=?",
                    new String[]{email},
                    null, null, null);

            User user = null;
            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setId(cursor.getString(0));
                user.setName(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setPassword(cursor.getString(3));
                user.setRole(UserRole.valueOf(cursor.getString(4)));
                user.setPhone(cursor.getString(5));
                user.setAddress(cursor.getString(6));
                user.setAvatarUrl(cursor.getString(7));
                user.setCreatedAt(cursor.getString(8));
                cursor.close();
            }
            db.close();
            return user;
        } catch (Exception e) {
            Log.e(TAG, "Error getting user by email: " + e.getMessage());
            return null;
        }
    }

    // Get All Users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            String selectQuery = "SELECT * FROM " + TABLE_USERS;

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    User user = new User();
                    user.setId(cursor.getString(0));
                    user.setName(cursor.getString(1));
                    user.setEmail(cursor.getString(2));
                    user.setPassword(cursor.getString(3));
                    user.setRole(UserRole.valueOf(cursor.getString(4)));
                    user.setPhone(cursor.getString(5));
                    user.setAddress(cursor.getString(6));
                    user.setAvatarUrl(cursor.getString(7));
                    user.setCreatedAt(cursor.getString(8));
                    users.add(user);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all users: " + e.getMessage());
        }
        return users;
    }

    // Update User
    public int updateUser(User user) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_NAME, user.getName());
            values.put(COLUMN_EMAIL, user.getEmail());
            values.put(COLUMN_PASSWORD, user.getPassword());
            values.put(COLUMN_ROLE, user.getRole().toString());
            values.put(COLUMN_PHONE, user.getPhone());
            values.put(COLUMN_ADDRESS, user.getAddress());
            values.put(COLUMN_AVATAR_URL, user.getAvatarUrl());
            values.put(COLUMN_CREATED_AT, user.getCreatedAt());

            int rowsAffected = db.update(TABLE_USERS, values,
                    COLUMN_ID + "=?",
                    new String[]{user.getId()});
            db.close();
            return rowsAffected;
        } catch (Exception e) {
            Log.e(TAG, "Error updating user: " + e.getMessage());
            return 0;
        }
    }

    // Delete User
    public void deleteUser(String userId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_USERS, COLUMN_ID + "=?", new String[]{userId});
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user: " + e.getMessage());
        }
    }
} 