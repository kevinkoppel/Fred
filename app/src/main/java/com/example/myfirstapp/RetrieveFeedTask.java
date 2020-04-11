package com.example.myfirstapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

    Product productFromDatabase;
    String resultString = null;

    



    public RetrieveFeedTask(String barcodeResult){
        resultString = barcodeResult;
        
    }
    

    protected void onPreExecute() {


    }

    protected String doInBackground(Void... urls) {
        Log.e("ASyncTask", "doinbackground");

        try {
            URL url = new URL("https://api.appery.io/rest/1/apiexpress/api/Rimi_Database/Products_Rimi/Products?apiKey=27ace6b1-be6d-4e94-933d-22a6770c0721&Barcode=87654321");
            StringBuilder sb = new StringBuilder("https://api.appery.io/rest/1/apiexpress/api/Rimi_Database/Products_Rimi/Products?apiKey=27ace6b1-be6d-4e94-933d-22a6770c0721&Barcode=");
            sb.append(resultString);
            String fullUrlString = sb.toString();
            URL fullUrl = new URL(fullUrlString);


            HttpURLConnection urlConnection = (HttpURLConnection) fullUrl.openConnection();
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
        Log.e("ASyncTask", "onPostExecute");


        if(response == null) {
            response = "THERE WAS AN ERROR";
        }

        Bundle bundle = new Bundle();
        CartFragment frag = new CartFragment();
        bundle.putString("resultString", response);
        frag.setArguments(bundle);
        


       /* Fragment currentFragment = cartFrag.getActivity().getSupportFragmentManager().findFragmentById(R.id.cartFragment);
        FragmentTransaction fragmentTransaction = currentFragment.getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();*/
        
       // deSerializeProduct(response);


        Log.e("asynctask", response);




    }

    public void deSerializeProduct(String response){
        response = response.substring(1,response.length() - 3);

        StringBuilder stringBuilder = new StringBuilder(response);

        stringBuilder.append(",\"productId\":\"23323123sdasd\"}"); // for testing
        String responseToDeSerialize = stringBuilder.toString();


        ObjectMapper mapper = new ObjectMapper();

        try {

            productFromDatabase = mapper.readValue(responseToDeSerialize, Product.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
     //   Log.e("asynctask", productFromDatabase.getProduct());



    }




}
