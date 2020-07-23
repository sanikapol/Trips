package com.example.mytripsapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripsToJoinAdapter extends RecyclerView.Adapter<TripsToJoinAdapter.ViewHolder> {

    ArrayList<Trip> trips;
    private static iTripToJoin tripOps;


    public TripsToJoinAdapter(ArrayList<Trip> trips,iTripToJoin tripOps) {
        this.trips = trips;
        this.tripOps = tripOps;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.trips_to_join,parent,false);
        TripsToJoinAdapter.ViewHolder viewHolder = new TripsToJoinAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.tv_dest.setText(trip.getLocation());
        holder.tv_tripOwner.setText(trip.getCreatorId());
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_tripOwner,tv_dest;
        ImageView iv_join;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_tripOwner = itemView.findViewById(R.id.tv_tripOwner);
            tv_dest = itemView.findViewById(R.id.tv_dest);
            iv_join = itemView.findViewById(R.id.iv_join);

            iv_join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tripOps.selectedTrip(getAdapterPosition());
                }
            });
        }
    }

    public interface iTripToJoin{
        public void selectedTrip(int position);
    }
}
