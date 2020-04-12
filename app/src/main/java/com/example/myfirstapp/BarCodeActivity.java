package com.example.myfirstapp;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BarCodeActivity extends AppCompatActivity implements BarcodeFragment.BarCodeFragmentListener {
private CartFragment cartFragment;
public Bundle args = new Bundle();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
           // Programmatically initialize the scanner view
        setContentView(R.layout.activity_bar_code);  // Set the scanner view as the content view
        CartFragment cartFragment = new CartFragment();
        BarcodeFragment barFragment = new BarcodeFragment();





        getSupportFragmentManager().beginTransaction().replace(R.id.barcodeContainer, barFragment)
                .replace(R.id.cartContainer, cartFragment)
                .commit();



    }

    @Override
    public void onInputSent(String result) {
        CartFragment frag = (CartFragment) getSupportFragmentManager().findFragmentById(R.id.cartContainer);
        frag.updateEditText(result);
    }




    //ToDo: scanner fragmenidina saada
//ToDo: saada triipkoodi põhjal andmebaasist toode

}