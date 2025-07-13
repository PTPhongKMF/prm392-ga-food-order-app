package com.example.foodorderapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.Foods;
import com.example.foodorderapp.utils.ImageManager;

import java.util.List;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductViewHolder> {

    private List<Foods> productList;
    private OnProductClickListener listener;
    private Context context;

    public interface OnProductClickListener {
        void onEditClick(Foods product);
        void onDeleteClick(Foods product);
    }

    public ProductManagementAdapter(List<Foods> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_management, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Foods product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProduct;
        private TextView tvTitle, tvPrice, tvRating, tvTime, tvBestFood;
        private ImageButton btnEdit, btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvBestFood = itemView.findViewById(R.id.tv_best_food);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(Foods product) {
            // Set product image
            String imageName = product.getImagePath();
            setProductImage(imageName);

            // Set product information
            tvTitle.setText(product.getTitle());
            tvPrice.setText("$" + String.format("%.2f", product.getPrice()));
            tvRating.setText(String.format("%.1f ★", product.getStar()));
            tvTime.setText(product.getTimeValue() + " phút");

            // Show best food badge if applicable
            if (product.isBestFood()) {
                tvBestFood.setVisibility(View.VISIBLE);
            } else {
                tvBestFood.setVisibility(View.GONE);
            }

            // Set click listeners
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(product);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(product);
                }
            });
        }
        
        private void setProductImage(String imageName) {
            if (imageName == null || imageName.isEmpty()) {
                imgProduct.setImageResource(R.drawable.pizza_1);
                return;
            }
            
            // Check if it's a built-in image
            int resourceId = ImageManager.getImageResource(imageName);
            if (resourceId != R.drawable.pizza_1 || imageName.equals("pizza_1")) {
                imgProduct.setImageResource(resourceId);
            } else {
                // Load from internal storage
                try {
                    File imageFile = ImageManager.getImageFile(context, imageName);
                    if (imageFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                        imgProduct.setImageBitmap(bitmap);
                    } else {
                        imgProduct.setImageResource(R.drawable.pizza_1);
                    }
                } catch (Exception e) {
                    imgProduct.setImageResource(R.drawable.pizza_1);
                }
            }
        }

    }
} 