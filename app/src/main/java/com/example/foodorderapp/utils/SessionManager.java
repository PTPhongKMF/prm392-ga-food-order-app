package com.example.foodorderapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.foodorderapp.model.User;
import com.example.foodorderapp.model.UserRole;
import com.google.gson.Gson;

public class SessionManager {
    private static final String PREF_NAME = "FoodOrderAppSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";
    
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private static SessionManager instance;
    
    private SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }
    
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }
    
    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.commit();
    }
    
    public void saveUser(User user) {
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        setLogin(true);
        editor.commit();
    }
    
    public User getUser() {
        String userJson = pref.getString(KEY_USER, null);
        if (userJson != null) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }
    
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    public void logout() {
        editor.clear();
        editor.commit();
    }
    
    // Getter methods for user information
    public String getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }
    
    public String getUserName() {
        User user = getUser();
        return user != null ? user.getName() : null;
    }
    
    public String getUserEmail() {
        User user = getUser();
        return user != null ? user.getEmail() : null;
    }
    
    public String getUserPhone() {
        User user = getUser();
        return user != null ? user.getPhone() : null;
    }
    
    public String getUserAddress() {
        User user = getUser();
        return user != null ? user.getAddress() : null;
    }
    
    public UserRole getUserRole() {
        User user = getUser();
        return user != null ? user.getRole() : null;
    }
} 