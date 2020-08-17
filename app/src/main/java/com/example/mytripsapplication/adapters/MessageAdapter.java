package com.example.mytripsapplication.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytripsapplication.R;
import com.example.mytripsapplication.model.Message;
import com.example.mytripsapplication.model.User;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    ArrayList<Message> messages;
    User currentUser;

    public MessageAdapter(ArrayList<Message> messages, User currentUser) {
        this.messages = messages;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.message,parent,false);
        MessageAdapter.ViewHolder viewHolder = new MessageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tv_message.setText(message.getContent());
        holder.tv_time.setText(message.getTime().toString());
        if(currentUser.getEmail().equals(message.getSenderId())){
            //Set gravity to right
            holder.tv_message.setGravity(Gravity.RIGHT);
            holder.tv_time.setGravity(Gravity.RIGHT);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(75, 75);
            layoutParams.gravity = Gravity.RIGHT;
        }else{
            //set gravity to left
            holder.tv_message.setGravity(Gravity.LEFT);
            holder.tv_time.setGravity(Gravity.LEFT);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(75, 75);
            layoutParams.gravity = Gravity.LEFT;
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_message,tv_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time = itemView.findViewById(R.id.tv_time);
        }
    }
}
