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
import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.model.CartItem;
import com.example.foodorderapp.model.Foods;
import com.example.foodorderapp.services.CartService;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<CartItem> items;
    private Context context;
    private CartService cartService;
    private CartUpdateListener listener;
    private DatabaseHelper dbHelper;

    public interface CartUpdateListener {
        void onCartUpdated();
    }

    public CartAdapter(ArrayList<CartItem> items, CartUpdateListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        cartService = new CartService(context);
        dbHelper = new DatabaseHelper(context);
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.titleTxt.setText(item.getProductTitle());
        holder.priceTxt.setText("$" + item.getProductPrice());
        holder.quantityTxt.setText(String.valueOf(item.getProductQuantity()));

        holder.plusBtn.setOnClickListener(v -> {
            int quantity = item.getProductQuantity() + 1;
            updateCartItem(item, quantity, holder);
        });

        holder.minusBtn.setOnClickListener(v -> {
            int quantity = item.getProductQuantity() - 1;
            if (quantity > 0) {
                updateCartItem(item, quantity, holder);
            }
        });

        holder.deleteBtn.setOnClickListener(v -> {
            if (cartService.removeFromCart(item.getCartId())) {
                Toast.makeText(context, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                listener.onCartUpdated();
            } else {
                Toast.makeText(context, "Không thể xóa sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            ArrayList<Foods> foods = dbHelper.getAllFoods();
            Foods targetFood = null;
            for (Foods food : foods) {
                if (food.getId() == item.getProductId()) {
                    targetFood = food;
                    break;
                }
            }
            
            if (targetFood != null) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("object", targetFood);
                intent.putExtra("from_cart", true);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartItem(CartItem item, int quantity, ViewHolder holder) {
        long result = cartService.addToCart(item.getProductId(), quantity - item.getProductQuantity());
        if (result > 0) {
            item.setProductQuantity(quantity);
            holder.quantityTxt.setText(String.valueOf(quantity));
            listener.onCartUpdated();
        } else {
            Toast.makeText(context, "Không thể cập nhật số lượng", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, quantityTxt, plusBtn, minusBtn;
        ImageView deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            quantityTxt = itemView.findViewById(R.id.quantityTxt);
            plusBtn = itemView.findViewById(R.id.plusBtn);
            minusBtn = itemView.findViewById(R.id.minusBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
} 