package com.example.foodorderapp.model;

import java.io.Serializable;

public class OrderDetail implements Serializable {
    private int id;
    private int orderId;
    private int productId;
    private int quantity;
    private double totalPrice;
    
    // Additional fields for display
    private String productTitle;
    private double originalPrice;
    private String productImage;

    public OrderDetail() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.originalPrice > 0) {
            this.totalPrice = this.quantity * this.originalPrice;
        }
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
        if (this.quantity > 0) {
            this.totalPrice = this.quantity * this.originalPrice;
        }
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
} 