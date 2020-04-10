package com.example.myfirstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class CartFragment extends Fragment  {

    static TextView showReceivedData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.cart_fragment, parent, false);
        showReceivedData = (TextView) view.findViewById(R.id.resultCode);
        return view;

    }

}
