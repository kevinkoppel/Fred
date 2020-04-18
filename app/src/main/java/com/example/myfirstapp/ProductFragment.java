package com.example.myfirstapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductFragment extends Fragment {

    TextView price, product, quantity;
    Button plus, minus;
    Product productFromDatabase;
    int quantityBox = 1;
    String nr, pr;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.product_fragment, parent, false);
        price = (TextView) view.findViewById(R.id.hindTextView);
        product = view.findViewById(R.id.toodeTextView);
        quantity = view.findViewById(R.id.integer_number);
        plus = view.findViewById(R.id.increase);
        minus = view.findViewById(R.id.decrease);

        nr = String.valueOf(quantityBox);

        quantity.setText(nr);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseInteger();
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseInteger();
            }
        });




        return view;

    }
    public void updateEditText(String resultt){


        RetrieveFeedTask rf = new RetrieveFeedTask(resultt);
        rf.execute();
    }

    public void increaseInteger(){
        quantityBox += 1;
        nr = String.valueOf(quantityBox);
        quantity.setText(nr);
        double priceWithQuantity = productFromDatabase.getPrice() * quantityBox;
        pr = String.valueOf(priceWithQuantity);
        price.setText(pr);
    }
    public void decreaseInteger(){
        quantityBox -= 1;
        nr= String.valueOf(quantityBox);
        quantity.setText(nr);
        double priceWithQuantity = productFromDatabase.getPrice() * quantityBox;
        pr = String.valueOf(priceWithQuantity);
        price.setText(pr);

    }
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







       /* Fragment currentFragment = cartFrag.getActivity().getSupportFragmentManager().findFragmentById(R.id.cartFragment);
        FragmentTransaction fragmentTransaction = currentFragment.getFragmentManager().beginTransaction();
        fragmentTransaction.detach(currentFragment);
        fragmentTransaction.attach(currentFragment);
        fragmentTransaction.commit();*/

            deSerializeProduct(response);
            //  showReceivedData1.setText(response);


            Log.e("asynctask", response);




        }
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
        product.setText(productFromDatabase.getProduct());
        pr = String.valueOf(productFromDatabase.getPrice());
        price.setText(pr);



    }




}
