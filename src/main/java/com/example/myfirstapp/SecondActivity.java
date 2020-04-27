package com.example.myfirstapp;


import android.Manifest;
import android.content.Context;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class SecondActivity extends AppCompatActivity implements LocationListener {
    private FusedLocationProviderClient fusedLocationClient;
    int PERMISSION_ID = 44;
    Double latitude, longitude;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    protected LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        getLocation();


        recyclerView = findViewById(R.id.storeList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Stores");
        FirebaseRecyclerOptions<Store> options =
                new FirebaseRecyclerOptions.Builder<Store>()
                        .setQuery(cartListRef
                                , Store.class).build();


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


               /* double theta = userLongitude - storeLongitude;

                double dist = Math.sin(Math.toRadians(userLatitude)) * Math.sin(Math.toRadians(storeLatitude)) + Math.cos(Math.toRadians(userLatitude)) * Math.cos(Math.toRadians(storeLatitude)) * Math.cos(Math.toRadians(theta));
                dist = Math.acos(dist);
                dist = Math.toDegrees(dist);
                dist = dist * 60 * 1.1515;
                dist = dist * 0.8684;
                distanceString = df.format(dist);*/

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






                storeViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startIntent = new Intent(getApplicationContext(),BarCodeActivity.class);
                        startActivity(startIntent);
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

    public Location getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocationGPS;
        if (locationManager != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocationGPS != null) {
                    latitude= lastKnownLocationGPS.getLatitude();
                    longitude = lastKnownLocationGPS.getLongitude();
                    return lastKnownLocationGPS;
                } else {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                        return loc;

                    }else{
                        Log.e("error", "siin2");
                    }

                }
            }else {
                requestPermissions();
                Log.e("error", "siin");
            }

        } else {
            return null;
        }
        Log.e("error", "siin3");
        return null;
    }


    private boolean checkPermissions(){
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
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
