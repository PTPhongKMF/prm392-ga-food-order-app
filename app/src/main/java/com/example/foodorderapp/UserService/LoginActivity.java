package com.example.foodorderapp.UserService;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.Activity.MainActivity;
import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.databinding.ActivityLoginBinding;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.utils.SessionManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        sessionManager = SessionManager.getInstance(this);
        setupClickListeners();
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void setupClickListeners() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());
        binding.signupLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            finish();
        });
    }

    private void attemptLogin() {
        String email = binding.emailEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (!validateForm(email, password)) {
            return;
        }

        binding.loginButton.setEnabled(false);
        
        User user = dbHelper.getUserByEmail(email);
        if (user != null && password.equals(user.getPassword())) {

            // Save user session
            sessionManager.saveUser(user);

            //login with firebase to get importance info later
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.trim(), password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            // Login successful
                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    });

        } else {
            binding.loginButton.setEnabled(true);
            Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.emailEditText.setError("Bắt buộc");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditText.setError("Email không hợp lệ");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Bắt buộc");
            valid = false;
        }
//        else if (password.length() < 6) {
//            binding.passwordEditText.setError("Mật khẩu quá ngắn");
//            valid = false;
//        }

        return valid;
    }
} 