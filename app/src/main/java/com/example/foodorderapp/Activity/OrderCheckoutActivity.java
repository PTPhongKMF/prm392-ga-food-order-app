package com.example.foodorderapp.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.services.OrderService;
import com.example.foodorderapp.utils.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class OrderCheckoutActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    
    private TextInputLayout deliveryAddressLayout, phoneLayout;
    private TextInputEditText deliveryAddressInput, phoneInput, notesInput;
    private Button placeOrderBtn;
    private ImageView backBtn;
    private ImageButton getCurrentLocationBtn;
    private OrderService orderService;
    private SessionManager sessionManager;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_checkout);
        
        initViews();
        setupServices();
        setupListeners();
        prefillUserData();
    }

    private void initViews() {
        deliveryAddressLayout = findViewById(R.id.deliveryAddressLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        deliveryAddressInput = findViewById(R.id.deliveryAddressInput);
        phoneInput = findViewById(R.id.phoneInput);
        notesInput = findViewById(R.id.notesInput);
        placeOrderBtn = findViewById(R.id.placeOrderBtn);
        backBtn = findViewById(R.id.backBtn);
        getCurrentLocationBtn = findViewById(R.id.getCurrentLocationBtn);
    }

    private void setupServices() {
        orderService = new OrderService(this);
        sessionManager = SessionManager.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupListeners() {
        placeOrderBtn.setOnClickListener(v -> validateAndPlaceOrder());
        backBtn.setOnClickListener(v -> finish());
        getCurrentLocationBtn.setOnClickListener(v -> getCurrentLocation());
    }

    private void getCurrentLocation() {
        // First check if location services are enabled
        if (!isLocationEnabled()) {
            Toast.makeText(this, "Vui lòng bật dịch vụ vị trí", Toast.LENGTH_LONG).show();
            // Open location settings
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        
        getLocationAndUpdateAddress();
    }

    private boolean isLocationEnabled() {
        android.location.LocationManager locationManager = 
            (android.location.LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) ||
                   locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void getLocationAndUpdateAddress() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Show loading indicator
        Toast.makeText(this, "Đang lấy vị trí...", Toast.LENGTH_SHORT).show();
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        updateAddressFromLocation(location);
                    } else {
                        Toast.makeText(this, "Không thể lấy vị trí. Giữ nguyên địa chỉ hiện tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this, "Lỗi khi lấy vị trí. Giữ nguyên địa chỉ hiện tại", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1
            );
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);
                if (addressLine != null) {
                    deliveryAddressInput.setText(addressLine);
                } else {
                    Toast.makeText(this, "Không thể lấy địa chỉ từ vị trí này. Giữ nguyên địa chỉ hiện tại", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Không tìm thấy địa chỉ cho vị trí này. Giữ nguyên địa chỉ hiện tại", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lấy địa chỉ. Giữ nguyên địa chỉ hiện tại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndUpdateAddress();
            } else {
                Toast.makeText(this, "Cần quyền truy cập vị trí để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void prefillUserData() {
        User currentUser = sessionManager.getUser();
        if (currentUser != null) {
            String userAddress = currentUser.getAddress();
            String userPhone = currentUser.getPhone();
            
            if (!TextUtils.isEmpty(userAddress)) {
                deliveryAddressInput.setText(userAddress);
            }
            if (!TextUtils.isEmpty(userPhone)) {
                phoneInput.setText(userPhone);
            }
        }
    }

    private void validateAndPlaceOrder() {
        boolean isValid = true;
        String address = deliveryAddressInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String notes = notesInput.getText().toString().trim();

        if (TextUtils.isEmpty(address)) {
            deliveryAddressLayout.setError("Địa chỉ giao hàng là bắt buộc");
            isValid = false;
        } else {
            deliveryAddressLayout.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Số điện thoại là bắt buộc");
            isValid = false;
        } else if (!phone.matches("^[0-9]{10}$")) {
            phoneLayout.setError("Vui lòng nhập số điện thoại hợp lệ (10 chữ số)");
            isValid = false;
        } else {
            phoneLayout.setError(null);
        }

        if (isValid) {
            placeOrder(address, phone, notes);
        }
    }

    private void placeOrder(String address, String phone, String notes) {
        long orderId = orderService.checkout(address, notes);
        if (orderId > 0) {
            showPaymentDialog(orderId);
        } else {
            Toast.makeText(this, "Đặt hàng thất bại. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
        }
    }

    private void showPaymentDialog(long orderId) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment_qr);
        dialog.setCancelable(false);

        TextView orderIdText = dialog.findViewById(R.id.orderIdText);
        Button backToMainBtn = dialog.findViewById(R.id.backToMainBtn);

        orderIdText.setText("Vui lòng bao gồm ID đơn hàng của bạn: " + orderId + " trong mô tả chuyển khoản");

        backToMainBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(OrderCheckoutActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }
}