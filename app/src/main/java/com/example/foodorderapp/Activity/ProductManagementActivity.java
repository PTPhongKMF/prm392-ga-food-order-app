package com.example.foodorderapp.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.foodorderapp.R;
import com.example.foodorderapp.database.CategoryDatabaseHelper;
import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.database.FoodDatabaseHelper;
import com.example.foodorderapp.model.Category;
import com.example.foodorderapp.model.Foods;
import com.example.foodorderapp.utils.ImageManager;
import com.example.foodorderapp.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ProductManagementActivity extends AppCompatActivity implements ProductListFragment.OnProductActionListener {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabAdd;
    private ImageButton btnBack, btnAddProduct;
    private BottomNavigationView bottomNavigation;
    private SessionManager sessionManager;
    
    private List<Category> categories;
    private CategoryDatabaseHelper categoryDatabaseHelper;
    private FoodDatabaseHelper foodDatabaseHelper;
    private ProductPagerAdapter pagerAdapter;

    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);
        
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        categoryDatabaseHelper = new CategoryDatabaseHelper(this, databaseHelper);
        foodDatabaseHelper = new FoodDatabaseHelper(this);
        sessionManager = SessionManager.getInstance(this);
        
        initViews();
        setupToolbar();
        loadCategories();
        setupViewPager();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        fabAdd = findViewById(R.id.fab_add);
        btnBack = findViewById(R.id.btn_back);
        btnAddProduct = findViewById(R.id.btn_add_product);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void loadCategories() {
        categories = categoryDatabaseHelper.getAllCategories();
    }

    private void setupViewPager() {
        pagerAdapter = new ProductPagerAdapter(this, categories);
        viewPager.setAdapter(pagerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            Category category = categories.get(position);
            tab.setText(category.getName());
        }).attach();
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnAddProduct.setOnClickListener(v -> showAddProductDialog());
        
        fabAdd.setOnClickListener(v -> {
            int currentPosition = viewPager.getCurrentItem();
            if (currentPosition < categories.size()) {
                Category currentCategory = categories.get(currentPosition);
                showAddProductDialog(currentCategory.getId());
            }
        });
    }
    
    private void setupBottomNavigation() {
        // Set default selected item
        bottomNavigation.setSelectedItemId(R.id.nav_products);
        
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_products) {
                // Already on products page, do nothing
                return true;
            } else if (itemId == R.id.nav_chat) {
                // Navigate to chat
                Intent intent = new Intent(this, ChatListStaffActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_orders) {
                // Navigate to order management
                Intent intent = new Intent(this, OrderManagementActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_logout) {
                // Show logout confirmation
                showLogoutConfirmation();
                return true;
            }
            
            return false;
        });
    }
    
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    
    private void logout() {
        // Clear session
        sessionManager.logout();
        
        // Navigate to login
        Intent intent = new Intent(this, com.example.foodorderapp.UserService.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onEditProduct(Foods product) {
        showEditProductDialog(product);
    }

    @Override
    public void onDeleteProduct(Foods product) {
        // This is handled in the fragment
    }

    @Override
    public void onAddProduct(int categoryId) {
        showAddProductDialog(categoryId);
    }

    private void showAddProductDialog() {
        showAddProductDialog(-1);
    }

    private void showAddProductDialog(int categoryId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Initialize dialog views
        TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title);
        ImageView imgPreview = dialogView.findViewById(R.id.img_preview);
        Button btnSelectImage = dialogView.findViewById(R.id.btn_select_image);
        TextInputEditText etTitle = dialogView.findViewById(R.id.et_title);
        TextInputEditText etDescription = dialogView.findViewById(R.id.et_description);
        TextInputEditText etPrice = dialogView.findViewById(R.id.et_price);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_category);
        TextInputEditText etStar = dialogView.findViewById(R.id.et_star);
        TextInputEditText etTimeValue = dialogView.findViewById(R.id.et_time_value);
        CheckBox cbBestFood = dialogView.findViewById(R.id.cb_best_food);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        // Set dialog title
        tvDialogTitle.setText("Thêm Sản Phẩm");

        // Setup category spinner
        setupCategorySpinner(spinnerCategory, categoryId);

        // Setup image selection
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        
        // Update image preview if we have a selected image
        if (!selectedImagePath.isEmpty()) {
            updateImagePreview(imgPreview, selectedImagePath);
        }

        // Setup buttons
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            if (validateForm(etTitle, etPrice, etStar, etTimeValue)) {
                Foods newProduct = createProductFromForm(
                    -1, // New product ID will be generated
                    etTitle.getText().toString(),
                    etDescription.getText().toString(),
                    selectedImagePath.isEmpty() ? "pizza_1" : selectedImagePath,
                    Double.parseDouble(etPrice.getText().toString()),
                    Double.parseDouble(etStar.getText().toString()),
                    Integer.parseInt(etTimeValue.getText().toString()),
                    cbBestFood.isChecked(),
                    ((Category) spinnerCategory.getSelectedItem()).getId()
                );

                boolean success = foodDatabaseHelper.addFood(newProduct);
                if (success) {
                    Toast.makeText(this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                    refreshCurrentFragment();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Lỗi khi thêm sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void showEditProductDialog(Foods product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_product_form, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Initialize dialog views
        TextView tvDialogTitle = dialogView.findViewById(R.id.tv_dialog_title);
        ImageView imgPreview = dialogView.findViewById(R.id.img_preview);
        Button btnSelectImage = dialogView.findViewById(R.id.btn_select_image);
        TextInputEditText etTitle = dialogView.findViewById(R.id.et_title);
        TextInputEditText etDescription = dialogView.findViewById(R.id.et_description);
        TextInputEditText etPrice = dialogView.findViewById(R.id.et_price);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_category);
        TextInputEditText etStar = dialogView.findViewById(R.id.et_star);
        TextInputEditText etTimeValue = dialogView.findViewById(R.id.et_time_value);
        CheckBox cbBestFood = dialogView.findViewById(R.id.cb_best_food);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave = dialogView.findViewById(R.id.btn_save);

        // Set dialog title
        tvDialogTitle.setText("Sửa Sản Phẩm");

        // Populate form with existing data
        etTitle.setText(product.getTitle());
        etDescription.setText(product.getDescription());
        etPrice.setText(String.valueOf(product.getPrice()));
        etStar.setText(String.valueOf(product.getStar()));
        etTimeValue.setText(String.valueOf(product.getTimeValue()));
        cbBestFood.setChecked(product.isBestFood());

        // Setup category spinner
        setupCategorySpinner(spinnerCategory, product.getCategoryId());

        // Setup image selection
        selectedImagePath = product.getImagePath();
        updateImagePreview(imgPreview, selectedImagePath);
        
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Setup buttons
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            if (validateForm(etTitle, etPrice, etStar, etTimeValue)) {
                Foods updatedProduct = createProductFromForm(
                    product.getId(),
                    etTitle.getText().toString(),
                    etDescription.getText().toString(),
                    selectedImagePath,
                    Double.parseDouble(etPrice.getText().toString()),
                    Double.parseDouble(etStar.getText().toString()),
                    Integer.parseInt(etTimeValue.getText().toString()),
                    cbBestFood.isChecked(),
                    ((Category) spinnerCategory.getSelectedItem()).getId()
                );

                boolean success = foodDatabaseHelper.updateFood(updatedProduct);
                if (success) {
                    Toast.makeText(this, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    refreshCurrentFragment();
                    dialog.dismiss();
                } else {
                    Toast.makeText(this, "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void setupCategorySpinner(Spinner spinner, int selectedCategoryId) {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Set selected category
        if (selectedCategoryId != -1) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == selectedCategoryId) {
                    spinner.setSelection(i);
                    break;
                }
            }
        }
    }

    private boolean validateForm(TextInputEditText etTitle, TextInputEditText etPrice, 
                               TextInputEditText etStar, TextInputEditText etTimeValue) {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Vui lòng nhập tên sản phẩm");
            return false;
        }
        if (etPrice.getText().toString().trim().isEmpty()) {
            etPrice.setError("Vui lòng nhập giá");
            return false;
        }
        if (etStar.getText().toString().trim().isEmpty()) {
            etStar.setError("Vui lòng nhập đánh giá");
            return false;
        }
        if (etTimeValue.getText().toString().trim().isEmpty()) {
            etTimeValue.setError("Vui lòng nhập thời gian chế biến");
            return false;
        }
        return true;
    }

    private Foods createProductFromForm(int id, String title, String description, String imagePath,
                                      double price, double star, int timeValue, boolean bestFood, int categoryId) {
        Foods product = new Foods();
        product.setId(id);
        product.setTitle(title);
        product.setDescription(description);
        product.setImagePath(imagePath);
        product.setPrice(price);
        product.setStar(star);
        product.setTimeValue(timeValue);
        product.setBestFood(bestFood);
        product.setCategoryId(categoryId);
        product.setLocationId(1); // Default location
        product.setTimeId(1); // Default time ID
        return product;
    }

    private void updateImagePreview(ImageView imageView, String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            imageView.setImageResource(R.drawable.pizza_1);
            return;
        }
        
        // Check if it's a built-in image
        int resourceId = ImageManager.getImageResource(imagePath);
        if (resourceId != R.drawable.pizza_1 || imagePath.equals("pizza_1")) {
            imageView.setImageResource(resourceId);
        } else {
            // Load from internal storage
            try {
                File imageFile = ImageManager.getImageFile(this, imagePath);
                if (imageFile.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.pizza_1);
                }
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.pizza_1);
            }
        }
    }

    private void refreshCurrentFragment() {
        int currentPosition = viewPager.getCurrentItem();
        Fragment currentFragment = pagerAdapter.getFragment(currentPosition);
        if (currentFragment instanceof ProductListFragment) {
            ((ProductListFragment) currentFragment).refreshProducts();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Copy image to internal storage
                selectedImagePath = ImageManager.copyImageToInternalStorage(this, selectedImageUri);
                Toast.makeText(this, "Ảnh đã được chọn và lưu: " + selectedImagePath, Toast.LENGTH_SHORT).show();
                
                // Update image preview if dialog is open
                updateImagePreviewInDialog();
            }
        }
    }
    
    private void updateImagePreviewInDialog() {
        // This method will be called to update image preview in the dialog
        // Implementation depends on how you want to handle the preview
    }

    // ViewPager2 Adapter
    private static class ProductPagerAdapter extends FragmentStateAdapter {
        private final List<Category> categories;
        private final List<Fragment> fragments = new ArrayList<>();

        public ProductPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Category> categories) {
            super(fragmentActivity);
            this.categories = categories;
            
            // Create fragments for each category
            for (Category category : categories) {
                fragments.add(ProductListFragment.newInstance(category.getId(), category.getName()));
            }
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        public Fragment getFragment(int position) {
            return fragments.get(position);
        }
    }
} 