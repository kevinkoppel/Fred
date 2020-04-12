package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        Button secondActivityBtn = findViewById(R.id.secondActivityBtn);
        secondActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),SecondActivity.class);
                startActivity(startIntent);

            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            Toast.makeText(getApplicationContext(), idToken, Toast.LENGTH_SHORT).show();
                            // Send token to your backend via HTTPS
                            // ...
                        } else {
                            Toast.makeText(getApplicationContext(), "You arent registered", Toast.LENGTH_SHORT).show();
                            Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                            startActivity(signUpIntent);

                        }
                    }
                });

       /* if (mAuth.getCurrentUser() == null) {
            Toast.makeText(getApplicationContext(), "You arent registered", Toast.LENGTH_SHORT).show();
            Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(signUpIntent);
        }*/


    }

}
