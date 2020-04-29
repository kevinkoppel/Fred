package com.example.myfirstapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class PaymentCompletedActivity extends AppCompatActivity {

    private String qrCodeString;
    private ImageView imageView;
    private Bitmap bmp;
    private QRGEncoder qrgEncoder;
    private String userId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_completed);

        Button backButton = findViewById(R.id.button4);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        UUID qrCode = UUID.randomUUID();
        qrCodeString = "lalalalalla";
        imageView = findViewById(R.id.qrCode);
        userId = mAuth.getCurrentUser().getUid();



        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        qrgEncoder = new QRGEncoder(
                qrCodeString, null,
                QRGContents.Type.TEXT,
                smallerDimension);

        try {
            bmp = qrgEncoder.encodeAsBitmap();
            imageView.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(userId);
                cartListRef.removeValue();

                Intent startIntent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(startIntent);
            }
        });


    }
}
