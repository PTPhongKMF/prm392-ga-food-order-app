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
import com.example.foodorderapp.utils.ImageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;

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
        // Sửa logic hiển thị ảnh
        if (imgName == null || imgName.isEmpty()) {
            binding.pic.setImageResource(R.drawable.pizza_1);
        } else {
            int resourceId = ImageManager.getImageResource(imgName);
            if (resourceId != R.drawable.pizza_1 || imgName.equals("pizza_1")) {
                binding.pic.setImageResource(resourceId);
            } else {
                File imageFile = ImageManager.getImageFile(this, imgName);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    binding.pic.setImageBitmap(bitmap);
                } else {
                    binding.pic.setImageResource(R.drawable.pizza_1);
                }
            }
        }
        
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