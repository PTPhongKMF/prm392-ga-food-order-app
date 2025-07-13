package com.example.foodorderapp.Activity;

import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.OrderHistoryAdapter;
import com.example.foodorderapp.model.Order;
import com.example.foodorderapp.services.OrderService;
import com.example.foodorderapp.utils.OrderStatusUtils;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {
    private RecyclerView rvOrders;
    private OrderHistoryAdapter adapter;
    private OrderService orderService;
    private TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);
        rvOrders = findViewById(R.id.rvOrders);
        tvTitle = findViewById(R.id.tvTitle);
        orderService = new OrderService(this);
        setupRecyclerView();
        loadOrders();
    }

    private void setupRecyclerView() {
        adapter = new OrderHistoryAdapter(null, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
        List<Order> orders = orderService.getAllOrders(); // Hàm này cần có trong OrderService
        if (orders != null && !orders.isEmpty()) {
            adapter.updateOrders(orders);
        } else {
            Toast.makeText(this, "Không có đơn hàng nào!", Toast.LENGTH_SHORT).show();
        }
    }
} 