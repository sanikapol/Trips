package com.example.mytripsapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private static Context context;
    ArrayList<Trip> trips;
    private static iTrip tripOps;

    public TripAdapter(ArrayList<Trip> trips,iTrip tripOps) {
        this.trips = trips;
        this.tripOps = tripOps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.trips,parent,false);
        TripAdapter.ViewHolder viewHolder = new TripAdapter.ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Trip trip = trips.get(position);
        holder.tv_tripName.setText(trip.getLocation());
        holder.tv_owner.setText(trip.getCreatorId());
        RecyclerView.Adapter mAdapter = null;
        holder.placesInTripRecycler.setHasFixedSize(true);
        holder.placesInTripRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.placesInTripRecycler.setAdapter(mAdapter);

        mAdapter = new PlacesInTripAdapter(trip.getPlaces(), (PlacesInTripAdapter.iPlaceInTrip) context);
        holder.placesInTripRecycler.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        holder.bind(trip);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = trip.isExpanded();
                trip.setExpanded(!expanded);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_tripName,tv_owner;
        ImageView iv_location,iv_addplace,iv_chat;
        RecyclerView placesInTripRecycler;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_tripName = itemView.findViewById(R.id.tv_destination);
            tv_owner = itemView.findViewById(R.id.tv_owner);
            iv_location = itemView.findViewById(R.id.iv_location);
            iv_addplace = itemView.findViewById(R.id.iv_addPlace);
            placesInTripRecycler = itemView.findViewById(R.id.placesInTripRecycler);
            iv_chat = itemView.findViewById(R.id.iv_chat);

            iv_addplace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tripOps.addPlace(getAdapterPosition());
                }
            });

            iv_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tripOps.displayLocations(getAdapterPosition());
                }
            });

            iv_chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tripOps.openChatroom(getAdapterPosition());
                }
            });


        }

        public void bind(Trip trip) {
            // Get the state
            boolean expanded = trip.isExpanded();
            // Set the visibility based on state
            placesInTripRecycler.setVisibility(expanded ? View.VISIBLE : View.GONE);

        }
    }

    public interface iTrip{
        public void addPlace(int position);
        public void displayLocations(int position);
        public void openChatroom(int position);
    }
}
