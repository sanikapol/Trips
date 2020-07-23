package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Places extends AppCompatActivity implements PlaceAdapter.iPlace{

    private static String TAG = "demo";
    private RecyclerView placesRecycler;
    private RecyclerView.Adapter mPlacesAdapter = null;
    private ArrayList<Place> placesList;

    FirebaseFirestore db;
    androidx.appcompat.widget.Toolbar toolbar;
    private static Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        placesRecycler = findViewById(R.id.placesRecycler);
        placesRecycler.setHasFixedSize(true);
        placesRecycler.setLayoutManager(new LinearLayoutManager(this));
        placesRecycler.setAdapter(mPlacesAdapter);

        if(isConnected()){
            if(getIntent()!=null && getIntent()!=null){
                trip = (Trip) getIntent().getExtras().getSerializable(Trips.tripToAddPlaces);
                String city = trip.getLocation().replaceAll("[^A-Za-z]","+");
                Log.d(TAG,"City Name : " + city);
                new GetPlaces().execute(city);
            }
        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(Places.this, "Not Connected", Toast.LENGTH_SHORT).show();
        }



    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !((NetworkInfo) networkInfo).isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }

    class GetPlaces extends AsyncTask<String,Void,ArrayList<Place>>{
        @Override
        protected ArrayList<Place> doInBackground(String... strings) {
            HttpURLConnection connection = null;
            String city = strings[0];

            try{
                String placeUrl = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + city + "+tourist+attraction" +
                        "&language=en&key=" + getResources().getString(R.string.api_key);
                Log.d(TAG,"placeUrl " + placeUrl);
                URL urlB = new URL(placeUrl);

                connection = (HttpURLConnection) urlB.openConnection();
                connection.connect();

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray results = root.getJSONArray("results");
                    ArrayList<Place> places = new ArrayList<>();
                    for(int i=0;i<results.length();i++){
                        Place place = new Place();
                        JSONObject placeObject = results.getJSONObject(i);
                        place.setPlaceId(placeObject.getString("id"));
                        place.setPlaceIcon(placeObject.getString("icon"));
                        place.setPlaceName(placeObject.getString("name"));
                        place.setLatitude(placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        place.setLongitude(placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        place.setRating(placeObject.getDouble("rating"));
                        //place.setOpenNow(placeObject.getJSONObject("opening_hours").getBoolean("open_now"));
                        places.add(place);
                    }
                    return places;

                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            super.onPostExecute(places);
            placesList = places;
            Log.d(TAG,"Places size: " + places.size());
            mPlacesAdapter = new PlaceAdapter(places,Places.this);
            placesRecycler.setAdapter(mPlacesAdapter);
            mPlacesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void AddPlaceToTrip(int position) {
        Place place = placesList.get(position);
        db = FirebaseFirestore.getInstance();
        DocumentReference tripRef = db.collection("Trips").document(trip.getTripId());
        tripRef
            .update("places", FieldValue.arrayUnion(place))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Place added successfully!");
                    Toast.makeText(Places.this, "Place added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });
    }
}
