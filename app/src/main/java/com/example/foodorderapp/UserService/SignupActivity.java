package com.example.foodorderapp.UserService;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.Activity.MainActivity;
import com.example.foodorderapp.database.DatabaseHelper;
import com.example.foodorderapp.databinding.ActivitySignupBinding;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.model.UserRole;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private DatabaseHelper dbHelper;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private  FirebaseAuth mAuth;
    private static String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        setupClickListeners();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("users");
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
            createFirebaseUser(email, password, newUser);
        } else {
            Toast.makeText(SignupActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
        }
    }

    //them user vao firebase de thuan tien cho chat sau nay
    private void createFirebaseUser(String email, String password, User user) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "CreateUserWithEmail: Success");
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    UserProfileChangeRequest userProfileChangeRequest =
                            new UserProfileChangeRequest.Builder().setDisplayName(user.getName()).build();

                    user.setId(mAuth.getUid());
                    databaseReference.child(mAuth.getUid()).setValue(user);
                    firebaseUser.updateProfile(userProfileChangeRequest);

                    mAuth.signOut();

                    Toast.makeText(SignupActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish();
                }else{
                    Log.w(TAG, "create: failure", task.getException());
                    Toast.makeText(SignupActivity.this, "Create Fail", Toast.LENGTH_LONG).show();
                }
            }
        });
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
        }
        else if (password.length() < 6) {
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