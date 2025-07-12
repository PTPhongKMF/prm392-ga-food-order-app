package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.Activity.OrderHistoryActivity;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private List<Order> orders;
    private final OrderHistoryActivity activity;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public OrderHistoryAdapter(List<Order> orders, OrderHistoryActivity activity) {
        this.orders = orders;
        this.activity = activity;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderIdText;
        private final TextView orderStatusText;
        private final TextView paymentStatusText;
        private final TextView orderDateText;
        private final TextView orderAddressText;
        private final TextView orderNotesText;
        private final TextView orderTotalText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            paymentStatusText = itemView.findViewById(R.id.paymentStatusText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderAddressText = itemView.findViewById(R.id.orderAddressText);
            orderNotesText = itemView.findViewById(R.id.orderNotesText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
        }

        public void bind(Order order) {
            try {
                orderIdText.setText("Order #" + order.getId());
                
                // Handle Order Status
                String orderStatus = order.getStatus() != null ? order.getStatus().name() : "N/A";
                orderStatusText.setText(activity.getStatusDisplayText(orderStatus));
                
                // Handle Payment Status
                String paymentStatus = order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "N/A";
                paymentStatusText.setText(activity.getStatusDisplayText(paymentStatus));
                
                orderDateText.setText(order.getCreatedAt() != null ? order.getCreatedAt() : "N/A");
                orderAddressText.setText(order.getAddress() != null ? order.getAddress() : "N/A");
                orderNotesText.setText(order.getNote() != null ? order.getNote() : "N/A");
                orderTotalText.setText(currencyFormat.format(order.getTotalAmount()));
            } catch (Exception e) {
                System.out.println("Debug - Error binding order: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 