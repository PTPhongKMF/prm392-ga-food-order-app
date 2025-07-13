package com.example.foodorderapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.FoodListAdapter;
import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.model.Foods;
import com.example.foodorderapp.utils.ImageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;

import java.util.ArrayList;

public class ListFoodActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ImageView backBtn;
    private TextView titleTxt;
    private ArrayList<Foods> foodList;
    private FoodListAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_food);
        initViews();
        loadData();
        setListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.foodListView);
        progressBar = findViewById(R.id.progressBar2);
        backBtn = findViewById(R.id.backBtn);
        titleTxt = findViewById(R.id.titleTxt);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        dbHelper = new DatabaseHelper(this);

    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        int categoryId = intent.getIntExtra("CategoryId", -1);
        String categoryName = intent.getStringExtra("CategoryName");

        ArrayList<Foods> foodList;
        if (categoryId != -1) {
            foodList = dbHelper.getFoodsByCategoryId(categoryId);
            titleTxt.setText(categoryName);
        } else {
            foodList = dbHelper.getAllFoods();
            titleTxt.setText("All Foods");
        }

        adapter = new FoodListAdapter(foodList);
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);
    }

    private void setListeners() {
        backBtn.setOnClickListener(v -> finish());
    }
}