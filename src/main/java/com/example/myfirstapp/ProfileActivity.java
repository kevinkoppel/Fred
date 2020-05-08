package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.okhttp.OkHttpClient;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;


public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth mAuth;
    public String UserId;




    private static final String backendUrl = "http://10.0.2.2:4242/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String setupIntentClientSecret, paymentMethod, customerId;
    private Stripe stripe;
    CardView creditCard;
    Button addCardBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_LeeWGteI9v0CIJh8VcC2MHvx00VdhTXv9p"
        );





        final EditText nimi = findViewById(R.id.nimi);
        final EditText emailText = findViewById(R.id.email);
        TextView cardOwnerName = findViewById(R.id.card_owner_name);

        Button logout = findViewById(R.id.logout);
        addCardBtn = findViewById(R.id.addCard);
        Button backButton = findViewById(R.id.BackButton2);
        creditCard = findViewById(R.id.creditCardPreview);

        creditCard.setVisibility(View.GONE);

        UserId = mAuth.getCurrentUser().getUid();
        Log.e("document", UserId );
        DocumentReference docRef = fStore.collection("users").document(UserId);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot == null) {
                            Log.d("document", "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            nimi.setText(documentSnapshot.getString("Name"));
                            cardOwnerName.setText(documentSnapshot.getString("Name"));

                            emailText.setText(documentSnapshot.getString("Email"));
                            paymentMethod = documentSnapshot.getString("paymentMethodId");
                            customerId = documentSnapshot.getString("customerId");
                            Log.d("document", "onSuccess: " + documentSnapshot.getString("Name"));

                        }

                    }
                });
        nimi.setFocusable(false);
        emailText.setFocusable(false);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent startIntent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(startIntent);

            }
        });
        addCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(paymentMethod == "" && customerId == ""){
                    Intent startIntent = new Intent(getApplicationContext(),AddCardActivity.class);
                    startActivity(startIntent);
                }else{



                    Toast.makeText(getApplicationContext(), "You already have added a card to your account", Toast.LENGTH_LONG);
                }


            }
        });
        getCreditCartPreview();

    }
    private void getCreditCartPreview(){
       /* if(payMethod == ""){

        }else{
            addCardBtn.setVisibility(View.GONE);
            creditCard.setVisibility(View.VISIBLE);
        }*/
        UserId = mAuth.getCurrentUser().getUid();
        Log.e("document", UserId );
        DocumentReference docRef = fStore.collection("users").document(UserId);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot == null) {
                            Log.d("document", "onSuccess: LIST EMPTY");
                            return;
                        } else {

                            if(paymentMethod == ""){

                            }else{
                                addCardBtn.setVisibility(View.GONE);
                                creditCard.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                });
    }



}
