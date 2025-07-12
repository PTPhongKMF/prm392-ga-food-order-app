package com.example.foodorderapp.model;

public enum OrderStatus {
    PENDING,
    PREPARING,
    SHIPPING,
    COMPLETED,
    CANCELLED;

    @Override
    public String toString() {
        return name();
    }
} 