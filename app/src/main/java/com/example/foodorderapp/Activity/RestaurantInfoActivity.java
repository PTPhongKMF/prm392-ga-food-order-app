package com.example.foodorderapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodorderapp.R;

public class RestaurantInfoActivity extends AppCompatActivity {

    private ImageButton btnBack, btnCall, btnDirection;
    private TextView tvPhone, tvAddress, tvHours, tvDescription;
    private WebView webView;
    
    // Restaurant coordinates
    private static final double RESTAURANT_LAT = 10.840929;
    private static final double RESTAURANT_LNG = 106.809746;
    private static final String RESTAURANT_PHONE = "0903641125";
    private static final String RESTAURANT_ADDRESS = "7 Đ. D1, Long Thạnh Mỹ, Thủ Đức, Hồ Chí Minh 700000";
    private static final String RESTAURANT_HOURS = "7:00 - 22:00 (Tất cả các ngày)";
    private static final String RESTAURANT_DESCRIPTION = "Quán ăn Đặt Là Có chuyên phục vụ các món ăn ngon, đa dạng với chất lượng cao và dịch vụ tốt. Chúng tôi cam kết mang đến cho khách hàng những trải nghiệm ẩm thực tuyệt vời nhất.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        // Initialize views
        initViews();
        setupToolbar();
        setupClickListeners();
        loadRestaurantInfo();
        setupMap();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnCall = findViewById(R.id.btn_call);
        btnDirection = findViewById(R.id.btn_direction);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvHours = findViewById(R.id.tv_hours);
        tvDescription = findViewById(R.id.tv_description);
        webView = findViewById(R.id.map);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnCall.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + RESTAURANT_PHONE.replace(" ", "")));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể mở ứng dụng gọi điện", Toast.LENGTH_SHORT).show();
            }
        });
        
        btnDirection.setOnClickListener(v -> {
            try {
                // Sử dụng OpenStreetMap để chỉ đường
                String uri = "https://www.openstreetmap.org/?mlat=" + RESTAURANT_LAT + 
                           "&mlon=" + RESTAURANT_LNG + "&zoom=15&layers=M";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Không thể mở bản đồ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRestaurantInfo() {
        tvPhone.setText(RESTAURANT_PHONE);
        tvAddress.setText(RESTAURANT_ADDRESS);
        tvHours.setText(RESTAURANT_HOURS);
        tvDescription.setText(RESTAURANT_DESCRIPTION);
    }

    private void setupMap() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Toast.makeText(RestaurantInfoActivity.this, "Bản đồ đã tải xong", Toast.LENGTH_SHORT).show();
            }
        });

        // Load OpenStreetMap với marker tại vị trí quán
        String url = "https://www.openstreetmap.org/?mlat=" + RESTAURANT_LAT + "&mlon=" + RESTAURANT_LNG + "&zoom=18";
        webView.loadUrl(url);
    }
} 