package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;



public class MainActivity extends AppCompatActivity {

    private static String TAG = "demo";
    EditText et_username, et_password;
    Button btn_login,bnt_sign_up;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        bnt_sign_up = findViewById(R.id.btn_login_signup);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListner = new FirebaseAuth.AuthStateListener() {
            FirebaseUser user = mAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(user!=null){
                    Log.d(TAG, "user email : " + user.getEmail());
                    currentUser = user;
                }
            }
        };

        if (isConnected()) {

            bnt_sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signUp = new Intent(MainActivity.this,SignUp.class);
                    startActivity(signUp);
                }
            });

            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(et_username.getText().toString().trim().equals("") || et_password.getText().toString().trim().equals("")){
                        Toast.makeText(MainActivity.this, "Please enter Email and Password", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SignInExistingUser(et_username.getText().toString().trim(), et_password.getText().toString().trim());
                    }
                }
            });

        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthStateListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListner != null) {
            mAuth.removeAuthStateListener(mAuthStateListner);
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


    private void SignInExistingUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Log.d(TAG,"Task : " + task.getResult().toString());
                if(!task.isSuccessful()){
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed. " + e, Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Log.d(TAG, "signInWithEmail:success");
                    currentUser= mAuth.getCurrentUser();
                    Log.d(TAG,"Signed-in user is : " + currentUser.getEmail());
                    Intent trips = new Intent(MainActivity.this,Trips.class);
                    startActivity(trips);
                    MakeEditTextsBlank();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Could not sign in because of : "  + e);
            }
        });

    }

    private void MakeEditTextsBlank(){
        et_username.setText("");
        et_password.setText("");
    }
}
