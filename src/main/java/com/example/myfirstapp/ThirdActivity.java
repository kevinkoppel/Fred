package com.example.myfirstapp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThirdActivity extends AppCompatActivity {

    public static TextView tvresult;
    Product productFromDatabase;
    TextView responseView;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_activity);

        //tvresult = (TextView) findViewById(R.id.tvresult);
        responseView = findViewById(R.id.rView);


        Button backButton = (Button)findViewById(R.id.button2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(startIntent);

            }
        });

        Button advanceButton = (Button)findViewById(R.id.processButton);
        advanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),BarCodeActivity.class);
                startActivity(startIntent);
             //   new RetrieveFeedTask().execute();

            }
        });






        if(getIntent().hasExtra("mustakacoop")){
            Button laagri = findViewById(R.id.processButton);
            laagri.setText("Alusta ostlemist kaupluses Mustamae Coop");
        }
        if(getIntent().hasExtra("lasnacoop")){
            Button laagri = findViewById(R.id.processButton);
            laagri.setText("Alusta ostlemist kaupluses Lasnamae Coop");
        }
        if(getIntent().hasExtra("laagricoop")){
            Button laagri = findViewById(R.id.processButton);
            laagri.setText("Alusta ostlemist kaupluses Laagri Coop");
        }

    }

    //Apist producti saamine
    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
       // TextView rView = findViewById(R.id.responseView);

        protected void onPreExecute() {
           // rView.setText("tyhi");

        }

        protected String doInBackground(Void... urls) {

            // Do some validation here
            Product testProduct = new Product("123","321","lalal", 10.2);

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



            responseView.setText(productFromDatabase.getProduct());



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
