package com.example.myfirstapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class SecondActivity extends AppCompatActivity implements LocationListener {
    private FusedLocationProviderClient fusedLocationClient;
    int PERMISSION_ID = 44;
    Double latitude, longitude;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseAuth mAuth;
    public FirebaseFirestore fStore;
    String UserId, paymentMethodId;

    protected LocationManager locationManager;

    private long LOCATION_REFRESHTIME = 1;
    private long LOCATION_REFRESH_DISTANCE = 1;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        ImageButton profileButton = findViewById(R.id.profileButton2);



       // getLocation();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLastLocation();


        recyclerView = findViewById(R.id.storeList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);



        //saab kätte kasutaja paymentId(et valideerida selle olemasolek järgmisse vaatesse minekuks)
        UserId = mAuth.getCurrentUser().getUid();
        Log.e("document", UserId );
        DocumentReference docRef = fStore.collection("users").document(UserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.e("document", "DocumentSnapshot data: " + document.getData());
                        paymentMethodId = document.getString("paymentMethodId");
                    }else{
                        Log.e("document", "No such document");
                    }
                }else {
                    Log.e("document", "get failed with", task.getException());
                }
            }
        });

        //Saab poed andmebaasist kätte
        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Stores");
        FirebaseRecyclerOptions<Store> options =
                new FirebaseRecyclerOptions.Builder<Store>()
                        .setQuery(cartListRef
                                , Store.class).build();

       // Koostab tabeli poodidest
        FirebaseRecyclerAdapter<Store, StoreViewHolder> adapter = new FirebaseRecyclerAdapter<Store, StoreViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StoreViewHolder storeViewHolder, int i, @NonNull final Store store) {
                DecimalFormat df = new DecimalFormat("#.#");
                DecimalFormat df2 = new DecimalFormat("#");
                String distanceString;
                double storeLatitude = store.getLatitude();
                double storeLongitude = store.getLongitude();
                double userLatitude = latitude;
                double userLongitude = longitude;


                // poe distantsi arvutus
               double radius = 6471000.0;
               double phiOne = userLatitude * Math.PI/180;
               double phiTwo = storeLatitude * Math.PI/180;
               double deltaPhi = (storeLatitude - userLatitude) * Math.PI/180;
               double deltaLambda = (storeLongitude - userLongitude) * Math.PI/180;

               double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) + Math.cos(phiOne) * Math.cos(phiTwo) * Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
               double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
               double dist = radius * c;

               if(dist < 1000){
                   distanceString = df2.format(dist) + "m";
               }else{
                   dist = dist /1000;
                   distanceString = df.format(dist) + "km";
               }

               profileButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent startIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                       startActivity(startIntent);
                   }
               });


               storeViewHolder.button.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       String storeName = store.getStoreName();
                       String laagriSelver = "Laagri Selver";
                       String hiiuRimi = "Hiiu Rimi";
                       if(paymentMethodId == ""){

                           AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this, R.style.MyAlertDialogStyle);
                           builder.setTitle("Maksekaart puudub");
                           builder.setMessage("Lisa maksekaart, et jätkata");
                           builder.setPositiveButton("Lisa maksekaart", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   Intent startIntent = new Intent(getApplicationContext(),AddCardActivity.class);
                                   startActivity(startIntent);
                                   dialog.cancel();
                               }
                           });
                           builder.setNegativeButton("Tagasi", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.cancel();
                               }
                           });

                           builder.show();


                       }else{
                           Intent startIntent = new Intent(getApplicationContext(),BarCodeActivity.class);
                           if(storeName.equals(laagriSelver)){
                               startIntent.putExtra("store", "Laagri Selver");
                               startActivity(startIntent);
                           }if (storeName.equals(hiiuRimi)){
                               startIntent.putExtra("store", "Hiiu Rimi");
                               startActivity(startIntent);

                           }

                       }
                   }
               });


                Log.e("store", distanceString);
                Log.e("store", store.getStoreName());

                storeViewHolder.distanceToStore.setText(distanceString);
                storeViewHolder.storeName.setText(store.getStoreName());

            }


            @NonNull
            @Override
            public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_items_layout, parent, false);
                StoreViewHolder holder = new StoreViewHolder(view);
                return holder;
            }

        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    //kontrollib et kasutajal oleks asukoha näitamine lubatud
    private boolean checkPermissions(){
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    // küsib luba kasutaja asukoha näitamiseks
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    Intent startIntent = new Intent(getApplicationContext(),SecondActivity.class);
                    startActivity(startIntent);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(startIntent);
                }
                return;

            // other 'case' lines to check for other

    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    //saab kasutaja asukoha
    @SuppressLint("MissingPermission")
    private void getLastLocation(){

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null) {
                        requestNewLocationData();
                    } else {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                });

            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Log.e("permission", "no permission2");
            }
        } else {
            requestPermissions();
            Log.e("permission", "no permission");
        }
    }
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );


    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

        }
    };



    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
