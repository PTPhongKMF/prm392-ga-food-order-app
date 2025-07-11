package com.example.foodorderapp.services;

import android.content.Context;

import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.model.CartItem;
import com.example.foodorderapp.utils.SessionManager;

import java.util.ArrayList;

public class CartService {
    private final DatabaseHelper dbHelper;
    private final Context context;
    private final SessionManager sessionManager;

    public CartService(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.sessionManager = SessionManager.getInstance(context);
    }

    public ArrayList<CartItem> getAllCartItems() {
        if (!sessionManager.isLoggedIn()) {
            return new ArrayList<>();
        }
        int userId = Integer.parseInt(sessionManager.getUser().getId());
        return dbHelper.cartHelper.getAllCartItems(userId);
    }

    public int getTotalQuantity() {
        if (!sessionManager.isLoggedIn()) {
            return 0;
        }
        int userId = Integer.parseInt(sessionManager.getUser().getId());
        return dbHelper.cartHelper.getTotalQuantity(userId);
    }

    public long addToCart(int productId, int quantity) {
        if (!sessionManager.isLoggedIn() || quantity <= 0) {
            return -1;
        }
        int userId = Integer.parseInt(sessionManager.getUser().getId());
        return dbHelper.cartHelper.addToCart(userId, productId, quantity);
    }

    public boolean removeFromCart(int cartItemId) {
        if (!sessionManager.isLoggedIn()) {
            return false;
        }
        int userId = Integer.parseInt(sessionManager.getUser().getId());
        return dbHelper.cartHelper.removeFromCart(userId, cartItemId) > 0;
    }

    public boolean clearCart() {
        if (!sessionManager.isLoggedIn()) {
            return false;
        }
        int userId = Integer.parseInt(sessionManager.getUser().getId());
        return dbHelper.cartHelper.clearCart(userId) > 0;
    }
}
