package com.example.myfirstapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import java.text.DecimalFormat;
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
    Button plus, minus;
    Product productFromDatabase;
    int quantityBox = 1;
    String nr, pr;
    ProductForCart cartProduct;
    public ProgressBar progressBar;

    ImageButton addToCart;

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
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);


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

    public void updateEditText(String resultt, String storeName){


        RetrieveFeedTask rf = new RetrieveFeedTask(resultt, storeName);
        String setQuantityToOne = "1";
        quantityBox = 1;

        quantity.setText(setQuantityToOne);
        rf.execute();
    }

    public void increaseInteger(){
        DecimalFormat df = new DecimalFormat("#.##");
        if(quantityBox > 0) {
            quantityBox += 1;
            nr = String.valueOf(quantityBox);
            quantity.setText(nr);
            double priceWithQuantity = productFromDatabase.getPrice() * quantityBox;
            pr = String.valueOf(priceWithQuantity);
            price.setText(df.format(priceWithQuantity) + "€");
        }else{
            return;
        }
    }
    public void decreaseInteger(){
        DecimalFormat df = new DecimalFormat("#.##");
        if(quantityBox > 1) {
            quantityBox -= 1;
            nr = String.valueOf(quantityBox);
            quantity.setText(nr);
            double priceWithQuantity = productFromDatabase.getPrice() * quantityBox;
            pr = String.valueOf(priceWithQuantity);
            price.setText(df.format(priceWithQuantity) + "€");
        }else{
            return;
        }

    }
    public class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        ProgressBar pr = getView().findViewById(R.id.progressBar2);
        TextView price = (TextView) getView().findViewById(R.id.hindTextView);
        TextView product = getView().findViewById(R.id.toodeTextView);
        TextView quantity = getView().findViewById(R.id.integer_number);
        Button plus = getView().findViewById(R.id.increase);
        Button minus = getView().findViewById(R.id.decrease);
        ImageButton addToCart = getView().findViewById(R.id.addToCart);




        String store;
        Product productFromDatabase;
        String resultString = null;
        String hiiuStore = "Hiiu Rimi";
        String laagriStore = "Laagri Selver";


        public RetrieveFeedTask(String barcodeResult, String storeName){
            resultString = barcodeResult;
            store = storeName;

        }

        protected void onPreExecute() {
            pr.setVisibility(View.VISIBLE);
            price.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            quantity.setVisibility(View.GONE);
            plus.setVisibility(View.GONE);
            minus.setVisibility(View.GONE);
            addToCart.setVisibility(View.GONE);




        }

        protected String doInBackground(Void... urls) {
            Log.e("ASyncTask", "doinbackground");

            try {
                if(store.equals(hiiuStore)){
                    StringBuilder sb = new StringBuilder("https://api.appery.io/rest/1/apiexpress/api/Rimi_tooted/");
                    sb.append(resultString);
                    sb.append("?apiKey=40e54857-50c4-4bdb-b6d7-0e7d46693022");
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
                if(store.equals(laagriStore)){

                }
                return "no store";




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




            deSerializeProduct(response);



            Log.e("asynctask", response);
            pr.setVisibility(View.GONE);
            price.setVisibility(View.VISIBLE);
            product.setVisibility(View.VISIBLE);
            quantity.setVisibility(View.VISIBLE);
            plus.setVisibility(View.VISIBLE);
            minus.setVisibility(View.VISIBLE);
            addToCart.setVisibility(View.VISIBLE);





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

        if(responseToDeSerialize.contains("Barcode"))
        {
            ObjectMapper mapper = new ObjectMapper();

            try {

                productFromDatabase = mapper.readValue(responseToDeSerialize, Product.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //   Log.e("asynctask", productFromDatabase.getProduct());

            product.setText(productFromDatabase.getProduct());
            pr = String.valueOf(productFromDatabase.getPrice());
            price.setText(pr + "€");
        }else{
            listener.onProductSent(cartProduct);
            Toast.makeText(getContext(), "Antud toodet ei ole andmebaasis", Toast.LENGTH_LONG).show();


           /* product.setText("Toodet ei ole andmebaasis");
            listener.onProductSent(cartProduct);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Toode puudub");
            builder.setMessage("Antud toodet ei ole meie andmebaasis");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    product.setText("");
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();*/


        }








    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof  ProductFragmentListener){
            listener = (ProductFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
                    + "must implement barcodefragment listener");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }




}
