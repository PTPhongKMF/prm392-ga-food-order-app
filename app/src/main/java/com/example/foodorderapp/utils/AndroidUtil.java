package com.example.foodorderapp.utils;

import android.content.Intent;

import com.example.foodorderapp.model.User;

public class AndroidUtil {

    public static void passUserModelAsIntent(Intent intent, User user) {
        intent.putExtra("userid", user.getId());
        intent.putExtra("username", user.getName());
        intent.putExtra("userphone", user.getPhone());
    }

    public static User getUserModelFromIntent(Intent intent) {
        String id = intent.getStringExtra("userid");
        String name = intent.getStringExtra("username");
        String phone = intent.getStringExtra("userphone");
        return new User(id, name, phone);
    }

}
