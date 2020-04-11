package com.example.myfirstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class CartFragment extends Fragment   {

    static TextView showReceivedData1;
    String result = "algus";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null){
            result = bundle.getString("resultstring", "null");
        }


        View view = inflater.inflate(R.layout.cart_fragment, parent, false);
        showReceivedData1 = (TextView) view.findViewById(R.id.resultCode);
        showReceivedData1.setText(result);



        return view;

    }




}
