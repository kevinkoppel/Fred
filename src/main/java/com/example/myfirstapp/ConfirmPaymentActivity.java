package com.example.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

public class ConfirmPaymentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    String userId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView total;
    private static final String BACKEND_URL = "http://192.168.43.182:4567/";

    private OkHttpClient httpClient = new OkHttpClient();
    private Stripe stripe;
    private Double totalPrice = 0.0;

    private String paymentIntentClientSecret;
    public String paymentMethod;
    public String customerId;

    public double totalPriceDouble;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);
        Intent recieveIntent = this.getIntent();
        totalPriceDouble = recieveIntent.getDoubleExtra("total", 15.0);



        Button backButton = findViewById(R.id.button3);

        recyclerView = findViewById(R.id.cartList2);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(userId);
                cartListRef.removeValue();



                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(startIntent);
            }
        });


        //get paymentMethodId and customerId
        userId = mAuth.getCurrentUser().getUid();
        Log.e("document", userId );
        DocumentReference docRef = fStore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.e("document", "DocumentSnapshot data: " + document.getData());
                        paymentMethod = document.getString("paymentMethodId");
                        customerId = document.getString("customerId");
                        pay(paymentMethod, customerId, totalPriceDouble);
                    }else{
                        Log.e("document", "No such document");
                    }
                }else {
                    Log.e("document", "get failed with", task.getException());
                }
            }
        });


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<ProductForCart> options =
                new FirebaseRecyclerOptions.Builder<ProductForCart>()
                        .setQuery(cartListRef.child("User View")
                                .child(userId).child("Products"), ProductForCart.class).build();

        FirebaseRecyclerAdapter<ProductForCart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<ProductForCart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final ProductForCart productForCart) {
                DecimalFormat df = new DecimalFormat("#.##");
                String productName = productForCart.getProduct();
                String productQuantity = productForCart.getQuantity().toString();

                String productPrice = df.format(productForCart.getPrice() * productForCart.getQuantity());
              /*  oneItemTotalPrice = Double.valueOf(productPrice);
                totalPrice = totalPrice + oneItemTotalPrice;*/

                cartViewHolder.txtProductName.setText(productName);
                cartViewHolder.txtProductQuantity.setText("Kogus: " +productQuantity);
                cartViewHolder.txtProductPrice.setText("Hind: " +productForCart.getPrice().toString() + "€");
             //   total.setText("Kokku: " + totalPrice.toString());
                cartViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cartListRef.child("User View").child(userId).child("Products").child(productForCart.getProductId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                  /*  DecimalFormat df = new DecimalFormat("#.##");
                                    totalPrice = totalPrice - oneItemTotalPrice;
                                    df.format(totalPrice);
                                    total.setText("Kokku: " + totalPrice.toString());*/
                                    Toast.makeText(getApplicationContext(), "Item removed successfully", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }

    private void pay(String paymentMethodString, String customerIdString, double totalPrice) {
        Button payButton = findViewById(R.id.maksa);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = "{"
                + "\"customer\":\"" + customerIdString + "\","
                + "\"paymentMethod\": \"" + paymentMethodString + "\","
                + "\"amount\": " + totalPrice
                + "}";

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();

        httpClient.newCall(request)
                .enqueue(new PayCallback(this));

        // .enqueue(new PayCallback(this));




// Todo: makse postmanist toimib, nyyd tuleb siit ka toimima saada
        payButton.setOnClickListener((View view) -> {
           // PaymentMethodCreateParams params = new PaymentMethodCreateParams.Card.Builder().setNumber("4242424242424242").setCvc()
            final Context context = getApplicationContext();
            stripe = new Stripe(
                    context,
                    PaymentConfiguration.getInstance(context).getPublishableKey()
            );
           // stripe.confirmPayment(this,confirmParams);



        });



    }
    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );

        // The response from the server includes the Stripe publishable key and
        // PaymentIntent details.
        // For added security, our sample app gets the publishable key from the server
        String stripePublishableKey = responseMap.get("publishableKey");
        paymentIntentClientSecret = responseMap.get("clientSecret");

        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull(stripePublishableKey)
        );
    }
    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<ConfirmPaymentActivity> activityRef;

        PayCallback(@NonNull ConfirmPaymentActivity activity) {
            activityRef = new WeakReference<>(activity);
        }



        @Override
        public void onFailure(Request request, IOException e) {
            final ConfirmPaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            activity.runOnUiThread(() ->
                    Toast.makeText(
                            activity, "Error: " + e.toString(), Toast.LENGTH_LONG
                    ).show()
            );
        }

        @Override
        public void onResponse(Response response) throws IOException {
            final ConfirmPaymentActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }

            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(
                                activity, "Error: " + response.toString(), Toast.LENGTH_LONG
                        ).show()
                );
            } else {
                activity.onPaymentSuccess(response);

            }

        }
    }

}
