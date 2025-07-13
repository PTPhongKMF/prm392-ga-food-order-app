package com.example.foodorderapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.Message;
import com.example.foodorderapp.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MyViewHolder> {
    Context context;
    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options, Context context) {
        super(options);
        this.context = context;

}

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Message model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.rightChatText.setText(model.getMessage());
        } else{
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftChatText.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_recycler_row, parent,
                false);
        return new MyViewHolder(view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftLayout, rightLayout;
        TextView leftChatText, rightChatText;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            leftLayout = itemView.findViewById(R.id.left_chat_layout);
            rightLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatText = itemView.findViewById(R.id.left_chat_text);
            rightChatText = itemView.findViewById(R.id.right_chat_text);
        }
    }

}


