package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stripe.android.Stripe;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Objects;

public class ConfirmPaymentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String userId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView total;
    private static final String BACKEND_URL = "http://192.168.43.182:4567/";

    private OkHttpClient httpClient = new OkHttpClient();
    private Stripe stripe;
    private Double totalPrice = 0.0;

    private String paymentIntentClientSecret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);


        Button backButton = findViewById(R.id.button3);
        Button payButton = findViewById(R.id.maksa);
        recyclerView = findViewById(R.id.cartList2);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
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
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay();

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

    private void pay() {


        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = "{"
                + "\"currency\":\"usd\","
                + "\"amount\": 5"
                + "}";

        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));


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
