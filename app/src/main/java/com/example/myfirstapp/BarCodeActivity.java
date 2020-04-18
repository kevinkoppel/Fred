package com.example.myfirstapp;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class BarCodeActivity extends AppCompatActivity implements BarcodeFragment.BarCodeFragmentListener, ProductFragment.ProductFragmentListener {

private ProductFragment productFragment;
private CartFragment cartFragment;
private BarcodeFragment barFragment;
public Bundle args = new Bundle();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
           // Programmatically initialize the scanner view
        setContentView(R.layout.activity_bar_code);  // Set the scanner view as the content view
        cartFragment = new CartFragment();
        barFragment = new BarcodeFragment();
        productFragment = new ProductFragment();






        getSupportFragmentManager().beginTransaction().replace(R.id.barcodeContainer, barFragment)
                .replace(R.id.productContainer, productFragment)
                .replace(R.id.cartContainer, cartFragment)
                .hide(productFragment)
                .commit();



    }



    @Override
    public void onInputSent(String result) {


        ProductFragment prod = (ProductFragment) getSupportFragmentManager().findFragmentById(R.id.productContainer);
        prod.updateEditText(result);
        getSupportFragmentManager().beginTransaction().show(productFragment).hide(cartFragment).commit();


/*        productFragment = (ProductFragment) getSupportFragmentManager().findFragmentById(R.id.cartContainer);
        if(productFragment != null) {
            productFragment.updateEditText(result);
        }else{
            return;
        }*/
      /*  CartFragment frag = (CartFragment) getSupportFragmentManager().findFragmentById(R.id.cartContainer);
        frag.updateEditText(result);*/
    }

    @Override
    public void onProductSent(ProductForCart resultProduct) {
        CartFragment frag = (CartFragment) getSupportFragmentManager().findFragmentById(R.id.cartContainer);
        frag.updateEditText(resultProduct.getProduct());
        getSupportFragmentManager().beginTransaction().show(cartFragment).hide(productFragment).commit();
    }




}