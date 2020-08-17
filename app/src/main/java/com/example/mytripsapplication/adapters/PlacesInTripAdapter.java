package com.example.mytripsapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mytripsapplication.R;
import com.example.mytripsapplication.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlacesInTripAdapter extends RecyclerView.Adapter<PlacesInTripAdapter.ViewHolder> {


    ArrayList<Place> places;
    private static iPlaceInTrip placeOps;
    private Context context;

    public PlacesInTripAdapter(ArrayList<Place> places,iPlaceInTrip placeOps) {
        this.places = places;
        this.placeOps = placeOps;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.places_in_trip,parent,false);
        PlacesInTripAdapter.ViewHolder viewHolder = new PlacesInTripAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.tv_placeName.setText(place.getPlaceName());
        Picasso.get().load(place.getPlaceIcon()).into(holder.iv_icon);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_placeName,tv_tripName;
        ImageView iv_icon,iv_delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_placeName = itemView.findViewById(R.id.tv_placeName);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            iv_delete = itemView.findViewById(R.id.iv_delete);

            iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placeOps.deletePlace(getAdapterPosition(),tv_tripName.getText().toString());
                }
            });

        }
    }

    public interface iPlaceInTrip{
        public void deletePlace(int position,String desc);
    }
}
