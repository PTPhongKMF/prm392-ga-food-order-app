package com.example.foodorderapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.Activity.ChatDetailActivity;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.ChatRoom;
import com.example.foodorderapp.model.User;
import com.example.foodorderapp.utils.AndroidUtil;
import com.example.foodorderapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class RecentChatAdapter extends FirestoreRecyclerAdapter<ChatRoom, RecentChatAdapter.ChatRoomViewHolder> {

    Context context;


    public RecentChatAdapter(@NonNull FirestoreRecyclerOptions<ChatRoom> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomViewHolder holder, int position, @NonNull ChatRoom model) {

        FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User otherUser = snapshot.getValue(User.class);
                            Log.i("CHAT_ROOM_STAFF", otherUser.getId());
                            holder.userNameText.setText(otherUser.getName());

                            Toast.makeText(context, otherUser.getName(), Toast.LENGTH_SHORT).show();

                            boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                            if (lastMessageSentByMe) {
                                holder.lastMessageText.setText("You: " + model.getLastMessage());
                            } else {
                                holder.lastMessageText.setText(model.getLastMessage());
                                holder.lastMessageText.setTypeface(null, android.graphics.Typeface.BOLD);
                            }
                            holder.lastMessageTimeText.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));

                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(context, ChatDetailActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, otherUser);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CHAT_ROOM_STAFF", "Error: " + error.getMessage());
                    }
                });

    }

    @NonNull
    @Override
    public ChatRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycle_row, parent, false);
        return new ChatRoomViewHolder(view);
    }

    public class ChatRoomViewHolder extends RecyclerView.ViewHolder {

        TextView userNameText, lastMessageText, lastMessageTimeText;

        public ChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            userNameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTimeText = itemView.findViewById(R.id.last_message_time_text);
        }
    }
}


