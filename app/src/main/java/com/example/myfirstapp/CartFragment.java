package com.example.myfirstapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class CartFragment extends Fragment   {

    static TextView showReceivedData1;
    Product productFromDatabase;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button pay;
    private TextView total, empty;

    private FirebaseAuth mAuth;
    public FirebaseFirestore fStore;

    String userId;


    private String resultString = "algus";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.cart_fragment, parent, false);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userId = mAuth.getCurrentUser().getUid();

        total = view.findViewById(R.id.totalPrice);
        recyclerView = view.findViewById(R.id.cartList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        pay = view.findViewById(R.id.payButton);
        empty = view.findViewById(R.id.emptyList);

         final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<ProductForCart> options =
                new FirebaseRecyclerOptions.Builder<ProductForCart>()
                .setQuery(cartListRef.child("User View")
                .child(userId).child("Products"), ProductForCart.class).build();

        FirebaseRecyclerAdapter<ProductForCart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<ProductForCart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull ProductForCart productForCart) {
                cartViewHolder.txtProductName.setText(productForCart.getProduct());
                cartViewHolder.txtProductQuantity.setText(productForCart.getQuantity().toString());
                cartViewHolder.txtProductPrice.setText(productForCart.getPrice().toString());

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();





        empty.setText(resultString);



        return view;

    }
    public void updateEditText(String resultt) {

        empty.setText(resultt);
    }










}
