package com.example.mytripsapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    ArrayList<Place> places;
    private static iPlace placeOps;

    public PlaceAdapter(ArrayList<Place> places,iPlace placeOps) {
        this.places = places;
        this.placeOps = placeOps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.places,parent,false);
        PlaceAdapter.ViewHolder viewHolder = new PlaceAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.tv_place.setText(place.getPlaceName());
        Picasso.get().load(place.getPlaceIcon()).into(holder.iv_placeIcon);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_place;
        ImageView iv_placeIcon,iv_addPlace;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_place = itemView.findViewById(R.id.tv_place);
            iv_placeIcon = itemView.findViewById(R.id.iv_placeIcon);
            iv_addPlace = itemView.findViewById(R.id.iv_addPlaceToTrip);

            iv_addPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placeOps.AddPlaceToTrip(getAdapterPosition());
                }
            });

        }
    }

    public interface iPlace{
        public void AddPlaceToTrip(int position);
    }

}
