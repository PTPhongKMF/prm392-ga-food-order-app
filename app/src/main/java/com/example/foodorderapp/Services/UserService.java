package com.example.foodorderapp.Services;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.foodorderapp.Database.DatabaseHelper;
import com.example.foodorderapp.Database.Tables.UserTable;
import com.example.foodorderapp.Models.User;
import com.example.foodorderapp.Models.UserRole;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final DatabaseHelper dbHelper;

    public UserService(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Create User
    public long createUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        
        values.put(UserTable.COLUMN_NAME, user.getName());
        values.put(UserTable.COLUMN_EMAIL, user.getEmail());
        values.put(UserTable.COLUMN_PASSWORD, user.getPassword());
        values.put(UserTable.COLUMN_ROLE, user.getRole().toString());
        values.put(UserTable.COLUMN_PHONE, user.getPhone());
        values.put(UserTable.COLUMN_ADDRESS, user.getAddress());
        values.put(UserTable.COLUMN_AVATAR_URL, user.getAvatarUrl());
        values.put(UserTable.COLUMN_CREATED_AT, user.getCreatedAt());

        long id = db.insert(UserTable.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Get User by Email
    public User getUserByEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(UserTable.TABLE_NAME,
                new String[]{UserTable.COLUMN_ID, UserTable.COLUMN_NAME, UserTable.COLUMN_EMAIL, UserTable.COLUMN_PASSWORD,
                        UserTable.COLUMN_ROLE, UserTable.COLUMN_PHONE, UserTable.COLUMN_ADDRESS, UserTable.COLUMN_AVATAR_URL,
                        UserTable.COLUMN_CREATED_AT},
                UserTable.COLUMN_EMAIL + "=?",
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
    }

    // Get All Users
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + UserTable.TABLE_NAME;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
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
        return users;
    }

    // Update User
    public int updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserTable.COLUMN_NAME, user.getName());
        values.put(UserTable.COLUMN_EMAIL, user.getEmail());
        values.put(UserTable.COLUMN_PASSWORD, user.getPassword());
        values.put(UserTable.COLUMN_ROLE, user.getRole().toString());
        values.put(UserTable.COLUMN_PHONE, user.getPhone());
        values.put(UserTable.COLUMN_ADDRESS, user.getAddress());
        values.put(UserTable.COLUMN_AVATAR_URL, user.getAvatarUrl());
        values.put(UserTable.COLUMN_CREATED_AT, user.getCreatedAt());

        int rowsAffected = db.update(UserTable.TABLE_NAME, values,
                UserTable.COLUMN_ID + "=?",
                new String[]{user.getId()});
        db.close();
        return rowsAffected;
    }

    // Delete User
    public void deleteUser(String userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(UserTable.TABLE_NAME, UserTable.COLUMN_ID + "=?", new String[]{userId});
        db.close();
    }
} 