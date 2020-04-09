package com.example.myfirstapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;

public class SecondActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Button backButton = (Button)findViewById(R.id.BackButton);
        TextView laagriButton = findViewById(R.id.laagri);
        TextView mustakaButton = findViewById(R.id.must);
        TextView lasnaButton = findViewById(R.id.lasn);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(startIntent);

            }
        });
        laagriButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),ThirdActivity.class);
                startIntent.putExtra("laagricoop","Alusta ostlemist kaupluses Laagri Coop");
                startActivity(startIntent);

            }
        });
        mustakaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),ThirdActivity.class);
                startIntent.putExtra("mustakacoop","Alusta ostlemist kaupluses Mustamäe Coop");
                startActivity(startIntent);

            }
        });
        lasnaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getApplicationContext(),ThirdActivity.class);
                startIntent.putExtra("lasnacoop","Alusta ostlemist kaupluses Lasnamäe Coop");
                startActivity(startIntent);

            }
        });
    }

}
