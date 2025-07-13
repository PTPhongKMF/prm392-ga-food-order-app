package com.example.foodorderapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.OrderHistoryAdapter;
import com.example.foodorderapp.model.Order;
import com.example.foodorderapp.model.OrderStatus;
import com.example.foodorderapp.model.PaymentStatus;
import com.example.foodorderapp.services.OrderService;
import com.example.foodorderapp.utils.OrderStatusUtils;
import com.example.foodorderapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private RecyclerView orderRecyclerView;
    private OrderHistoryAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private ImageView backBtn;
    private SessionManager sessionManager;
    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_order_history);
            initViews();
            setupRecyclerView();
            loadOrders();
            backBtn.setOnClickListener(v -> finish());
        } catch (Exception e) {
            System.out.println("Debug - Error in onCreate: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void initViews() {
        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyView = findViewById(R.id.emptyView);
        backBtn = findViewById(R.id.backBtn);
        sessionManager = SessionManager.getInstance(this);
        orderService = new OrderService(this);
    }

    private void setupRecyclerView() {
        adapter = new OrderHistoryAdapter(new ArrayList<>(), this);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderRecyclerView.setAdapter(adapter);
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        if (!sessionManager.isLoggedIn()) {
            showEmptyView();
            return;
        }

        List<Order> orders = orderService.getUserOrders();
        
        if (orders.isEmpty()) {
            showEmptyView();
        } else {
            try {
                adapter.updateOrders(orders);
                orderRecyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } catch (Exception e) {
                System.out.println("Debug - Error updating adapter: " + e.getMessage());
                e.printStackTrace();
                showEmptyView();
            }
        }
        progressBar.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        orderRecyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }
} 