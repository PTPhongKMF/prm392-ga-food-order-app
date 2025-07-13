package com.example.foodorderapp.Activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.R;

public class ChatListStaffActivity extends AppCompatActivity {

    ChatFragment chatFragment;
    ImageView btn_back_fragment_chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list_staff);

        chatFragment = new ChatFragment();
        btn_back_fragment_chat = findViewById(R.id.btn_back_fragment_chat);
        btn_back_fragment_chat.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
    }
}