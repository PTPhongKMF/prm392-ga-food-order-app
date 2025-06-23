package com.example.foodorderapp.UserActivities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.Database.DatabaseHelper;
import com.example.foodorderapp.databinding.ActivitySignupBinding;
import com.example.foodorderapp.Models.User;
import com.example.foodorderapp.Models.UserRole;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.signupButton.setOnClickListener(v -> attemptSignup());
        binding.loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptSignup() {
        String name = binding.nameEditText.getText().toString().trim();
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String phone = binding.phoneEditText.getText().toString().trim();
        String address = binding.addressEditText.getText().toString().trim();

        if (!validateForm(name, email, password, phone, address)) {
            return;
        }

        binding.signupButton.setEnabled(false);

        // Check if email already exists
        if (dbHelper.getUserByEmail(email) != null) {
            binding.signupButton.setEnabled(true);
            binding.emailEditText.setError("Email already exists");
            return;
        }

        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setPhone(phone);
        newUser.setAddress(address);
        newUser.setRole(UserRole.CUSTOMER);
        newUser.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));

        long userId = dbHelper.createUser(newUser);
        binding.signupButton.setEnabled(true);

        if (userId != -1) {
            Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm(String name, String email, String password, String phone, String address) {
        boolean valid = true;

        if (TextUtils.isEmpty(name)) {
            binding.nameEditText.setError("Required");
            valid = false;
        }

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Invalid email address");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Required");
            valid = false;
        } else if (password.length() < 6) {
            binding.passwordEditText.setError("Password too short");
            valid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            binding.phoneEditText.setError("Required");
            valid = false;
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            binding.phoneEditText.setError("Invalid phone number");
            valid = false;
        }

        if (TextUtils.isEmpty(address)) {
            binding.addressEditText.setError("Required");
            valid = false;
        }

        return valid;
    }
} 