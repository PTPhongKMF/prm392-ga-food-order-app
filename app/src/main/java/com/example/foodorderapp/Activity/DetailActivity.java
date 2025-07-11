package com.example.foodorderapp.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.databinding.ActivityDetailBinding;
import com.example.foodorderapp.model.Foods;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float)object.getStar());
        binding.totalTxt.setText((num * object.getPrice() + " $"));
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

    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}