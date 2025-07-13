package com.example.foodorderapp.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodorderapp.model.Foods;

import java.util.ArrayList;
import java.util.List;

public class FoodDatabaseHelper {
    private static final String TAG = "FoodDatabaseHelper";

    // Product Table
    public static final String TABLE_PRODUCT = "product";
    public static final String PRODUCT_ID = "id";
    public static final String PRODUCT_CATEGORY_ID = "category_id";
    public static final String PRODUCT_BEST_FOOD = "best_food";
    public static final String PRODUCT_TITLE = "title";
    public static final String PRODUCT_DESCRIPTION = "description";
    public static final String PRODUCT_IMAGE_PATH = "image_path";
    public static final String PRODUCT_LOCATION_ID = "location_id";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_PRICE_ID = "price_id";
    public static final String PRODUCT_STAR = "star";
    public static final String PRODUCT_TIME_ID = "time_id";
    public static final String PRODUCT_TIME_VALUE = "time_value";

    private final Context context;
    private final DatabaseHelper dbHelper;

    public FoodDatabaseHelper(@Nullable Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public FoodDatabaseHelper(@Nullable Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    protected void createTables(SQLiteDatabase db) {
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
                + "FOREIGN KEY(" + PRODUCT_CATEGORY_ID + ") REFERENCES category(id)"
                + ")";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    protected void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
    }

    protected void initializeData(SQLiteDatabase db) {
        dbHelper.executeSqlFromAsset(db, "init_products.sql");
    }

    public ArrayList<Foods> getFoodsByCategoryId(int categoryId) {
        ArrayList<Foods> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDb();

        String query = "SELECT " + PRODUCT_ID + ", " + PRODUCT_CATEGORY_ID + ", " + PRODUCT_TITLE + ", " 
                + PRODUCT_DESCRIPTION + ", " + PRODUCT_IMAGE_PATH + ", " + PRODUCT_LOCATION_ID + ", " 
                + PRODUCT_PRICE + ", " + PRODUCT_PRICE_ID + ", " + PRODUCT_STAR + ", " + PRODUCT_TIME_ID + ", " 
                + PRODUCT_TIME_VALUE + ", " + PRODUCT_BEST_FOOD 
                + " FROM " + TABLE_PRODUCT 
                + " WHERE " + PRODUCT_CATEGORY_ID + " = ?";
        
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
        SQLiteDatabase db = dbHelper.getReadableDb();

        String query = "SELECT " + PRODUCT_ID + ", " + PRODUCT_CATEGORY_ID + ", " + PRODUCT_TITLE + ", " 
                + PRODUCT_DESCRIPTION + ", " + PRODUCT_IMAGE_PATH + ", " + PRODUCT_LOCATION_ID + ", " 
                + PRODUCT_PRICE + ", " + PRODUCT_PRICE_ID + ", " + PRODUCT_STAR + ", " + PRODUCT_TIME_ID + ", " 
                + PRODUCT_TIME_VALUE + ", " + PRODUCT_BEST_FOOD 
                + " FROM " + TABLE_PRODUCT;
        
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

    public List<Foods> getFoodsByCategory(int categoryId) {
        return getFoodsByCategoryId(categoryId);
    }

    public boolean addFood(Foods food) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        try {
            String query = "INSERT INTO " + TABLE_PRODUCT + " (" +
                    PRODUCT_CATEGORY_ID + ", " + PRODUCT_TITLE + ", " + PRODUCT_DESCRIPTION + ", " +
                    PRODUCT_IMAGE_PATH + ", " + PRODUCT_LOCATION_ID + ", " + PRODUCT_PRICE + ", " +
                    PRODUCT_PRICE_ID + ", " + PRODUCT_STAR + ", " + PRODUCT_TIME_ID + ", " +
                    PRODUCT_TIME_VALUE + ", " + PRODUCT_BEST_FOOD + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            db.execSQL(query, new Object[]{
                    food.getCategoryId(),
                    food.getTitle(),
                    food.getDescription(),
                    food.getImagePath(),
                    food.getLocationId(),
                    food.getPrice(),
                    1, // price_id default
                    food.getStar(),
                    food.getTimeId(),
                    String.valueOf(food.getTimeValue()),
                    food.isBestFood() ? 1 : 0
            });
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error adding food: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean updateFood(Foods food) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        try {
            String query = "UPDATE " + TABLE_PRODUCT + " SET " +
                    PRODUCT_CATEGORY_ID + " = ?, " + PRODUCT_TITLE + " = ?, " + PRODUCT_DESCRIPTION + " = ?, " +
                    PRODUCT_IMAGE_PATH + " = ?, " + PRODUCT_LOCATION_ID + " = ?, " + PRODUCT_PRICE + " = ?, " +
                    PRODUCT_STAR + " = ?, " + PRODUCT_TIME_ID + " = ?, " + PRODUCT_TIME_VALUE + " = ?, " +
                    PRODUCT_BEST_FOOD + " = ? WHERE " + PRODUCT_ID + " = ?";
            
            db.execSQL(query, new Object[]{
                    food.getCategoryId(),
                    food.getTitle(),
                    food.getDescription(),
                    food.getImagePath(),
                    food.getLocationId(),
                    food.getPrice(),
                    food.getStar(),
                    food.getTimeId(),
                    String.valueOf(food.getTimeValue()),
                    food.isBestFood() ? 1 : 0,
                    food.getId()
            });
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error updating food: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public boolean deleteFood(int foodId) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        try {
            String query = "DELETE FROM " + TABLE_PRODUCT + " WHERE " + PRODUCT_ID + " = ?";
            db.execSQL(query, new Object[]{foodId});
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting food: " + e.getMessage());
            return false;
        } finally {
            db.close();
        }
    }

    public Foods getFoodById(int foodId) {
        SQLiteDatabase db = dbHelper.getReadableDb();
        Foods food = null;

        String query = "SELECT " + PRODUCT_ID + ", " + PRODUCT_CATEGORY_ID + ", " + PRODUCT_TITLE + ", " 
                + PRODUCT_DESCRIPTION + ", " + PRODUCT_IMAGE_PATH + ", " + PRODUCT_LOCATION_ID + ", " 
                + PRODUCT_PRICE + ", " + PRODUCT_PRICE_ID + ", " + PRODUCT_STAR + ", " + PRODUCT_TIME_ID + ", " 
                + PRODUCT_TIME_VALUE + ", " + PRODUCT_BEST_FOOD 
                + " FROM " + TABLE_PRODUCT 
                + " WHERE " + PRODUCT_ID + " = ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(foodId)});

        if (cursor.moveToFirst()) {
            food = new Foods();
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
        }

        cursor.close();
        db.close();
        return food;
    }
} 