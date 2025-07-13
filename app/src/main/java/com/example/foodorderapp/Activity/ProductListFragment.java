package com.example.foodorderapp.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.ProductManagementAdapter;
import com.example.foodorderapp.database.FoodDatabaseHelper;
import com.example.foodorderapp.model.Foods;

import java.util.ArrayList;
import java.util.List;

public class ProductListFragment extends Fragment {

    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_CATEGORY_NAME = "category_name";

    private int categoryId;
    private String categoryName;
    private List<Foods> productList;
    private ProductManagementAdapter adapter;
    private FoodDatabaseHelper databaseHelper;

    private RecyclerView recyclerProducts;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    private OnProductActionListener actionListener;

    public interface OnProductActionListener {
        void onEditProduct(Foods product);
        void onDeleteProduct(Foods product);
        void onAddProduct(int categoryId);
    }

    public static ProductListFragment newInstance(int categoryId, String categoryName) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORY_ID, categoryId);
        args.putString(ARG_CATEGORY_NAME, categoryName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(ARG_CATEGORY_ID);
            categoryName = getArguments().getString(ARG_CATEGORY_NAME);
        }
        productList = new ArrayList<>();
        databaseHelper = new FoodDatabaseHelper(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        loadProducts();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProductActionListener) {
            actionListener = (OnProductActionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnProductActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        actionListener = null;
    }

    private void initViews(View view) {
        recyclerProducts = view.findViewById(R.id.recycler_products);
        tvEmpty = view.findViewById(R.id.tv_empty);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void setupRecyclerView() {
        adapter = new ProductManagementAdapter(productList, new ProductManagementAdapter.OnProductClickListener() {
            @Override
            public void onEditClick(Foods product) {
                if (actionListener != null) {
                    actionListener.onEditProduct(product);
                }
            }

            @Override
            public void onDeleteClick(Foods product) {
                showDeleteConfirmationDialog(product);
            }
        });

        recyclerProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerProducts.setAdapter(adapter);
    }

    private void loadProducts() {
        showLoading(true);
        
        // Load products from database by category
        List<Foods> products = databaseHelper.getFoodsByCategory(categoryId);
        
        productList.clear();
        productList.addAll(products);
        adapter.notifyDataSetChanged();
        
        showLoading(false);
        updateEmptyState();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerProducts.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateEmptyState() {
        if (productList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerProducts.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerProducts.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmationDialog(Foods product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + product.getTitle() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteProduct(product);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(Foods product) {
        boolean success = databaseHelper.deleteFood(product.getId());
        if (success) {
            productList.remove(product);
            adapter.notifyDataSetChanged();
            updateEmptyState();
            Toast.makeText(requireContext(), "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Lỗi khi xóa sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshProducts() {
        loadProducts();
    }

    public void addProduct(Foods product) {
        productList.add(product);
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    public void updateProduct(Foods product) {
        for (int i = 0; i < productList.size(); i++) {
            if (productList.get(i).getId() == product.getId()) {
                productList.set(i, product);
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }
} 