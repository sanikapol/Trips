package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mytripsapplication.adapters.CityAdapter;
import com.example.mytripsapplication.adapters.MessageAdapter;
import com.example.mytripsapplication.model.Message;
import com.example.mytripsapplication.model.User;
import com.example.mytripsapplication.util.FireStore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Chat extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;
    private String tripId,creatorOfTheTrip;
    private ArrayList<String> receivers;
    FirebaseFirestore db;
    private static String TAG = "demo";
    User currentUser;
    private ArrayList<Message> messages = new ArrayList<Message>() {
        public boolean add(Message message) {
            int index = Collections.binarySearch(this, message);
            if (index < 0) index = ~index;
            super.add(index, message);
            return true;
        }
    };
    private RecyclerView chatRecycler;
    private RecyclerView.Adapter mAdapter = null;

    TextView tv_content;
    ImageView iv_sendMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        chatRecycler = findViewById(R.id.chatRecyclerView);
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(mAdapter);

        tv_content = findViewById(R.id.editText_message);
        iv_sendMsg = findViewById(R.id.imageView_send);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            tripId = getIntent().getStringExtra(AppConstants.TRIPID);
            receivers = getIntent().getStringArrayListExtra(AppConstants.USERS_IN_THE_TRIP);
            creatorOfTheTrip = getIntent().getStringExtra(AppConstants.CREATOR_ID);
            GetMyDetails();




            mAdapter = new MessageAdapter(messages,currentUser);
            chatRecycler.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();

            loadMessages();
            iv_sendMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(tv_content.getText().toString().trim()!=null){
                        final Message message = new Message(tv_content.getText().toString().trim());
                        tv_content.setText("");
                        message.setSenderId(currentUser.getEmail());
                        message.setReceiverIds(receivers);
                        message.setSenderName(currentUser.getFname());
                        message.setGroupId(tripId);
                        HashMap<String,Object> messageMap = message.toHashMap();
                        sendMessage(messageMap);
                    }
                }
            });
        }



    }

    private void sendMessage(HashMap messageMap){
        db = FirebaseFirestore.getInstance();
        db.collection("Messages")
                .add(messageMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        DocumentReference messageDocRef = db.collection("Messages").document(documentReference.getId());
                         messageDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        Message message = document.toObject(Message.class);
                                        messages.add(message);
                                        mAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private void loadMessages(){
        db = FirebaseFirestore.getInstance();
        db.collection("Messages")
                .whereEqualTo("groupId", tripId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Message message = document.toObject(Message.class);
                                messages.add(message);
                            }
                            Collections.sort(messages);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void GetMyDetails(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Chat.this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("logged_in_user", "");
        currentUser = gson.fromJson(json, User.class);
        Log.d(TAG,"User details:" + currentUser.toString());
    }
}
