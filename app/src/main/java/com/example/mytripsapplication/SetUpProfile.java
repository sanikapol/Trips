package com.example.mytripsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mytripsapplication.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SetUpProfile extends AppCompatActivity {

    private static String TAG = "demo";
    Bitmap bitmap;
    Boolean isPhotoTaken = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMAGE = 2;

    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;

    EditText et_fname,et_lname;
    Button btn_save;
    Spinner spn_gender;
    ImageView iv_camera,iv_profilePhoto;

    FirebaseFirestore db;
    User user;
    androidx.appcompat.widget.Toolbar toolbar;
    ActionBar actionBar;


    ArrayList<String> genders = new ArrayList<String> (Arrays.asList("Female", "Male", "Decline to Identify"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);

        toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        FirebaseApp.initializeApp(this);

        db = FirebaseFirestore.getInstance();

        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        spn_gender = findViewById(R.id.spn_gender);
        iv_profilePhoto = findViewById(R.id.iv_profilePhoto);
        btn_save = findViewById(R.id.btn_save);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SetUpProfile.this,android.R.layout.simple_spinner_dropdown_item,genders);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_gender.setAdapter(dataAdapter);

        if(isConnected()){
            user = new User();
            if(getIntent()!=null && getIntent().getExtras()!=null){
                String whichActivity = (String) getIntent().getExtras().getString(MainActivity.activityKey);
                Log.d(TAG,"whichActivity " + whichActivity);
                if(whichActivity.equals("SetUpNewProfile")){
                    Log.d(TAG,"This is mainActivity");
                    user.setEmail((String) getIntent().getExtras().getString(MainActivity.userKey));
                    Log.d(TAG, "Main Activity : " + user.getEmail());
                }
                else if(whichActivity.equals("EditProfile")) {
                    //This is from main activity
                    Log.d(TAG,"This is tripsActivity");
                    actionBar = getSupportActionBar();
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    user  = (User) getIntent().getExtras().getSerializable(Trips.userKey);
                    et_fname.setText(user.getFname());
                    et_lname.setText(user.getLname());
                    if(user.getGender().equals("Female"))
                        spn_gender.setSelection(0);
                    else if(user.getGender().equals("Male"))
                        spn_gender.setSelection(1);
                    else
                        spn_gender.setSelection(2);
                }

            }

            iv_profilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyCustomAlertDialog();
                }
            });

            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(ValidateInputs()){
                        FormatUserData();
                        finish();
                    }
                }
            });

        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(SetUpProfile.this, "Not Connected", Toast.LENGTH_SHORT).show();
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


    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void dispatchBrowseImageIntent(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmap = imageBitmap;
            iv_profilePhoto.setImageBitmap(imageBitmap);
            isPhotoTaken = true;
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            File imgFile = new  File(picturePath);
            iv_profilePhoto.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            Log.d(TAG,"Image path : " + "file://"+picturePath);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
            isPhotoTaken = true;
        }
    }

    private boolean ValidateInputs(){
        if(et_fname.getText().toString().trim() == null || et_fname.getText().toString().trim().equals("")){
                Toast.makeText(SetUpProfile.this, "First name cannot be blank.", Toast.LENGTH_SHORT).show();
                return false;
        }
        else if(et_lname.getText().toString().trim()==null || et_lname.getText().toString().trim().equals("")){
            Toast.makeText(SetUpProfile.this, "Last name cannot be blank.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }

    }

    private void MyCustomAlertDialog(){
        final Dialog MyDialog = new Dialog(SetUpProfile.this);
        MyDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        MyDialog.setContentView(R.layout.edit_profile_photo);

        ImageView iv_camera = (ImageView) MyDialog.findViewById(R.id.iv_camera);
        Button btn_upload = (Button) MyDialog.findViewById(R.id.btn_upload);

        iv_camera.setEnabled(true);
        btn_upload.setEnabled(true);

        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                MyDialog.dismiss();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchBrowseImageIntent();
                MyDialog.dismiss();
            }
        });

        MyDialog.show();
    }

    private void GetMyDetails(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SetUpProfile.this);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("logged_in_user", "");
        user = gson.fromJson(json, User.class);
        Log.d(TAG,"User details:" + user.toString());
    }

    private void SaveUserData(final HashMap mapUser){
        db.collection("Users")
            .whereEqualTo("email", user.getEmail())
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG,"size of trips :" + task.getResult().size());
                        if(task.getResult().isEmpty()){
                            //Adding trip to the database;
                            AddNewUser(mapUser);
                        }
                        else {
                            //Update User profile
                            Log.d(TAG,"User detilas to update:" + user.toString());
                            UpdateUser(mapUser);

                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
    }


    private void FormatUserData(){
        user.setFname(et_fname.getText().toString().trim());
        user.setLname(et_lname.getText().toString().trim());
        user.setGender((String) spn_gender.getSelectedItem());
        if(isPhotoTaken){
            final StorageReference imageStorageRef = storageRef.child("images/" + user.getEmail() + ".jpg");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imageStorageRef.putBytes(data);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageStorageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d(TAG, "URL : " + downloadUri);
                        Picasso.get().load(downloadUri);
                        user.setProfilePhoto(downloadUri.toString());
                        HashMap mapUser = user.toHashMap();
                        SaveUserData(mapUser);
                    }
                }
            });
        }else {
            Log.d(TAG,"Profile photo set to default");
            user.setProfilePhoto("default");
            HashMap mapUser = user.toHashMap();
            SaveUserData(mapUser);
        }


    }

    private void AddNewUser(final HashMap mapUser){
        db.collection("Users")
            .add(mapUser)
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(getApplicationContext(), "User created successfully.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error adding document", e);
                }
            });
    }

    private void UpdateUser(HashMap mapUser) {
        db = FirebaseFirestore.getInstance();
        db.collection("Users").document(user.getEmail())
                .set(mapUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Profile updated successfully!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error in updating profile", e);
                    }
                });
    }


}
