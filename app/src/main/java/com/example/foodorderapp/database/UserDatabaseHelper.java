package com.example.foodorderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodorderapp.model.User;
import com.example.foodorderapp.model.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHelper {
    private static final String TAG = "UserDatabaseHelper";

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

    private final Context context;
    private final DatabaseHelper dbHelper;

    public UserDatabaseHelper(@Nullable Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    protected void createTables(SQLiteDatabase db) {
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
        db.execSQL(CREATE_USERS_TABLE);
        Log.d(TAG, "Created users table: " + CREATE_USERS_TABLE);
    }

    protected void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
    }

    // Create User
    public long createUser(User user) {
        try {
            SQLiteDatabase db = dbHelper.getWritableDb();
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
            SQLiteDatabase db = dbHelper.getReadableDb();
            
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

            SQLiteDatabase db = dbHelper.getReadableDb();
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
            SQLiteDatabase db = dbHelper.getWritableDb();
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
            SQLiteDatabase db = dbHelper.getWritableDb();
            db.delete(TABLE_USERS, COLUMN_ID + "=?", new String[]{userId});
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error deleting user: " + e.getMessage());
        }
    }
} 