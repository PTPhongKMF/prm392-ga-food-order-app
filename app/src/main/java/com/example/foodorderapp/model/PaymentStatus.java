package com.example.foodorderapp.model;

public enum PaymentStatus {
    PENDING,
    COMPLETED,
    CANCELLED;

    @Override
    public String toString() {
        return name();
    }
} 