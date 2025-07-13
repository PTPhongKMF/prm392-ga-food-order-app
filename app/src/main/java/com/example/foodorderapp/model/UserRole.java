package com.example.foodorderapp.model;

public enum UserRole {
    GUEST,
    CUSTOMER,
    STAFF;

    @Override
    public String toString() {
        return name();
    }
} 