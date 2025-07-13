package com.example.foodorderapp.services;

import android.content.Context;

import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.model.CartItem;
import com.example.foodorderapp.model.Order;
import com.example.foodorderapp.model.OrderDetail;
import com.example.foodorderapp.model.OrderStatus;
import com.example.foodorderapp.model.PaymentStatus;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.model.UserRole;
import com.example.foodorderapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final CartService cartService;
    private final SessionManager sessionManager;

    public OrderService(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
        this.cartService = new CartService(context);
        this.sessionManager = SessionManager.getInstance(context);
    }

    public long checkout(String address, String note) {
        if (!sessionManager.isLoggedIn()) {
            return -1;
        }

        User user = sessionManager.getUser();
        ArrayList<CartItem> cartItems = cartService.getAllCartItems();
        if (cartItems.isEmpty()) {
            return -1;
        }

        // Create new order
        Order order = new Order();
        order.setUserId(Integer.parseInt(user.getId()));
        order.setAddress(address);
        order.setNote(note);

        // Convert cart items to order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setProductId(cartItem.getProductId());
            detail.setQuantity(cartItem.getProductQuantity());
            detail.setOriginalPrice(Double.parseDouble(cartItem.getProductPrice()));
            orderDetails.add(detail);
        }
        order.setOrderDetails(orderDetails);

        // Create order in database
        long orderId = dbHelper.orderHelper.createOrder(order);
        if (orderId > 0) {
            // Clear cart after successful order creation
            cartService.clearCart();
        }

        return orderId;
    }

    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        if (!isStaff()) {
            return false;
        }
        return dbHelper.orderHelper.updateOrderStatus(orderId, newStatus);
    }

    public boolean updatePaymentStatus(int orderId, PaymentStatus newStatus) {
        if (!isStaff()) {
            return false;
        }
        return dbHelper.orderHelper.updatePaymentStatus(orderId, newStatus);
    }

    public boolean cancelOrder(int orderId) {
        Order order = dbHelper.orderHelper.getOrderById(orderId);
        if (order == null) {
            return false;
        }

        // Staff can cancel anytime
        if (isStaff()) {
            return dbHelper.orderHelper.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        }

        // Customer can only cancel if both statuses are PENDING
        if (order.getStatus() == OrderStatus.PENDING && 
            order.getPaymentStatus() == PaymentStatus.PENDING) {
            return dbHelper.orderHelper.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        }

        return false;
    }

    public List<Order> getUserOrders() {
        if (!sessionManager.isLoggedIn()) {
            return new ArrayList<>();
        }
        return dbHelper.orderHelper.getOrdersByUserId(Integer.parseInt(sessionManager.getUser().getId()));
    }

    public double getCartTotal() {
        List<CartItem> cartItems = cartService.getAllCartItems();
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getProductQuantity() * Double.parseDouble(item.getProductPrice());
        }
        return total;
    }

    private boolean isStaff() {
        return sessionManager.isLoggedIn() && 
               sessionManager.getUser().getRole() == UserRole.STAFF;
    }
} 