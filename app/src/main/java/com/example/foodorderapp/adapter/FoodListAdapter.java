package com.example.foodorderapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.Activity.DetailActivity;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.Foods;
import com.example.foodorderapp.services.CartService;
import com.example.foodorderapp.utils.ImageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.viewholder> {
    ArrayList<Foods> items;
    Context context;
    private CartService cartService;

    public FoodListAdapter(ArrayList<Foods> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public FoodListAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        cartService = new CartService(context);
        return new viewholder(LayoutInflater.from(context).inflate(R.layout.viewholder_list_food,parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FoodListAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.timeTxt.setText(items.get(position).getTimeValue()+" phút");
        NumberFormat vnFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        vnFormat.setMaximumFractionDigits(0);
        String priceFormatted = vnFormat.format(items.get(position).getPrice()) + " đ";
        holder.priceTxt.setText(priceFormatted);
        holder.rateTxt.setText("" + items.get(position).getStar());
        String imgName = items.get(position).getImagePath();
        // Sửa logic hiển thị ảnh
        if (imgName == null || imgName.isEmpty()) {
            holder.pic.setImageResource(R.drawable.pizza_1);
        } else {
            int resourceId = ImageManager.getImageResource(imgName);
            if (resourceId != R.drawable.pizza_1 || imgName.equals("pizza_1")) {
                holder.pic.setImageResource(resourceId);
            } else {
                File imageFile = ImageManager.getImageFile(context, imgName);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    holder.pic.setImageBitmap(bitmap);
                } else {
                    holder.pic.setImageResource(R.drawable.pizza_1);
                }
            }
        }
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", items.get(position));
            context.startActivity(intent);
        });

        holder.addToCartBtn.setOnClickListener(v -> {
            Foods food = items.get(position);
            long result = cartService.addToCart(food.getId(), 1);
            if (result > 0) {
                Toast.makeText(context, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Không thể thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder{
        TextView titleTxt, priceTxt, rateTxt, timeTxt, addToCartBtn;
        ImageView pic;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            rateTxt = itemView.findViewById(R.id.ratingTxt);
            timeTxt = itemView.findViewById(R.id.timeTxt);
            pic = itemView.findViewById(R.id.img);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
        }
    }
}
