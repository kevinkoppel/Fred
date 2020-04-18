package com.example.myfirstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartFragment extends Fragment   {

    static TextView showReceivedData1;
    Product productFromDatabase;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button pay;
    private TextView total, empty;


    private String resultString = "algus";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.cart_fragment, parent, false);

        total = view.findViewById(R.id.totalPrice);
        recyclerView = view.findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        pay = view.findViewById(R.id.payButton);
        empty = view.findViewById(R.id.emptyList);




        empty.setText(resultString);



        return view;

    }
    public void updateEditText(String resultt) {

        empty.setText(resultt);
    }










}
