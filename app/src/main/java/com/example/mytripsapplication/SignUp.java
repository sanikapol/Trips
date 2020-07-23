package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SignUp extends AppCompatActivity {


    private static String TAG = "demo";
    EditText et_email,et_password,et_confirm_password;
    Button btn_signUp,btn_Cancel;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");


        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_signUp = findViewById(R.id.btn_signup);
        btn_Cancel = findViewById(R.id.btn_signup_cancel);

        mAuth=FirebaseAuth.getInstance();


        if (isConnected()) {


            btn_signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(validateInputs()){
                        String email = et_email.getText().toString().trim();
                        String password = et_password.getText().toString().trim();
                        SignUpNewUser(email,password);
                    }
                }


            });


            btn_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    finish();
                }
            });


        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(SignUp.this, "Not Connected", Toast.LENGTH_SHORT).show();
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

    public boolean validateInputs(){
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String confirm_password = et_confirm_password.getText().toString().trim();
        if(email!=null && password!=null && confirm_password!=null && !email.equals("") && !password.equals("")
                && !confirm_password.equals("") && password.equals(confirm_password))
            if(password.length()>=6)
                return true;
            else return false;
        else{
            if(email==null || email.equals(""))
                Toast.makeText(getApplicationContext(), "Email is required", Toast.LENGTH_SHORT).show();

            if(password==null || password.equals(""))
                Toast.makeText(getApplicationContext(), "Password is required", Toast.LENGTH_SHORT).show();
            if(confirm_password==null || confirm_password.equals(""))
                Toast.makeText(getApplicationContext(), "Confirm Password is required", Toast.LENGTH_SHORT).show();

            if(!password.equals("") && !confirm_password.equals(""))
            {
                if(password.length()<6)
                    Toast.makeText(getApplicationContext(), "Password must at least be 6 characters", Toast.LENGTH_SHORT).show();

                if(!password.equals(confirm_password))
                {
                    Toast.makeText(getApplicationContext(), "Confirm password does not match", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        }
    }


    private void SignUpNewUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUp.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(SignUp.this, SetUpProfile.class);
                            intent.putExtra(MainActivity.userKey,user.getEmail());
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(SignUp.this, "Sign Up failed.",Toast.LENGTH_SHORT).show();
                            Log.d("demo", "createUserWithEmail:failure", task.getException());
                        }
                    }
                });
    }

}







