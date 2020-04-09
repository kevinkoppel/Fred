package com.example.myfirstapp;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BarCodeActivity extends AppCompatActivity implements BarcodeFragment.FragmentAListener {
private CartFragment cartFragment;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
           // Programmatically initialize the scanner view
        setContentView(R.layout.activity_bar_code);                // Set the scanner view as the content view

    }

    @Override
    public void onInputASent(String input) {
        CartFragment.updateText(input);
    }

//ToDo: scanner fragmenidina saada
//ToDo: saada triipkoodi põhjal andmebaasist toode

}