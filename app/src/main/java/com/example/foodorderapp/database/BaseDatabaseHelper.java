package com.example.foodorderapp.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BaseDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "BaseDatabaseHelper";
    protected static final String DATABASE_NAME = "FoodOrderApp.db";
    protected static final int DATABASE_VERSION = 4;
    protected final Context context;

    public BaseDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
        // To be implemented by child classes
    }

    protected void dropTables(SQLiteDatabase db) {
        // To be implemented by child classes
    }

    protected void initializeData(SQLiteDatabase db) {
        // To be implemented by child classes
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
} 