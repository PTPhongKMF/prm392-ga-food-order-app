package com.example.foodorderapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodorderapp.model.Order;
import com.example.foodorderapp.model.OrderDetail;
import com.example.foodorderapp.model.OrderStatus;
import com.example.foodorderapp.model.PaymentStatus;

import java.util.ArrayList;
import java.util.List;

public class OrderDatabaseHelper {
    private static final String TAG = "OrderDatabaseHelper";

    // Order Table
    private static final String TABLE_ORDER = "orders";
    private static final String ORDER_ID = "id";
    private static final String ORDER_USER_ID = "user_id";
    private static final String ORDER_ADDRESS = "address";
    private static final String ORDER_NOTE = "note";
    private static final String ORDER_STATUS = "status";
    private static final String ORDER_PAYMENT_STATUS = "payment_status";
    private static final String ORDER_CREATED_AT = "created_at";
    private static final String ORDER_UPDATED_AT = "updated_at";

    // Order Detail Table
    private static final String TABLE_ORDER_DETAIL = "order_detail";
    private static final String DETAIL_ID = "id";
    private static final String DETAIL_ORDER_ID = "order_id";
    private static final String DETAIL_PRODUCT_ID = "product_id";
    private static final String DETAIL_QUANTITY = "quantity";
    private static final String DETAIL_TOTAL_PRICE = "total_price";

    private final Context context;
    private final DatabaseHelper dbHelper;

    public OrderDatabaseHelper(@Nullable Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    protected void createTables(SQLiteDatabase db) {
        String CREATE_ORDER_TABLE = "CREATE TABLE " + TABLE_ORDER + "("
                + ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ORDER_USER_ID + " INTEGER,"
                + ORDER_ADDRESS + " TEXT,"
                + ORDER_NOTE + " TEXT,"
                + ORDER_STATUS + " TEXT,"
                + ORDER_PAYMENT_STATUS + " TEXT,"
                + ORDER_CREATED_AT + " TEXT,"
                + ORDER_UPDATED_AT + " TEXT,"
                + "FOREIGN KEY(" + ORDER_USER_ID + ") REFERENCES " + UserDatabaseHelper.TABLE_USERS + "(id)"
                + ")";

        String CREATE_ORDER_DETAIL_TABLE = "CREATE TABLE " + TABLE_ORDER_DETAIL + "("
                + DETAIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DETAIL_ORDER_ID + " INTEGER,"
                + DETAIL_PRODUCT_ID + " INTEGER,"
                + DETAIL_QUANTITY + " INTEGER,"
                + DETAIL_TOTAL_PRICE + " REAL,"
                + "FOREIGN KEY(" + DETAIL_ORDER_ID + ") REFERENCES " + TABLE_ORDER + "(id),"
                + "FOREIGN KEY(" + DETAIL_PRODUCT_ID + ") REFERENCES " + FoodDatabaseHelper.TABLE_PRODUCT + "(id)"
                + ")";

        db.execSQL(CREATE_ORDER_TABLE);
        db.execSQL(CREATE_ORDER_DETAIL_TABLE);
    }

    protected void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
    }

    public long createOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        long orderId = -1;

        try {
            db.beginTransaction();

            ContentValues orderValues = new ContentValues();
            orderValues.put(ORDER_USER_ID, order.getUserId());
            orderValues.put(ORDER_ADDRESS, order.getAddress());
            orderValues.put(ORDER_NOTE, order.getNote());
            orderValues.put(ORDER_STATUS, order.getStatus().toString());
            orderValues.put(ORDER_PAYMENT_STATUS, order.getPaymentStatus().toString());
            orderValues.put(ORDER_CREATED_AT, System.currentTimeMillis() + "");
            orderValues.put(ORDER_UPDATED_AT, System.currentTimeMillis() + "");

            orderId = db.insert(TABLE_ORDER, null, orderValues);

            if (orderId != -1 && order.getOrderDetails() != null) {
                for (OrderDetail detail : order.getOrderDetails()) {
                    ContentValues detailValues = new ContentValues();
                    detailValues.put(DETAIL_ORDER_ID, orderId);
                    detailValues.put(DETAIL_PRODUCT_ID, detail.getProductId());
                    detailValues.put(DETAIL_QUANTITY, detail.getQuantity());
                    detailValues.put(DETAIL_TOTAL_PRICE, detail.getTotalPrice());

                    db.insert(TABLE_ORDER_DETAIL, null, detailValues);
                }
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error creating order: " + e.getMessage());
            orderId = -1;
        } finally {
            db.endTransaction();
        }

        return orderId;
    }

