package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mytripsapplication.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "demo";
    EditText et_username, et_password;
    Button btn_login,bnt_sign_up;
    SignInButton btn_google_SignIn;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListner;
    private static FirebaseFirestore db;
    static String userKey="userEmail";
    static String activityKey = "WhichActivity";
    GoogleSignInClient mGoogleSignInClient;
    private static final int GOOGLE_SIGNIN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        bnt_sign_up = findViewById(R.id.btn_login_signup);
        btn_google_SignIn = findViewById(R.id.btn_google_signIn);

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
                    if(validate()){
                        String email = et_username.getText().toString().trim();
                        String password = et_password.getText().toString().trim();
                        SignInExistingUser(email,password);
                    }

                }
            });

            btn_google_SignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInWithGoogle();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGNIN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                getUserDataByEmail(account.getEmail());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.d("demo", "Google sign in failed", e);
                // ...
            }
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


    private void SignInExistingUser(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG,"email = " + email);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email=user.getEmail();
                        getUserDataByEmail(email);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Wrong email/password", Toast.LENGTH_SHORT).show();
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

    public boolean validate(){

        if((et_username.getText()!= null && et_password.getText()!= null)
                && (!et_username.getText().toString().equals("") && !et_password.getText().toString().equals("")))
            return true;
        else {
            if (et_username.getText() == null || et_username.getText().toString().equals(""))
                Toast.makeText(MainActivity.this, "Please enter Email", Toast.LENGTH_SHORT).show();

            else if (et_password.getText() == null || et_password.getText().toString().equals(""))
                Toast.makeText(MainActivity.this, "Please enter Password", Toast.LENGTH_SHORT).show();

            return false;
        }
    }

    public void getUserDataByEmail(final String email){
        db = FirebaseFirestore.getInstance();
        db.collection("Users").whereEqualTo("email",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    // size>0 indicated user is already present in the database, therefore navigate him to
                    // dashboard
                    if(task.getResult().size()>0){
                        for (QueryDocumentSnapshot document: task.getResult()){
                            if(document.getString("email").equals(email)){
                                User user=new User();
                                user.setFname(document.getString("fname"));
                                user.setLname(document.getString("lname"));
                                user.setEmail(document.getString("email"));
                                user.setProfilePhoto(document.getString("profilePhoto")==null? "":document.getString("profilePhoto"));
                                user.setGender(document.getString("gender")==null? "":document.getString("gender"));
                                Log.d("demo",user.getEmail());
                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Gson gson = new Gson();
                                String json = gson.toJson(user);
                                editor.putString("logged_in_user", json);
                                editor.commit();
                                Intent i= new Intent(MainActivity.this,Trips.class);
                                startActivity(i);
                                finish();
                            }
                        }
                    }
                    else{
                        Intent setUpProfile = new Intent(MainActivity.this,SetUpProfile.class);
                        setUpProfile.putExtra(activityKey,"SetUpNewProfile");
                        setUpProfile.putExtra(userKey,email);
                        startActivity(setUpProfile);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signInWithGoogle(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient= GoogleSignIn.getClient(this, gso );
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGNIN);
    }
}
