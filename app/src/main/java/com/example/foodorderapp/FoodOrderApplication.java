package com.example.foodorderapp;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class FoodOrderApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Khởi tạo Firebase
        FirebaseApp.initializeApp(this);
    }
} 