    public Order getOrderById(int orderId) {
        SQLiteDatabase db = dbHelper.getReadableDb();
        Order order = null;

        String query = "SELECT o.*, u." + UserDatabaseHelper.COLUMN_NAME + " as user_name "
                + "FROM " + TABLE_ORDER + " o "
                + "LEFT JOIN " + UserDatabaseHelper.TABLE_USERS + " u ON o." + ORDER_USER_ID + " = u." + UserDatabaseHelper.COLUMN_ID
                + " WHERE o." + ORDER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor != null && cursor.moveToFirst()) {
            order = extractOrderFromCursor(cursor);
            order.setOrderDetails(getOrderDetails(orderId));
            cursor.close();
        }

        return order;
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDb();

        String query = "SELECT o.*, u." + UserDatabaseHelper.COLUMN_NAME + " as user_name "
                + "FROM " + TABLE_ORDER + " o "
                + "LEFT JOIN " + UserDatabaseHelper.TABLE_USERS + " u ON o." + ORDER_USER_ID + " = u." + UserDatabaseHelper.COLUMN_ID
                + " WHERE o." + ORDER_USER_ID + " = ? "
                + "ORDER BY o." + ORDER_CREATED_AT + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Order order = extractOrderFromCursor(cursor);
                order.setOrderDetails(getOrderDetails(order.getId()));
                orders.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return orders;
    }

    private List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDb();

        String query = "SELECT od.*, p." + FoodDatabaseHelper.PRODUCT_TITLE + ", p." + FoodDatabaseHelper.PRODUCT_PRICE
                + ", p." + FoodDatabaseHelper.PRODUCT_IMAGE_PATH
                + " FROM " + TABLE_ORDER_DETAIL + " od "
                + "LEFT JOIN " + FoodDatabaseHelper.TABLE_PRODUCT + " p ON od." + DETAIL_PRODUCT_ID + " = p.id"
                + " WHERE od." + DETAIL_ORDER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                OrderDetail detail = new OrderDetail();
                detail.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DETAIL_ID)));
                detail.setOrderId(cursor.getInt(cursor.getColumnIndexOrThrow(DETAIL_ORDER_ID)));
                detail.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(DETAIL_PRODUCT_ID)));
                detail.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(DETAIL_QUANTITY)));
                detail.setTotalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(DETAIL_TOTAL_PRICE)));
                detail.setProductTitle(cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.PRODUCT_TITLE)));
                detail.setOriginalPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.PRODUCT_PRICE)));
                detail.setProductImage(cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.PRODUCT_IMAGE_PATH)));
                details.add(detail);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return details;
    }

    private Order extractOrderFromCursor(Cursor cursor) {
        Order order = new Order();
        order.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ORDER_ID)));
        order.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(ORDER_USER_ID)));
        order.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(ORDER_ADDRESS)));
        order.setNote(cursor.getString(cursor.getColumnIndexOrThrow(ORDER_NOTE)));
        order.setStatus(OrderStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ORDER_STATUS))));
        order.setPaymentStatus(PaymentStatus.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(ORDER_PAYMENT_STATUS))));
        order.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ORDER_CREATED_AT)));
        order.setUpdatedAt(cursor.getString(cursor.getColumnIndexOrThrow(ORDER_UPDATED_AT)));
        order.setUserName(cursor.getString(cursor.getColumnIndexOrThrow("user_name")));
        return order;
    }

    public boolean updateOrderStatus(int orderId, OrderStatus status) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        ContentValues values = new ContentValues();
        values.put(ORDER_STATUS, status.toString());
        values.put(ORDER_UPDATED_AT, System.currentTimeMillis() + "");

        return db.update(TABLE_ORDER, values,
                ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)}) > 0;
    }

    public boolean updatePaymentStatus(int orderId, PaymentStatus status) {
        SQLiteDatabase db = dbHelper.getWritableDb();
        ContentValues values = new ContentValues();
        values.put(ORDER_PAYMENT_STATUS, status.toString());
        values.put(ORDER_UPDATED_AT, System.currentTimeMillis() + "");

        return db.update(TABLE_ORDER, values,
                ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)}) > 0;
    }
} 