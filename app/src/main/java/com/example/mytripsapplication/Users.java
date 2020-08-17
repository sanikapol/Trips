package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mytripsapplication.adapters.UserAdapter;
import com.example.mytripsapplication.model.Trip;
import com.example.mytripsapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Users extends AppCompatActivity implements UserAdapter.iUser{

    private static String TAG = "demo";
    private RecyclerView usersRecycler;
    private RecyclerView.Adapter mUserAdapter = null;
    private ArrayList<User> users;

    FirebaseFirestore db;
    private static String userEmail;
    static String selectedUserEmail = "selectedUserEmail";
    static String tripSelected = "selectedTrip";
    androidx.appcompat.widget.Toolbar toolbar;
    private int SELECTED_TRIP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        usersRecycler = findViewById(R.id.recycler_users);
        usersRecycler.setHasFixedSize(true);
        usersRecycler.setLayoutManager(new LinearLayoutManager(this));
        usersRecycler.setAdapter(mUserAdapter);
        db = FirebaseFirestore.getInstance();

        if(getIntent()!=null && getIntent().getExtras()!=null){
            userEmail = (String) getIntent().getExtras().getString(Trips.loggedInUserEmail);
            Log.d(TAG, "User email in intent  " + userEmail );
            GetOtherUsers();
        }


    }

    private void GetOtherUsers(){
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            users = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User u = document.toObject(User.class);
                                if (!u.getEmail().equals(userEmail)) {
                                    users.add(u);
                                }
                            }
                            mUserAdapter = new UserAdapter(users,Users.this);
                            usersRecycler.setAdapter(mUserAdapter);
                            mUserAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Exception + " + e);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Log.d(TAG,"OnResume user email : " + userEmail);
        GetOtherUsers();
    }

    @Override
    public void getUser(int position) {
        User user = users.get(position);

        Intent getTrips = new Intent(Users.this,TripsToJoin.class);
        getTrips.putExtra(selectedUserEmail,user.getEmail());
        startActivityForResult(getTrips,SELECTED_TRIP);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECTED_TRIP && resultCode == RESULT_OK){
            Trip selectedTrip = (Trip) data.getSerializableExtra(TripsToJoin.selectedTrip);
            Log.d(TAG,"Trip details : " + selectedTrip.toString());
            if(selectedTrip.getUsers().contains(userEmail)){
                Toast.makeText(Users.this, "You have alreay added this trip", Toast.LENGTH_SHORT).show();
                finish();
            }
            else {
                selectedTrip.getUsers().add(userEmail);
                AddUserToTheTrip(selectedTrip);
            }
        }
    }

    private void AddUserToTheTrip(final Trip trip){

        //Log.d(TAG,"AddUserToTrip TripId : " + trip.getTripId() + " UserEamil: " + userEmail);
        DocumentReference tripRef = db.collection("Trips").document(trip.getTripId());
        tripRef
            .update("users", FieldValue.arrayUnion(userEmail))
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                    Intent tripChosen = new Intent();
                    tripChosen.putExtra(tripSelected,trip);
                    setResult(Activity.RESULT_OK,tripChosen);
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
