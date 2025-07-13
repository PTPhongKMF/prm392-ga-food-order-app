package com.example.foodorderapp.utils;

import com.example.foodorderapp.model.OrderStatus;
import com.example.foodorderapp.model.PaymentStatus;

public class OrderStatusUtils {
    public static String getStatusDisplayText(String status) {
        if (status == null) return "N/A";
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status);
            switch (orderStatus) {
                case PENDING:
                    return "Chờ xác nhận";
                case CONFIRMED:
                    return "Đã xác nhận";
                case PREPARING:
                    return "Đang chuẩn bị";
                case DELIVERING:
                    return "Đang giao";
                case COMPLETED:
                    return "Đã hoàn thành";
                case CANCELLED:
                    return "Đã hủy";
                default:
                    return status;
            }
        } catch (IllegalArgumentException e) {
            // If not an OrderStatus, try PaymentStatus
            try {
                PaymentStatus paymentStatus = PaymentStatus.valueOf(status);
                switch (paymentStatus) {
                    case PENDING:
                        return "Chờ thanh toán";
                    case PAID:
                        return "Đã thanh toán";
                    case FAILED:
                        return "Thanh toán thất bại";
                    case REFUNDED:
                        return "Đã hoàn tiền";
                    default:
                        return status;
                }
            } catch (IllegalArgumentException ex) {
                return status;
            }
        }
    }
} 