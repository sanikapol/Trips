package com.example.mytripsapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TripsToJoin extends AppCompatActivity implements TripsToJoinAdapter.iTripToJoin{

    private androidx.appcompat.widget.Toolbar toolbar;
    private RecyclerView TripsToJoinRecycler;
    private RecyclerView.Adapter mTripsToJoinAdapter = null;
    private ArrayList<Trip> tripsToJoin;
    private static String TAG = "demo";
    private String userEmail = null;
    static String selectedTrip = "selectedTrip";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_to_join);

        toolbar = findViewById(R.id.appbar_trips_to_join);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TripsToJoinRecycler = findViewById(R.id.recyclerTripsToJoin);
        TripsToJoinRecycler.setHasFixedSize(true);
        TripsToJoinRecycler.setLayoutManager(new LinearLayoutManager(this));
        TripsToJoinRecycler.setAdapter(mTripsToJoinAdapter);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            userEmail = getIntent().getExtras().getString(Users.selectedUserEmail);
            Log.d(TAG,"Selected user is : " + userEmail);
            GetUserTripsAndDisplay();
        }

    }

    private void GetUserTripsAndDisplay(){
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        db.collection("Trips")
                .whereArrayContains("users", userEmail)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        tripsToJoin = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            tripsToJoin.add(doc.toObject(Trip.class));
                        }
                        mTripsToJoinAdapter = new TripsToJoinAdapter(tripsToJoin,TripsToJoin.this);
                        TripsToJoinRecycler.setAdapter(mTripsToJoinAdapter);
                        mTripsToJoinAdapter.notifyDataSetChanged();
                    }
                });

    }

    @Override
    public void selectedTrip(int position) {
        Trip trip = tripsToJoin.get(position);
        Intent tripChosen = new Intent();
        tripChosen.putExtra(selectedTrip,trip);
        setResult(Activity.RESULT_OK,tripChosen);
        finish();
    }
}
