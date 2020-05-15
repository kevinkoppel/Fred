package com.example.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.SetupIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmSetupIntentParams;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.SetupIntent;
import com.stripe.android.view.CardInputWidget;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class AddCardActivity extends AppCompatActivity {
    private static final String backendUrl = "https://saving-cards-without-payment.herokuapp.com/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String setupIntentClientSecret;
    private Stripe stripe;
    private com.example.myfirstapp.PaymentMethod paymentMethod;
    private FirebaseFirestore fStore;
    private FirebaseAuth mAuth;
    private String UserId, userEmail, customerId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        Button backButton = findViewById(R.id.BackButton5);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(startIntent);
            }
        });

        loadPage();
    }

    private void loadPage() {
        userEmail = mAuth.getCurrentUser().getEmail();
        UserId = mAuth.getCurrentUser().getUid();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        String json = "{"
                + "\"email\":" + "\"" + userEmail + "\""
                + "}";
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(backendUrl + "create-setup-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        runOnUiThread(() -> {
                            Context applicationContext = getApplicationContext();
                            Toast.makeText(applicationContext, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
                        });
                        Log.e("add card error", e.toString());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                Context applicationContext = getApplicationContext();
                                Toast.makeText(applicationContext, "Error: " + response.toString(), Toast.LENGTH_LONG).show();
                            });
                            Log.e("add card error2", response.toString());

                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Map<String, String>>(){}.getType();
                            Map<String, String> responseMap = gson.fromJson(response.body().string(), type);

                            // The response from the server includes the Stripe publishable key and
                            // SetupIntent details.
                            setupIntentClientSecret = responseMap.get("clientSecret");
                            customerId = responseMap.get("customerId");


                            // Use the key from the server to initialize the Stripe instance.
                            stripe = new Stripe(getApplicationContext(), responseMap.get("publishableKey"));

                        }

                    }


                });
        UserId = mAuth.getCurrentUser().getUid();
        Log.e("document", UserId );
        DocumentReference docRef = fStore.collection("users").document(UserId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Log.e("document", "DocumentSnapshot data: " + document.getData());
                        userEmail = document.getString("Email");
                    }else{
                        Log.e("document", "No such document");
                    }
                }else {
                    Log.e("document", "get failed with", task.getException());
                }
            }
        });

        // Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            // Collect card details
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams.Card card = cardInputWidget.getPaymentMethodCard();


            // Later, you will need to attach the PaymentMethod to the Customer it belongs to.
            // This example collects the customer's email to know which customer the PaymentMethod belongs to, but your app might use an account id, session cookie, etc.

            PaymentMethod.BillingDetails billingDetails = (new PaymentMethod.BillingDetails.Builder())
                    .setEmail(userEmail)
                    .build();
            if (card != null) {
                // Create SetupIntent confirm parameters with the above
                PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams
                        .create(card, billingDetails);
                ConfirmSetupIntentParams confirmParams = ConfirmSetupIntentParams
                        .create(paymentMethodParams, setupIntentClientSecret);
                stripe.confirmSetupIntent(this, confirmParams);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        WeakReference<Activity> weakActivity = new WeakReference<>(this);

        // Handle the result of stripe.confirmSetupIntent
        stripe.onSetupResult(requestCode, data, new ApiResultCallback<SetupIntentResult>() {
            @Override
            public void onSuccess(@NonNull SetupIntentResult result) {
                SetupIntent setupIntent = result.getIntent();
                SetupIntent.Status status = setupIntent.getStatus();
                String resulrt = result.toString();
                if (status == SetupIntent.Status.Succeeded) {
                    // Setup completed successfully
                    runOnUiThread(() -> {
                        if (weakActivity.get() != null) {
                            Activity activity = weakActivity.get();
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Setup completed");
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();

                           // gson.toJson(setupIntent);
                            String json = gson.toJson(setupIntent);
                            deSerializeProduct(json);
                            addPaymentMethodToUsed(paymentMethod);


                            Intent startIntent = new Intent(getApplicationContext(),ProfileActivity.class);
                            startActivity(startIntent);
                        }
                    });

                } else if (status == SetupIntent.Status.RequiresPaymentMethod) {
                    // Setup failed – allow retrying using a different payment method
                    runOnUiThread(() -> {
                        Activity activity = weakActivity.get();
                        if (activity != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle("Setup failed");
                            builder.setMessage(setupIntent.getLastSetupError().getMessage());
                            builder.setPositiveButton("Ok", (DialogInterface dialog, int index) -> {
                                CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
                                cardInputWidget.clear();
                             //   EditText emailInput = findViewById(R.id.emailInput);
                              //  emailInput.setText(null);
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }

            @Override
            public void onError(@NonNull Exception e) {
                // Setup request failed – allow retrying using the same payment method
                runOnUiThread(() -> {
                    Activity activity = weakActivity.get();
                    if (activity != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(e.toString());
                        builder.setPositiveButton("Okei", null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            }
        });

    }

    private void addPaymentMethodToUsed(com.example.myfirstapp.PaymentMethod paymentMethodToAdd) {
        UserId = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("paymentMethodId", paymentMethodToAdd.paymentMethodId);

        fStore.collection("users")
                .document(UserId)
                .update("paymentMethodId", paymentMethodToAdd.getPaymentMethodId())

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("payment", "payment method added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("payment", "error writing document", e);
                    }
                });
        fStore.collection("users")
                .document(UserId)
                .update("customerId", customerId)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("payment", "payment method added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("payment", "error writing document", e);
                    }
                });



    }

    public void deSerializeProduct(String response){

        ObjectMapper mapper = new ObjectMapper();

        try {

            paymentMethod = mapper.readValue(response, com.example.myfirstapp.PaymentMethod.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //   Log.e("asynctask", productFromDatabase.getProduct());





    }


}
