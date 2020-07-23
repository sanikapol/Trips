package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;

public class Trips extends AppCompatActivity implements TripAdapter.iTrip,PlacesInTripAdapter.iPlaceInTrip{

    private static String TAG = "demo";
    private RecyclerView tripRecycler;
    private RecyclerView.Adapter mTripAdapter = null;
    private ArrayList<Trip> trips;
    FirebaseFirestore db;
    androidx.appcompat.widget.Toolbar toolbar;
    User user;
    static String userKey = "UserDetailsToEdit";
    static String loggedInUserEmail = "UserEmail";
    static String tripToAddPlaces = "TripToAddPlaces";
    static String locatePlaces = "LocatePlaces";
    static String tripForChat = "TripForChat";
    private int SELECTED_USER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips);


        toolbar = findViewById(R.id.app_bar);
        tripRecycler = findViewById(R.id.recyclerTrips);
        tripRecycler.setHasFixedSize(true);
        tripRecycler.setLayoutManager(new LinearLayoutManager(this));
        tripRecycler.setAdapter(mTripAdapter);
        // Removes blinks
        ((SimpleItemAnimator) tripRecycler.getItemAnimator()).setSupportsChangeAnimations(false);

        setSupportActionBar(toolbar);

        if(isConnected()){
            GetMyDetails();
            getSupportActionBar().setTitle("Welcome " + user.getFname());
            GetMyTripsAndDisplay();
        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(Trips.this, "Not Connected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        GetMyTripsAndDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.addTrip){
            Intent addTrip = new Intent(Trips.this,AddTrip.class);
            addTrip.putExtra(loggedInUserEmail,user.getEmail());
            startActivity(addTrip);
        }
        else if(itemId == R.id.chat){
            //GetMyTripsAndDisplay();
        }
        else if(itemId == R.id.otherTrips){
            Intent getOtherUsers = new Intent(Trips.this,Users.class);
            getOtherUsers.putExtra(loggedInUserEmail,user.getEmail());
            startActivityForResult(getOtherUsers,SELECTED_USER);
            //GetOtherUsers();
        }
        else if(itemId == R.id.edit_profile){
            Intent editProfile  = new Intent(Trips.this,SetUpProfile.class);
            editProfile.putExtra(MainActivity.activityKey,"EditProfile");
            editProfile.putExtra(userKey,user);
            startActivity(editProfile);
        }
        else if(itemId == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(Trips.this, MainActivity.class);        // Specify any activity here e.g. home or splash or login etc
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("EXIT", true);
            startActivity(i);
            finish();
        }
        return super.onOptionsItemSelected(item);
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

    private void GetMyTripsAndDisplay(){
        db = FirebaseFirestore.getInstance();
        db.collection("Trips")
            .whereArrayContains("users", user.getEmail())
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    trips = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        trips.add(doc.toObject(Trip.class));
                    }
                    mTripAdapter = new TripAdapter(trips,Trips.this);
                    tripRecycler.setAdapter(mTripAdapter);
                    mTripAdapter.notifyDataSetChanged();
                }
            });

    }



    private void GetMyDetails(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Trips.this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("logged_in_user", "");
        user = gson.fromJson(json, User.class);
        Log.d(TAG,"User details:" + user.toString());
    }

    @Override
    public void addPlace(int position) {
        Trip trip = trips.get(position);
        Intent places = new Intent(Trips.this,Places.class);
        places.putExtra(tripToAddPlaces,trip);
        startActivity(places);
    }

    @Override
    public void displayLocations(int position) {

        Intent loc = new Intent(Trips.this,LocationOfPlaces.class);
        loc.putExtra(locatePlaces,trips.get(position));
        startActivity(loc);
    }


    @Override
    public void deletePlace(int position, String desc) {

    }

    @Override
    public void openChatroom(int position) {
        Trip trip = trips.get(position);
        Intent chatroom = new Intent(Trips.this,ChatRoom.class);
        chatroom.putExtra(tripForChat,trip);
        chatroom.putExtra(loggedInUserEmail,user.getEmail());
        startActivity(chatroom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECTED_USER && resultCode == RESULT_OK){
            Trip selectedTrip = (Trip) data.getSerializableExtra(Users.tripSelected);
            trips.add(selectedTrip);
            mTripAdapter.notifyDataSetChanged();
        }
    }
}
