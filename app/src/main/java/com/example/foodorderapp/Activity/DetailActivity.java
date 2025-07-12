package com.example.foodorderapp.Activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.databinding.ActivityDetailBinding;
import com.example.foodorderapp.model.Foods;
import com.example.foodorderapp.services.CartService;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    private CartService cartService;
    private boolean fromCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cartService = new CartService(this);
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
            if (fromCart) {
                finish();
            } else {
                finish();
            }
        });
        
        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar() + " Sao");
        binding.ratingBar.setRating((float)object.getStar());
        binding.totalTxt.setText("$" + (num * object.getPrice()));
        String imgName = object.getImagePath();
        int imgResourceId = getResources().getIdentifier(imgName, "drawable", getPackageName());
        binding.pic.setImageResource(imgResourceId);
        
        binding.plusBtn.setOnClickListener(v -> {
                num += 1;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText("$" + (num * object.getPrice()));
        });

        binding.minusBtn.setOnClickListener(v -> {
            if(num > 0) {
                num -= 1;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText("$" + (num * object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            if (num > 0) {
                long result = cartService.addToCart(object.getId(), num);
                if (result > 0) {
                    Toast.makeText(this, "Đã thêm " + num + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Vui lòng chọn ít nhất 1 sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
        fromCart = getIntent().getBooleanExtra("from_cart", false);
    }
}