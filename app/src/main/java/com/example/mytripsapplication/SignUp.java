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
    EditText et_fname,et_lname,et_email,et_password,et_confirm_password;
    Button btn_signUp,btn_Cancel;
    Spinner spn_gender;
    ImageView iv_camera,iv_profilePhoto;

    private FirebaseAuth mAuth;
    FirebaseStorage storage;
    StorageReference storageRef;

    Bitmap bitmap;
    Boolean isPhotoTaken = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static int RESULT_LOAD_IMAGE = 2;


    ArrayList<String> genders = new ArrayList<String> (Arrays.asList("Female", "Male", "Decline to Identify"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        FirebaseApp.initializeApp(this);

        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_confirm_password = findViewById(R.id.et_confirm_password);
        btn_signUp = findViewById(R.id.btn_signup);
        btn_Cancel = findViewById(R.id.btn_signup_cancel);
        spn_gender = findViewById(R.id.spn_gender);
        iv_profilePhoto = findViewById(R.id.iv_profilePhoto);



        if (isConnected()) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SignUp.this,android.R.layout.simple_spinner_dropdown_item,genders);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spn_gender.setAdapter(dataAdapter);

            btn_signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(et_fname.getText().toString().trim().equals("")){
                        Toast.makeText(SignUp.this, "First name cannot be blank.", Toast.LENGTH_SHORT).show();
                    }
                    else if(et_lname.getText().toString().trim().equals("")){
                        Toast.makeText(SignUp.this, "Last name cannot be blank.", Toast.LENGTH_SHORT).show();
                    }
                    else if(et_email.getText().toString().trim().equals("")){
                        Toast.makeText(SignUp.this, "Email cannot be blank.", Toast.LENGTH_SHORT).show();
                    }
                    else if(et_password.getText().toString().trim().equals("")){
                        Toast.makeText(SignUp.this, "Password cannot be blank.", Toast.LENGTH_SHORT).show();
                    }
                    else if(et_confirm_password.getText().toString().trim().equals("")){
                        Toast.makeText(SignUp.this, "Confirm Password cannot be blank.", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        if(et_password.getText().toString().trim().compareToIgnoreCase(et_confirm_password.getText().toString().trim()) != 0){
                            Toast.makeText(SignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }else{
                            AddNewUser();
                        }

                    }
                }
            });


            btn_Cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            iv_profilePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyCustomAlertDialog();
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


    private void MyCustomAlertDialog(){
        final Dialog MyDialog = new Dialog(SignUp.this);
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

    private void AddNewUser(){
        final String gender = (String) spn_gender.getSelectedItem();
        final User user = new User(et_fname.getText().toString().trim(),et_lname.getText().toString().trim(),et_email.getText().toString().trim());
        user.setGender(gender);
        if(isPhotoTaken){
            final StorageReference imageStorageRef = storageRef.child("images/" + user.getUserId()+ ".jpg");
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
                        HelperAddNewUSer(mapUser);
                    }
                }
            });



        }else {
            Log.d(TAG,"Profile set to defaylt");
            user.setProfilePhoto("default");
            HashMap mapUser = user.toHashMap();
            HelperAddNewUSer(mapUser);
        }

    }

    private void HelperAddNewUSer(final HashMap mapUser){
        mAuth.createUserWithEmailAndPassword(et_email.getText().toString().trim(), et_password.toString().trim())
                .addOnCompleteListener(SignUp.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("Users")
                                    .add(mapUser)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            Toast.makeText(getApplicationContext(), "User created successfully.",
                                                    Toast.LENGTH_SHORT).show();
                                            FirebaseAuth.getInstance().signOut();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });



                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error in mauth ", e);
                    }
                });
    }
}







