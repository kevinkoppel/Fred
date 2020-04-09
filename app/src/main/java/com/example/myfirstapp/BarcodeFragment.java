package com.example.myfirstapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BarcodeFragment extends Fragment  {
    private CodeScanner mCodeScanner;
    Product productFromDatabase;
    Integer resultBarCode;
    String resultBarcodeForUrl;
    public static String productName;
    private FragmentAListener listener;
    CartFragment cart;


    public interface FragmentAListener {
        void onInputASent(String data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.barcode_fragment, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


                        resultBarcodeForUrl = result.getText();
                       // resultBarCode = Integer.parseInt(resultBarcodeForUrl);

                      //  new BarcodeFragment.RetrieveFeedTask().execute();
                      //  productName = productFromDatabase.getProduct();
                        cart.updateText(resultBarcodeForUrl);

                       // listener.onInputASent(resultBarcodeForUrl);

                    }
                });



            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
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



    }

}

