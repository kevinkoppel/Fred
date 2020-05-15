package com.example.myfirstapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BarCodeActivity extends AppCompatActivity implements BarcodeFragment.BarCodeFragmentListener, ProductFragment.ProductFragmentListener {

private ProductFragment productFragment;
private CartFragment cartFragment;
private BarcodeFragment barFragment;
public Bundle args = new Bundle();
public String storeName;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
           // Programmatically initialize the scanner view
        setContentView(R.layout.activity_bar_code);  // Set the scanner view as the content view

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b!=null){
            storeName = b.getString("store");

        }

        cartFragment = new CartFragment();
        barFragment = new BarcodeFragment();
        productFragment = new ProductFragment();



        getSupportFragmentManager().beginTransaction().replace(R.id.barcodeContainer, barFragment)
                .replace(R.id.productContainer, productFragment)
                .replace(R.id.cartContainer, cartFragment)
                .hide(productFragment)
                .commit();
        cartFragment.updateEditText(storeName);



    }



    @Override
    public void onInputSent(String result) {

        ProductFragment prod = (ProductFragment) getSupportFragmentManager().findFragmentById(R.id.productContainer);
        prod.updateEditText(result, storeName);
        getSupportFragmentManager().beginTransaction().show(productFragment).hide(cartFragment).commit();

    }

    @Override
    public void onProductSent(ProductForCart resultProduct) {
        CartFragment frag = (CartFragment) getSupportFragmentManager().findFragmentById(R.id.cartContainer);
        frag.updateEditText(storeName);
        getSupportFragmentManager().beginTransaction().show(cartFragment).hide(productFragment).commit();
    }




}