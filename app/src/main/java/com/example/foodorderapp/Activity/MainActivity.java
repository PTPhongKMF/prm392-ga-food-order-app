package com.example.foodorderapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.foodorderapp.R;
import com.example.foodorderapp.Activity.OrderHistoryActivity;
import com.example.foodorderapp.UserService.LoginActivity;
import com.example.foodorderapp.UserService.UserProfileActivity;
import com.example.foodorderapp.Activity.RestaurantInfoActivity;
import com.example.foodorderapp.adapter.CategoryAdapter;
import com.example.foodorderapp.adapter.SliderAdapter;
import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.databinding.ActivityMainBinding;
import com.example.foodorderapp.model.Category;
import com.example.foodorderapp.model.SliderItems;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.services.CartService;
import com.example.foodorderapp.utils.AndroidUtil;
import com.example.foodorderapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ImageButton btnUserMenu;
    private BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    DatabaseReference databaseReference;
    private CartService cartService;
    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;


    RecyclerView recyclerView;
    ProgressBar progressBar;
    CategoryAdapter adapter;
    ArrayList<Category> categoryList;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            FirebaseApp.initializeApp(this);
            mAuth = FirebaseAuth.getInstance();
        }
        sessionManager = SessionManager.getInstance(this);
        cartService = new CartService(this);

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize views
        btnUserMenu = findViewById(R.id.btn_user_menu);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Set up user menu click listener with debug Toast
        btnUserMenu.setOnClickListener(v -> {
            Toast.makeText(this, "Button clicked", Toast.LENGTH_SHORT).show();
            showUserMenu();
        });

        // Make sure button is clickable and visible
        btnUserMenu.setClickable(true);
        btnUserMenu.setEnabled(true);
        btnUserMenu.setVisibility(View.VISIBLE);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBarCategory);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        dbHelper = new DatabaseHelper(this);
        loadCategories();

        ArrayList<SliderItems> items = new ArrayList<>();
        items.add(new SliderItems("banner1"));
        items.add(new SliderItems("banner2"));
        items.add(new SliderItems("banner3"));

        banners(items);
    }


    private void loadCategories() {
        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        categoryList = dbHelper.getAllCategories();

        adapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void banners(ArrayList<SliderItems> items) {
        binding.viewpager2.setAdapter(new SliderAdapter(items, binding.viewpager2));
        binding.viewpager2.setClipChildren(false);
        binding.viewpager2.setClipToPadding(false);
        binding.viewpager2.setOffscreenPageLimit(3);
        binding.viewpager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        binding.viewpager2.setPageTransformer(compositePageTransformer);
        binding.progressBarBanner.setVisibility(View.GONE);


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

        try {
            PopupMenu popup = new PopupMenu(MainActivity.this, btnUserMenu);
            popup.getMenuInflater().inflate(R.menu.user_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                try {
                    if (itemId == R.id.action_profile) {
                        startActivity(new Intent(this, UserProfileActivity.class));
                        return true;
                    } else if (itemId == R.id.action_orders) {
                        if (!isUserLoggedIn()) {
                            Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        Intent intent = new Intent(this, OrderHistoryActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.action_restaurant_info) {
                        startActivity(new Intent(this, RestaurantInfoActivity.class));
                        return true;
                    } else if (itemId == R.id.action_settings) {
                        Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (itemId == R.id.action_logout) {
                        logout();
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return false;
            });

            popup.show();
        } catch (Exception e) {
            // Debug any potential errors
            Toast.makeText(this, "Error showing menu: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private boolean isUserLoggedIn() {
        boolean logged = sessionManager.isLoggedIn();
        // Debug Toast
//        Toast.makeText(this, "User logged in: " + logged, Toast.LENGTH_SHORT).show();
        return logged;
    }

    private void logout() {
        sessionManager.logout();
        //dang xuat tai khoan firebase
        mAuth.signOut();
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
            Toast.makeText(this, "Trang chủ", Toast.LENGTH_SHORT).show();
            return true;
        } else if (itemId == R.id.navigation_menu) {
            Toast.makeText(this, "Thực đơn", Toast.LENGTH_SHORT).show();
            return true;

        }else if (itemId == R.id.navigation_cart) {
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
            startActivity(new Intent(MainActivity.this, CartListActivity.class));
            return true;
        } else if (itemId == R.id.navigation_chat){
            if (!isUserLoggedIn()) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Đăng nhập")
                    .setMessage("Bạn cần đăng nhập để sử dụng chat")
                    .setPositiveButton("Đăng nhập", (dialog, which) -> {
                        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
                return false;
            }

            String uid = mAuth.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uid);

            databaseReference.child("role").get().addOnSuccessListener(dataSnapshot -> {
                if(dataSnapshot.exists()){
                    String role = dataSnapshot.getValue(String.class);
                    if(role.equalsIgnoreCase("CUSTOMER")){
                        User user = new User("hlX4nY01RNOqv9pwZqanLe0axkE2", "Staff 2",
                                "05558886663");
                        Intent intent = new Intent(this, ChatDetailActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent, user);
                        startActivity(intent);
                        //startActivity(new Intent(this, ChatListStaffActivity.class));
                    } else if(role.equalsIgnoreCase("STAFF")){
                        startActivity(new Intent(this, ChatListStaffActivity.class));
                    }
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_orders) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}