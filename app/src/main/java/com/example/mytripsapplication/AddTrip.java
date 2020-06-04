package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
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
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);

        setTitle("Add Trip");

        et_tripName = findViewById(R.id.et_tripName);
        et_searchCity = findViewById(R.id.et_searchCity);
        btn_search = findViewById(R.id.btn_search);
        btn_addTrip = findViewById(R.id.btn_addTrip);

        cityRecycler = findViewById(R.id.CityRecycler);
        cityRecycler.setHasFixedSize(true);
        cityRecycler.setLayoutManager(new LinearLayoutManager(this));
        cityRecycler.setAdapter(mAdapter);


        if (isConnected()) {
            mAuth = FirebaseAuth.getInstance();
            mAuthStateListner = new FirebaseAuth.AuthStateListener() {
                FirebaseUser user = mAuth.getCurrentUser();
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(user!=null){
                        Log.d(TAG, "Add Trip user email : " + user.getEmail());
                        currentUser = user;
                    }
                }
            };

            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_tripName.getText().toString().trim().equals("") || et_searchCity.getText().toString().trim().equals("")){
                        Toast.makeText(AddTrip.this,"Trip name and city cannot be blank!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        new GetCityList().execute(et_searchCity.getText().toString().trim());
                    }
                }
            });

            btn_addTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_tripName.getText().toString().trim().equals("") || et_searchCity.getText().toString().trim().equals("")){
                        Toast.makeText(AddTrip.this,"Trip name and city cannot be blank!",Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListner);
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
            AddTrip(trip);
        }
    }

    @Override
    public void addCityToTrips(int position) {
        et_searchCity.setText(cityList.get(position));
        selectedCity = cities.get(cityList.get(position));
    }

    private void AddTrip(final Trip trip){
        trip.setTitle(et_tripName.getText().toString().trim());
        trip.setLocation(et_searchCity.getText().toString().trim());
        Log.d(TAG,"trip details: " + trip.toString());


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Trips")
                .whereEqualTo("description", trip.getLocation())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG,"size of trips :" + task.getResult().size());
                            if(task.getResult().isEmpty()){
                                //Adding trip to the database;
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                HashMap<String,Object> tripMap = trip.toHashMap();
                                db.collection("Trips")
                                        .add(tripMap)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                            else {

                                Toast.makeText(AddTrip.this, "Trip already exists", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        finish();
                    }
                });
    }

}
