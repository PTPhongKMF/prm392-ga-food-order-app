package com.example.foodorderapp.UserActivities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodorderapp.R;
import com.example.foodorderapp.Database.DatabaseHelper;
import com.example.foodorderapp.Models.User;
import com.example.foodorderapp.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class UserProfileActivity extends AppCompatActivity {

    private TextInputEditText editName, editEmail, editPhone, editAddress;
    private Button btnUpdateProfile;
    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize SessionManager and DatabaseHelper
        sessionManager = SessionManager.getInstance(this);
        dbHelper = new DatabaseHelper(this);

        // Get current user
        currentUser = sessionManager.getUser();
        if (currentUser == null) {
            Toast.makeText(this, "Lỗi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_profile);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize views
        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPhone = findViewById(R.id.edit_phone);
        editAddress = findViewById(R.id.edit_address);
        btnUpdateProfile = findViewById(R.id.btn_update_profile);

        // Load user data
        loadUserData();

        // Set up update button click listener
        btnUpdateProfile.setOnClickListener(v -> updateProfile());
    }

    private void loadUserData() {
        editName.setText(currentUser.getName());
        editEmail.setText(currentUser.getEmail());
        editPhone.setText(currentUser.getPhone());
        editAddress.setText(currentUser.getAddress());
        
        // Disable email editing as it's the unique identifier
        editEmail.setEnabled(false);
    }

    private void updateProfile() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        // Validate inputs
        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user object
        currentUser.setName(name);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);

        // Update in database
        int result = dbHelper.updateUser(currentUser);
        if (result > 0) {
            // Update in session
            sessionManager.saveUser(currentUser);
            Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
} 