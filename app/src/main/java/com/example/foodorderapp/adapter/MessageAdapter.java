package com.example.foodorderapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private String currentUserId;

    private Context context;
    private List<Message> messageList;

    public MessageAdapter(Context context){
        this.context = context;
        this.messageList = new ArrayList<>();
    }

    public void add(Message message){
        messageList.add(message);
        notifyDataSetChanged();
    }

    public void clear(){
        messageList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(viewType == VIEW_TYPE_SENT){
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_sent,
                            parent, false);
            return new MyViewHolder(view);
        }
        else {
            View view =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_received, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        if(message.getSenderId().equals(currentUserId)){
            holder.tvSentMessage.setText(message.getMessage());
        }
        else{
            holder.tvReceivedMessage.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getSenderId().equals(currentUserId)){
            return VIEW_TYPE_SENT;
        }
        else{
            return VIEW_TYPE_RECEIVED;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvSentMessage, tvReceivedMessage;
        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
        }
    }

}
