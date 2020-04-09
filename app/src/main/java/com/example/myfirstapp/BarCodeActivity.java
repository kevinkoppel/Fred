package com.example.myfirstapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class BarCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    private DatabaseReference dbRef;
    List<Product> productList;
    Product productFromDatabase;
    Integer resultBarCode;
    String resultBarcodeForUrl;



    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        dbRef = FirebaseDatabase.getInstance().getReference("products");
        //writeNewUser("7350015509016", "tups");
       /* Query query = dbRef
                .orderByChild("barcode")
                .equalTo("5900497600507");
        query.addListenerForSingleValueEvent(valueEventListener);*/

        productList = new ArrayList<>();

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                    product = productSnapshot.getValue(Product.class);
                    productList.add(product);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


    }

   /* ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            productList.clear();

            for(DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                product = productSnapshot.getValue(Product.class);
                productList.add(product);

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };*/

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

//ToDo: scanner fragmenidina saada
//ToDo: saada triipkoodi põhjal andmebaasist toode
    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        resultBarcodeForUrl = rawResult.getText();
        resultBarCode = Integer.parseInt(resultBarcodeForUrl);
        new RetrieveFeedTask().execute();
        String productName = productFromDatabase.getProduct();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BarCodeActivity.this);
        final TextView result = new TextView(this);
        result.setText(productName);
        mBuilder.setView(result);
        mBuilder.setPositiveButton("Lisa ostukorvi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                AddingToCartList(productFromDatabase);
            }
        });




      /*  String lala = rawResult.getText();
        final Product resultProduct = getIndexByProperty(lala);
        String productName = resultProduct.getProduct();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BarCodeActivity.this);
        final TextView result = new TextView(this);
        result.setText(productName);
        mBuilder.setView(result);
        mBuilder.setPositiveButton("Lisa ostukorvi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                AddingToCartList(resultProduct);
            }
        });

        mBuilder.setNegativeButton("Tagasi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                onResume();
            }
        });

        mBuilder.show();*/




        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }

    private void AddingToCartList(Product productToAdd) {

        final String productId = productToAdd.getProductId();
        String productName = productToAdd.getProduct();
        String barCode = productToAdd.getBarcode();

        String saveCurrentTime, saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productId", productId);
        cartMap.put("barcode", barCode);
        cartMap.put("productName", productName);


        cartListRef.child("User View").child("Products").child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            cartListRef.child("Admin View").child("Products").child(productId)
                                    .updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(BarCodeActivity.this, "Product added", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(BarCodeActivity.this, SecondActivity.class);
                                                startActivity(intent);

                                            }

                                        }
                                    });
                        }
                    }
                });
    }

    private void writeNewUser(String barcode, String productName, double price) {

        String id =  dbRef.push().getKey();
        Product product = new Product(id, barcode, productName, price);       //lisasin siia price aga mujal ei muutnud seda
        dbRef.child(id).setValue(product);
        Toast.makeText(this, "Product added", Toast.LENGTH_LONG).show();





    }
    private Product getIndexByProperty(String yourString) {


        for (Product p : productList) {
            if (p.getBarcode().equals(yourString)) {


                return p;
            }
        }
        return productList.get(0);
    }
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
        // TextView rView = findViewById(R.id.responseView);

        protected void onPreExecute() {
            // rView.setText("tyhi");

        }


        protected String doInBackground(Void... urls) {

            // Do some validation here


            try {
                URL url = new URL("https://api.appery.io/rest/1/apiexpress/api/Rimi_Database/Products_Rimi/Products?apiKey=27ace6b1-be6d-4e94-933d-22a6770c0721&Barcode=12345678");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            Log.i("INFO", response);
            deSerializeProduct(response);







        }
    }

    //Jsonist productiks tegemine
    public void deSerializeProduct(String response){
        response = response.substring(1,response.length() - 3);

        StringBuilder stringBuilder = new StringBuilder(response);

        stringBuilder.append(",\"productId\":\"23323123sdasd\"}");
        String responseToDeSerialize = stringBuilder.toString();

        ObjectMapper mapper = new ObjectMapper();

        try {

            productFromDatabase = mapper.readValue(responseToDeSerialize, Product.class);
        } catch (IOException e) {
            e.printStackTrace();
        }





       /* GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Product createdProduct = gson.fromJson(responseToDeSerialize, Product.class);*/

        String lala = "123";


    }



}