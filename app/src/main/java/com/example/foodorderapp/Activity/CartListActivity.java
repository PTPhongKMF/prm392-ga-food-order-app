package com.example.foodorderapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.CartAdapter;
import com.example.foodorderapp.model.CartItem;
import com.example.foodorderapp.services.CartService;

import java.util.ArrayList;

public class CartListActivity extends AppCompatActivity implements CartAdapter.CartUpdateListener {
    private RecyclerView cartRecyclerView;
    private Button checkoutBtn;
    private ImageView backBtn;
    private TextView clearAllBtn;
    private CartAdapter adapter;
    private CartService cartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadCartItems();
    }

    private void initViews() {
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        checkoutBtn = findViewById(R.id.checkoutBtn);
        backBtn = findViewById(R.id.backBtn);
        clearAllBtn = findViewById(R.id.clearAllBtn);
        cartService = new CartService(this);
    }

    private void setupRecyclerView() {
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        backBtn.setOnClickListener(v -> finish());
        
        clearAllBtn.setOnClickListener(v -> {
            if (cartService.clearCart()) {
                Toast.makeText(this, "Đã xóa tất cả sản phẩm", Toast.LENGTH_SHORT).show();
                loadCartItems();
            } else {
                Toast.makeText(this, "Không thể xóa giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
        
        checkoutBtn.setOnClickListener(v -> {
            ArrayList<CartItem> cartItems = cartService.getAllCartItems();
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, OrderCheckoutActivity.class));
        });
    }

    private void loadCartItems() {
        ArrayList<CartItem> cartItems = cartService.getAllCartItems();
        adapter = new CartAdapter(cartItems, this);
        cartRecyclerView.setAdapter(adapter);
        
        // Show/hide checkout button based on cart items
        checkoutBtn.setVisibility(cartItems.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCartUpdated() {
        loadCartItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
    }
}