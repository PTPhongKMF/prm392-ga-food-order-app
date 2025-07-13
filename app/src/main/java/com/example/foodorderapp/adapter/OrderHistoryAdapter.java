package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.Activity.OrderHistoryActivity;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.Order;
import com.example.foodorderapp.model.OrderStatus;
import com.example.foodorderapp.model.PaymentStatus;
import com.example.foodorderapp.services.OrderService;
import com.example.foodorderapp.utils.OrderStatusUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import android.content.Context;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {
    private List<Order> orders;
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public OrderHistoryAdapter(List<Order> orders, Context context) {
        this.orders = orders;
        this.context = context;
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
        private final Spinner spinnerOrderStatus;
        private final Spinner spinnerPaymentStatus;
        private OrderService orderService;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdText = itemView.findViewById(R.id.orderIdText);
            orderStatusText = itemView.findViewById(R.id.orderStatusText);
            paymentStatusText = itemView.findViewById(R.id.paymentStatusText);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderAddressText = itemView.findViewById(R.id.orderAddressText);
            orderNotesText = itemView.findViewById(R.id.orderNotesText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
            spinnerOrderStatus = itemView.findViewById(R.id.spinnerOrderStatus);
            spinnerPaymentStatus = itemView.findViewById(R.id.spinnerPaymentStatus);
            orderService = new OrderService(itemView.getContext());
        }

        public void bind(Order order) {
            try {
                orderIdText.setText("Order #" + order.getId());
                // Handle Order Status
                String orderStatus = order.getStatus() != null ? order.getStatus().name() : "N/A";
                orderStatusText.setText(OrderStatusUtils.getStatusDisplayText(orderStatus));
                // Handle Payment Status
                String paymentStatus = order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "N/A";
                paymentStatusText.setText(OrderStatusUtils.getStatusDisplayText(paymentStatus));
                orderDateText.setText(order.getCreatedAt() != null ? order.getCreatedAt() : "N/A");
                orderAddressText.setText(order.getAddress() != null ? order.getAddress() : "N/A");
                orderNotesText.setText(order.getNote() != null ? order.getNote() : "N/A");
                orderTotalText.setText(currencyFormat.format(order.getTotalAmount()));

                // Setup OrderStatus Spinner
                ArrayAdapter<OrderStatus> orderStatusAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, OrderStatus.values());
                orderStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerOrderStatus.setAdapter(orderStatusAdapter);
                if (order.getStatus() != null) {
                    spinnerOrderStatus.setSelection(order.getStatus().ordinal());
                }
                spinnerOrderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        OrderStatus selectedStatus = (OrderStatus) parent.getItemAtPosition(position);
                        if (order.getStatus() != selectedStatus) {
                            order.setStatus(selectedStatus);
                            orderService.updateOrderStatus(order.getId(), selectedStatus);
                            orderStatusText.setText(OrderStatusUtils.getStatusDisplayText(selectedStatus.name()));
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                // Setup PaymentStatus Spinner
                ArrayAdapter<PaymentStatus> paymentStatusAdapter = new ArrayAdapter<>(itemView.getContext(), android.R.layout.simple_spinner_item, PaymentStatus.values());
                paymentStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPaymentStatus.setAdapter(paymentStatusAdapter);
                if (order.getPaymentStatus() != null) {
                    spinnerPaymentStatus.setSelection(order.getPaymentStatus().ordinal());
                }
                spinnerPaymentStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        PaymentStatus selectedStatus = (PaymentStatus) parent.getItemAtPosition(position);
                        if (order.getPaymentStatus() != selectedStatus) {
                            order.setPaymentStatus(selectedStatus);
                            orderService.updatePaymentStatus(order.getId(), selectedStatus);
                            paymentStatusText.setText(OrderStatusUtils.getStatusDisplayText(selectedStatus.name()));
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            } catch (Exception e) {
                System.out.println("Debug - Error binding order: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 