package com.example.mytripsapplication;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.example.mytripsapplication.model.Place;
import com.example.mytripsapplication.model.Trip;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocationOfPlaces extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String TAG = "demo";
    LatLngBounds latLngBounds;
    Trip trip;
    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_of_places);

        if(getIntent()!=null && getIntent().getExtras()!=null) {
            trip = (Trip) getIntent().getExtras().getSerializable(Trips.locatePlaces);
            places = trip.getPlaces();
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                if(places.size()!=0) {
                    LatLngBounds.Builder latlngBuilder = getLatLngBoundsfromPlaces(trip.getPlaces());
                    latLngBounds = latlngBuilder.build();
                    for (Place place:places){
                        mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLatitude(),place.getLongitude())).title(place.getPlaceName()));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,100));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
                }
                else{
                    LatLng tripDetails = new LatLng(trip.getLatitude(), trip.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(tripDetails).title(trip.getLocation()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(tripDetails));
                }
            }
        });

        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private LatLngBounds.Builder getLatLngBoundsfromPlaces(ArrayList<Place> places){

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for(Place place:places){
            LatLng latlng = new LatLng(place.getLatitude(),place.getLongitude());
            latLngs.add(latlng);
        }

        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        for(LatLng latLng:latLngs){
            latlngBuilder.include(latLng);
        }

        return latlngBuilder;

    }
}
