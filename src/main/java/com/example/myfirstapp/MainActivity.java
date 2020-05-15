package com.example.myfirstapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;

    String UserId;
    TextView greeting;
    static String[] appPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int PERMISSIONS_REQUEST_CODE = 1240;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        greeting = findViewById(R.id.textView3);



        Button secondActivityBtn = findViewById(R.id.secondActivityBtn);
        ImageButton profileBtn = findViewById(R.id.profileButton);
        secondActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(startIntent);

            }
        });
        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(startIntent);

            }
        });
        if (mAuth.getCurrentUser() != null) {
            UserId = mAuth.getCurrentUser().getUid();
            DocumentReference docRef = fStore.collection("users").document(UserId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document.exists()){
                            Log.e("document", "DocumentSnapshot data: " + document.getData());
                            greeting.setText("Tere " + document.getString("Name") + "!");

                        }else{
                            Log.e("document", "No such document");
                        }
                    }else {
                        Log.e("document", "get failed with", task.getException());
                    }
                }
            });
        }



    }
    @Override
    protected void onStart() {
        super.onStart();


        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            mUser.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                               // String idToken = task.getResult().getToken();
                             /*   UserId = mUser.getUid();
                                DocumentReference docRef = fStore.collection("users").document(UserId);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot document = task.getResult();
                                            if(document.exists()){
                                                Log.e("document", "DocumentSnapshot data: " + document.getData());
                                                greeting.setText(document.getString("Name"));

                                            }else{
                                                Log.e("document", "No such document");
                                            }
                                        }else {
                                            Log.e("document", "get failed with", task.getException());
                                        }
                                    }
                                });*/


                                // Send token to your backend via HTTPS
                                // ...
                               // checkCameraPermissions();
                               // checkLocationPermissions();
                                hasPermissions();
                            } else {
                                Toast.makeText(getApplicationContext(), "You arent registered", Toast.LENGTH_SHORT).show();
                                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                                startActivity(signUpIntent);

                            }
                        }
                    });
        }
        else {
            Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(signUpIntent);
        }





       /* if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "You arent registered", Toast.LENGTH_SHORT).show();
            Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(signUpIntent);
        }*/


    }
    private void requestLocationPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                44
        );

    }
    private boolean checkLocationPermissions(){
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            requestLocationPermissions();
        }

        return false;
    }
    private boolean checkCameraPermissions(){
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            requestCameraPermissions();
        }

        return false;
    }
    private void requestCameraPermissions(){

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.CAMERA},
                1011
        );
    }
    private boolean hasPermissions() {
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(String perm : appPermissions){
            if(ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(perm);
            }
        }

        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return true;
    }




}
