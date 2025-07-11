package com.example.foodorderapp.Activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.MessageAdapter;
import com.example.foodorderapp.model.ChatRoom;
import com.example.foodorderapp.model.Message;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.utils.AndroidUtil;
import com.example.foodorderapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class ChatDetailActivity extends AppCompatActivity {

    User user;
    private String chatRoomId;
    private ChatRoom chatroomModel;

    private DatabaseReference dbReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ImageView btnSendChat;
    EditText etMessage;
    RecyclerView chatDetailList;
    MessageAdapter messageAdapter;
    ImageView backBtnChatDetail;
    TextView user_name_chat_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);
        setAdjustScreen();

        user = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(mAuth.getUid(), user.getId());

        btnSendChat = findViewById(R.id.btnSendChat);
        etMessage = findViewById(R.id.etMessage);
        chatDetailList = findViewById(R.id.chatDetailList);
        user_name_chat_detail = findViewById(R.id.user_name_chat_detail);
        backBtnChatDetail = findViewById(R.id.backBtnChatDetail);


        //set text hien thi ten nguoi nhan tin nhan
        user_name_chat_detail.setText(user.getName());


        //quay lai
        backBtnChatDetail.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnSendChat.setOnClickListener(v -> {
            String message = etMessage.getText().toString();
            if (message.isEmpty()) {
                return;
            }
            sendMessageToUser(message);
        });


        //get ra hoac tao moi mot room chat neu chua co
        getOrCreateChatRoomModel();

        //setup hien thi len recycle view data tu firebase
        setUpChatDetailList();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setAdjustScreen() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void setUpChatDetailList() {
        Query query = FirebaseUtil.getChatRoomMessageReference(chatRoomId).orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Message> options =
                new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        messageAdapter = new MessageAdapter(options,getApplicationContext());

        //tin nhan se bi nguoc nen phai reverse lai
        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setReverseLayout(true);
        chatDetailList.setLayoutManager(manager);
        chatDetailList.setAdapter(messageAdapter);

        //listen change va cap nhat vao adapter
        messageAdapter.startListening();

        //tu dong keo xuong khi co tin nhan moi
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                chatDetailList.smoothScrollToPosition(0);
            }
        });
    }

    private void sendMessageToUser(String messageInput) {

        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        chatroomModel.setLastMessageSenderId(mAuth.getUid());
        chatroomModel.setLastMessage(messageInput);
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatroomModel);

        Message message = new Message(mAuth.getUid(), messageInput, Timestamp.now());

        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(message).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    etMessage.setText("");
                }
            }
        });

    }


    private void getOrCreateChatRoomModel() {
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(task ->{
            if(task.isSuccessful()) {
                chatroomModel = task.getResult().toObject(ChatRoom.class);
                if(chatroomModel == null){
                    //first time
                    chatroomModel = new ChatRoom(chatRoomId,
                                                Arrays.asList(mAuth.getUid(),
                                                user.getId()),
                                                Timestamp.now(),
                              "");
                    FirebaseUtil.getChatRoomReference(chatRoomId).set(chatroomModel);
                }
            }
        });
    }

}