package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    String UserId;
    private static final String backendUrl = "http://10.0.2.2:4242/";
    private OkHttpClient httpClient = new OkHttpClient();
    private String setupIntentClientSecret;
    private Stripe stripe;


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
        TextView logout = findViewById(R.id.logout);
        Button addCardBtn = findViewById(R.id.addCard);
        Button backButton = findViewById(R.id.BackButton2);

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

               /* String userId = mAuth.getCurrentUser().getUid();
                DocumentReference docRef = fStore.collection("users").document(userId);
                docRef.update("card no", "51231235432")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("card","Card no added");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e){
                                Log.w("card","Error updating document",e);
                            }
                        });*/





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
                        nimi.setText(document.getString("Name"));
                        emailText.setText(document.getString("Email"));
                    }else{
                        Log.e("document", "No such document");
                    }
                }else {
                    Log.e("document", "get failed with", task.getException());
                }
            }
        });



        nimi.setFocusable(false);
        emailText.setFocusable(false);





    }



}
