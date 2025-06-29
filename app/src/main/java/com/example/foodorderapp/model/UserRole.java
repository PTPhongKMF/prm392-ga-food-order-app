package com.example.foodorderapp.model;

public enum UserRole {
    GUEST,
    CUSTOMER,
    STAFF,
    SELLER;

    @Override
    public String toString() {
        return name();
    }
} 