package com.example.foodorderapp.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.MessageAdapter;
import com.example.foodorderapp.model.Message;
import com.example.foodorderapp.utils.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatDetailActivity extends AppCompatActivity {

    String receiverId, receiverName, receiverRoom;
    String senderId, senderName, senderRoom;

    DatabaseReference dbReferenceSender, dbReferenceReceiver;

    ImageView btnSendChat;
    EditText etMessage;
    RecyclerView chatDetailList;
    MessageAdapter messageAdapter;
    Toolbar toolbar;

    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_detail);

        toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);

        currentUserId = SessionManager.getInstance(this).getUser().getId();

        receiverId = getIntent().getStringExtra("id");
        receiverName = getIntent().getStringExtra("name");

        if(receiverId != null){

        }

        btnSendChat = findViewById(R.id.btnSendChat);
        etMessage = findViewById(R.id.etMessage);
        messageAdapter = new MessageAdapter(this);
        chatDetailList = findViewById(R.id.chatDetailList);

        chatDetailList.setAdapter(messageAdapter);
        chatDetailList.setLayoutManager(new LinearLayoutManager(this));

        dbReferenceSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        dbReferenceReceiver =
                FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);

        dbReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messageList = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messageAdapter.clear();
                for(Message message : messageList){
                    messageAdapter.add(message);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnSendChat.setOnClickListener(v -> {
            String message = etMessage.getText().toString();
            if(message.trim().length() > 0){
                sendMessage(message);
            }
            else{
                Toast.makeText(this, "Message can't be empty!", Toast.LENGTH_LONG).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void sendMessage(String messageInput){
        String messageId = UUID.randomUUID().toString();
        Message message = new Message(messageId, currentUserId, messageInput);

        messageAdapter.add(message);
        dbReferenceSender.child(messageId).setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatDetailActivity.this, "Cannot send message, please try " +
                                "again!", Toast.LENGTH_SHORT).show();
                    }
                })
        ;

        dbReferenceReceiver.child(messageId).setValue(message);
        chatDetailList.scrollToPosition(messageAdapter.getItemCount() - 1);
        etMessage.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.user_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == R.id.)
//        return super.onOptionsItemSelected(item);
//    }
}