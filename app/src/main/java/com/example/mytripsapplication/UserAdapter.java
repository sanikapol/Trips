package com.example.mytripsapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    ArrayList<User> users;
    private static UserAdapter.iUser userOps;

    public UserAdapter(ArrayList<User> users,iUser userOps) {
        this.users = users;
        this.userOps = userOps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tv_user_Details.setText(user.getFname() + " " + user.getLname());

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_user_Details;
        ImageView iv_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_user_Details = itemView.findViewById(R.id.tv_user_deatils);
            iv_profile = itemView.findViewById(R.id.iv_profile);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userOps.getUser(getAdapterPosition());
                }
            });
        }
    }

    public interface iUser{
        public void getUser(int position);
    }
}
