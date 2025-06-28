package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodorderapp.UserService.LoginActivity;
import com.example.foodorderapp.UserService.UserProfileActivity;
import com.example.foodorderapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageButton btnUserMenu;
    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SessionManager
        sessionManager = SessionManager.getInstance(this);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        btnUserMenu = findViewById(R.id.btn_user_menu);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set up user menu click listener
        btnUserMenu.setOnClickListener(v -> showUserMenu());
    }

    //User Menu options
    private void showUserMenu() {
        if (!isUserLoggedIn()) {
            // Show login dialog
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Đăng nhập")
                .setMessage("Bạn cần đăng nhập để xem thông tin tài khoản")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                })
                .setNegativeButton("Hủy", null)
                .show();
            return;
        }

        PopupMenu popup = new PopupMenu(this, btnUserMenu);
        popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_profile) {
                startActivity(new Intent(this, UserProfileActivity.class));
                return true;
            } else if (itemId == R.id.action_orders) {
                // TODO: Navigate to orders
                Toast.makeText(this, "Đơn hàng của tôi", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_settings) {
                // TODO: Navigate to settings
                Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.action_logout) {
                logout();
                return true;
            }
            return false;
        });

        popup.show();
    }

    private boolean isUserLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    private void logout() {
        sessionManager.logout();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        // Restart activity to refresh state
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            // TODO: Navigate to home
            Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_menu) {
            // TODO: Navigate to menu
            Toast.makeText(this, "Thực đơn", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_cart) {
            if (!isUserLoggedIn()) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đăng nhập")
                    .setMessage("Bạn cần đăng nhập để xem giỏ hàng")
                    .setPositiveButton("Đăng nhập", (dialog, which) -> {
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
                return false;
            }
            // TODO: Navigate to cart
            Toast.makeText(this, "Giỏ hàng", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}