package com.example.foodorderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodorderapp.model.CartItem;

import java.util.ArrayList;

public class CartDatabaseHelper {
    private static final String TAG = "CartDatabaseHelper";

    // Cart Table
    private static final String TABLE_CART = "CartItem";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_QUANTITY = "quantity";

    private final Context context;
    private final DatabaseHelper dbHelper;

    public CartDatabaseHelper(@Nullable Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    protected void createTables(SQLiteDatabase db) {
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID + " INTEGER,"
                + COLUMN_PRODUCT_ID + " INTEGER,"
                + COLUMN_QUANTITY + " INTEGER,"
                + "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES users(id),"
                + "FOREIGN KEY (" + COLUMN_PRODUCT_ID + ") REFERENCES " + FoodDatabaseHelper.TABLE_PRODUCT + "(id)"
                + ")";
        db.execSQL(CREATE_CART_TABLE);
        Log.d(TAG, "Created cart table: " + CREATE_CART_TABLE);
    }

    protected void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
    }

    public ArrayList<CartItem> getAllCartItems(int userId) {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDb();
        
        String query = "SELECT c.*, p." + FoodDatabaseHelper.PRODUCT_TITLE + ", p." + FoodDatabaseHelper.PRODUCT_PRICE 
                + " FROM " + TABLE_CART + " c "
                + " INNER JOIN " + FoodDatabaseHelper.TABLE_PRODUCT + " p ON c." + COLUMN_PRODUCT_ID + " = p.id"
                + " WHERE c." + COLUMN_USER_ID + " = ?";
                
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.PRODUCT_TITLE)),
                    String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.PRODUCT_PRICE))),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY))
                );
                cartItems.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        
        return cartItems;
    }

    public int getTotalQuantity(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDb();
        Cursor cursor = db.rawQuery(
            "SELECT SUM(" + COLUMN_QUANTITY + ") as total FROM " + TABLE_CART 
            + " WHERE " + COLUMN_USER_ID + " = ?",
            new String[]{String.valueOf(userId)}
        );
        
        int totalQuantity = 0;
        if (cursor != null && cursor.moveToFirst()) {
            totalQuantity = cursor.getInt(0);
            cursor.close();
        }
        return totalQuantity;
    }

    public long addToCart(int userId, int productId, int quantity) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        
        // Check if item already exists
        Cursor cursor = db.query(TABLE_CART,
            new String[]{COLUMN_ID, COLUMN_QUANTITY},
            COLUMN_USER_ID + " = ? AND " + COLUMN_PRODUCT_ID + " = ?",
            new String[]{String.valueOf(userId), String.valueOf(productId)},
            null, null, null);
            
        long result;
        
        if (cursor != null && cursor.moveToFirst()) {
            // Update existing item
            int existingQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
            int newQuantity = existingQuantity + quantity;
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQuantity);
            
            result = db.update(TABLE_CART, values,
                COLUMN_ID + " = ?",
                new String[]{String.valueOf(itemId)});
                
            cursor.close();
        } else {
            // Insert new item
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_PRODUCT_ID, productId);
            values.put(COLUMN_QUANTITY, quantity);
            
            result = db.insert(TABLE_CART, null, values);
        }
        
        return result;
    }

    public int removeFromCart(int userId, int cartItemId) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        return db.delete(TABLE_CART,
            COLUMN_ID + " = ? AND " + COLUMN_USER_ID + " = ?",
            new String[]{String.valueOf(cartItemId), String.valueOf(userId)});
    }

    public int clearCart(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        return db.delete(TABLE_CART,
            COLUMN_USER_ID + " = ?",
            new String[]{String.valueOf(userId)});
    }
} 