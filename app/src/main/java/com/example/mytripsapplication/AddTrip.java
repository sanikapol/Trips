package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AddTrip extends AppCompatActivity implements CityAdapter.iCity{

    EditText et_tripName, et_searchCity;
    Button btn_search, btn_addTrip;
    private static String TAG = "demo";
    private RecyclerView cityRecycler;
    private RecyclerView.Adapter mAdapter = null;
    HashMap<String,String> cities;
    ArrayList<String> cityList;
    String selectedCity;
    FirebaseFirestore db;
    androidx.appcompat.widget.Toolbar toolbar;
    private String loggedInUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        setTitle("Add Trip");

        //et_tripName = findViewById(R.id.et_tripName);
        et_searchCity = findViewById(R.id.et_searchCity);
        btn_search = findViewById(R.id.btn_search);
        btn_addTrip = findViewById(R.id.btn_addTrip);

        cityRecycler = findViewById(R.id.CityRecycler);
        cityRecycler.setHasFixedSize(true);
        cityRecycler.setLayoutManager(new LinearLayoutManager(this));
        cityRecycler.setAdapter(mAdapter);
        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        if (isConnected()) {

            if(getIntent()!=null && getIntent().getExtras()!=null){
                loggedInUserEmail = (String) getIntent().getExtras().getString(Trips.loggedInUserEmail);
                Log.d(TAG, "User email in intent  " + loggedInUserEmail );
            }


            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_searchCity.getText().toString().trim().equals("")){
                        Toast.makeText(AddTrip.this,"Please enter a destination!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        new GetCityList().execute(et_searchCity.getText().toString().trim());
                    }
                }
            });

            btn_addTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_searchCity.getText().toString().trim().equals("")){
                        Toast.makeText(AddTrip.this,"Please enter a destination!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.d(TAG,"selectedCity" + selectedCity);
                        new GetTripDetails().execute(selectedCity);
                    }

                }
            });
        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(AddTrip.this, "Not Connected", Toast.LENGTH_SHORT).show();
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


    class GetCityList extends AsyncTask<String,Void, HashMap<String,String>> {
        @Override
        protected HashMap<String, String> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            HashMap<String,String> cities = new HashMap<>();
            String city = strings[0];
            try {
                String city_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?" +"key="
                        + getResources().getString(R.string.api_key) + "&types=(cities)&"
                        + "input=" + city;
                Log.d(TAG,"city_URL " + city_URL);
                URL urlB = new URL(city_URL);

                connection = (HttpURLConnection) urlB.openConnection();
                connection.connect();

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray predictions = root.getJSONArray("predictions");
                    for(int i=0;i<predictions.length();i++){
                        JSONObject cityObj = predictions.getJSONObject(i);
                        String cityDesc = cityObj.getString("description");
                        String placeId = cityObj.getString("place_id");
                        cities.put(cityDesc,placeId);
                    }
                    return cities;
                }
                else{
                    Log.d(TAG,"Connection failed");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
                Log.d(TAG,"Exception" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
            super.onPostExecute(stringStringHashMap);

            cityList = new ArrayList<>();
            Iterator iterator = stringStringHashMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                cityList.add(entry.getKey().toString());
            }
            cities = stringStringHashMap;
            mAdapter = new CityAdapter(cityList,AddTrip.this);
            cityRecycler.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
    class GetTripDetails extends AsyncTask<String,Void,Trip>{
        @Override
        protected Trip doInBackground(String... strings) {
            HttpURLConnection connection = null;
            String placeId = strings[0];

            try{
                String trip_URL = "https://maps.googleapis.com/maps/api/place/details/json?" +"key="
                        + getResources().getString(R.string.api_key) + "&placeid=" + placeId;
                Log.d(TAG,"trip_URL " + trip_URL);
                URL urlB = new URL(trip_URL);

                connection = (HttpURLConnection) urlB.openConnection();
                connection.connect();

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONObject location = root.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                    Trip trip = new Trip();
                    trip.setLatitude(location.getDouble("lat"));
                    trip.setLongitude(location.getDouble("lng"));
                    trip.setTripId(root.getJSONObject("result").getString("id"));
                    return trip;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Trip trip) {
            super.onPostExecute(trip);
            GetTripDataFromDatabase(trip);
        }
    }

    @Override
    public void addCityToTrips(int position) {
        et_searchCity.setText(cityList.get(position));
        selectedCity = cities.get(cityList.get(position));
    }

    private void GetTripDataFromDatabase(final Trip trip){
        //trip.setTitle(et_tripName.getText().toString().trim());
        trip.setLocation(et_searchCity.getText().toString().trim());
        Log.d(TAG,"trip details: " + trip.toString());

        db = FirebaseFirestore.getInstance();
        db.collection("Trips")
            .whereEqualTo("tripId", trip.getTripId())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        if(task.getResult().isEmpty()){
                            //Adding trip to the database;
                            trip.setCreatorId(loggedInUserEmail);
                            ArrayList<String> users = new ArrayList<>();
                            users.add(loggedInUserEmail);
                            trip.setUsers(users);
                            HashMap<String,Object> tripMap = trip.toHashMap();
                            Log.d(TAG,"Trip to add : " + trip.toString());
                            AddTrip(tripMap,trip.getTripId());
                        }else{
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("location").equals(et_searchCity.getText().toString().trim())){
                                    Trip t = document.toObject(Trip.class);
                                    ArrayList<String> users = t.getUsers();
                                    if(users.contains(loggedInUserEmail)){
                                        Toast.makeText(AddTrip.this, "You have alreay added this trip", Toast.LENGTH_SHORT).show();
                                    }else {
                                        users.add(loggedInUserEmail);
                                        AddUserToTrip(document.getId(),users);
                                    }
                                }
                                else{

                                }
                            }
                        }
                    }else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    finish();
                }
            });
    }

    private void AddTrip(HashMap tripMap, String tripId){
        db.collection("Trips").document(tripId)
                .set(tripMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Trip added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void AddUserToTrip(String docId, ArrayList<String> users){
        db = FirebaseFirestore.getInstance();
        DocumentReference tripRef = db.collection("Trips").document(docId);
        tripRef
            .update("users", users)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddTrip.this, "A trip with this destination already exists. Adding it to your trips", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User added to the trip successfully!");
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
