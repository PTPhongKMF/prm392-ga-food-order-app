package com.example.foodorderapp.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.model.CartItem;

public class CartService extends AppCompatActivity {
    private final DatabaseHelper dbHelper;

    public CartService() {
        this.dbHelper = new DatabaseHelper(this);
    }

    public int getCartItemQuantity(int userId) {
        Cursor cursor = dbHelper.select("SELECT COUNT(*) AS total_items FROM CartItem WHERE user_id = " + userId);
        int totalItems = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                totalItems = cursor.getInt(cursor.getColumnIndexOrThrow("total_items"));
            }
            cursor.close();
        }

        return totalItems;
    }

    public CartItem[] getAllCartItems (int userId) {

    }
}
