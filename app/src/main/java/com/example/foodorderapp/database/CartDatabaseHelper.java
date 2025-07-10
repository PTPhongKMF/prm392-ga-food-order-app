package com.example.foodorderapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodorderapp.model.CartItem;

public class CartDatabaseHelper extends BaseDatabaseHelper {
    private static final String TAG = "CartDatabaseHelper";

    // Cart Table
    private static final String TABLE_CART = "CartItem";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_QUANTITY = "quantity";

    public CartDatabaseHelper(@Nullable Context context) {
        super(context);
    }

    @Override
    protected void createTables(SQLiteDatabase db) {
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_PRODUCT_ID + " INTEGER,"
                + COLUMN_QUANTITY + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES users(id),"
                + "FOREIGN KEY (" + COLUMN_PRODUCT_ID + ") REFERENCES product(id)"
                + ")";
        db.execSQL(CREATE_CART_TABLE);
        Log.d(TAG, "Created cart table: " + CREATE_CART_TABLE);
    }

    @Override
    protected void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
    }

    public int getCartItemQuantity(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) AS total_items FROM " + TABLE_CART + " WHERE " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
        
        int totalItems = 0;
        if (cursor != null && cursor.moveToFirst()) {
            totalItems = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return totalItems;
    }

    // Add more cart operations here as needed
} 