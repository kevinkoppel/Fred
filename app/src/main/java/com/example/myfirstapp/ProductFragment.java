package com.example.myfirstapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

public class ProductFragment extends Fragment {
    public interface ProductFragmentListener {

        void onProductSent(ProductForCart resultProduct);
    }
    private ProductFragmentListener listener;

    TextView price, product, quantity;
    Button plus, minus, addToCart;
    Product productFromDatabase;
    int quantityBox = 1;
    String nr, pr;
    ProductForCart cartProduct;

    private FirebaseAuth mAuth;
    public FirebaseFirestore fStore;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = mAuth.getCurrentUser().getUid();



        View view = inflater.inflate(R.layout.product_fragment, parent, false);
        price = (TextView) view.findViewById(R.id.hindTextView);
        product = view.findViewById(R.id.toodeTextView);
        quantity = view.findViewById(R.id.integer_number);
        plus = view.findViewById(R.id.increase);
        minus = view.findViewById(R.id.decrease);
        addToCart = view.findViewById(R.id.addToCart);


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
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartProduct = new ProductForCart(productFromDatabase.getProductId(), productFromDatabase.getBarcode(), productFromDatabase.getProduct(), productFromDatabase.getPrice(), quantityBox);
                addingToCartList();


                listener.onProductSent(cartProduct);
            }
        });




        return view;

    }

    private void addingToCartList() {

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productId", cartProduct.getProductId());
        cartMap.put("product", cartProduct.getProduct());
        cartMap.put("price", cartProduct.getPrice());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", cartProduct.getQuantity());

        cartListRef.child("User View").child(userId).child("Products").child(cartProduct.getProductId())
                .updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.e("addToCart", "added to cart");
                            Toast.makeText(getContext(), "Toode on lisatud ostukorvi", Toast.LENGTH_LONG);

                        }
                    }
                });

    }

    public void updateEditText(String resultt){


        RetrieveFeedTask rf = new RetrieveFeedTask(resultt);
        rf.execute();
    }

    public void increaseInteger(){
        if(quantityBox > 0) {
            quantityBox += 1;
            nr = String.valueOf(quantityBox);
            quantity.setText(nr);
            double priceWithQuantity = productFromDatabase.getPrice() * quantityBox;
            pr = String.valueOf(priceWithQuantity);
            price.setText(pr);
        }else{
            return;
        }
    }
    public void decreaseInteger(){
        if(quantityBox > 1) {
            quantityBox -= 1;
            nr = String.valueOf(quantityBox);
            quantity.setText(nr);
            double priceWithQuantity = productFromDatabase.getPrice() * quantityBox;
            pr = String.valueOf(priceWithQuantity);
            price.setText(pr);
        }else{
            return;
        }

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

                StringBuilder sb = new StringBuilder("https://api.appery.io/rest/1/apiexpress/api/Rimi_tooted/Tooted/");
                sb.append(resultString);
                sb.append("?apiKey=365e3d7e-6f5b-458d-8be4-0e5e5d1260da");
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
        stringBuilder.insert(0, "{");
        stringBuilder.append("\"");
        UUID uid = UUID.randomUUID();
        String idString = uid.toString();

        stringBuilder.append(",\"productId\":\"" + idString + "\"}");
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
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof  ProductFragmentListener){
            listener = (ProductFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + "mus implement barcodefragment listener");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }




}
