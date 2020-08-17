package com.example.mytripsapplication.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mytripsapplication.MainActivity;
import com.example.mytripsapplication.SetUpProfile;
import com.example.mytripsapplication.Trips;
import com.example.mytripsapplication.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

public class FireStore {
    private static final FireStore ourInstance = new FireStore();

    static FireStore getInstance() {
        return ourInstance;
    }


    public FireStore() {
    }

    private static String TAG = "demo";
    private static FirebaseFirestore db;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthStateListner;
    private static User currentUser;
    private static String userEmail;

    private static void getUserEmail(){
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            FirebaseUser user = mAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user!=null){
                    Log.d(TAG, "user email : " + user.getEmail());
                    userEmail = user.getEmail();
                }
            }
        };
    }

    private static void getCurrentUser(){
        ourInstance.db.collection("Users").whereEqualTo("email", userEmail).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            // size>0 indicated user is already present in the database
                            if(task.getResult().size()>0){
                                for (QueryDocumentSnapshot document: task.getResult()){
                                    if(document.getString("email").equals(userEmail)){
                                        currentUser = document.toObject(User.class);
                                    }
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public static User getCurrentUserDetails(){
        getUserEmail();
        getCurrentUser();
        return currentUser;
    }
}
