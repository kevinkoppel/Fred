package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;

public class CartFragment extends Fragment   {

    static TextView showReceivedData1;
    Product productFromDatabase;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button pay;
    private TextView total, empty;

    private FirebaseAuth mAuth;
    public FirebaseFirestore fStore;

    public String userId, totalPriceString;


    private Double oneItemTotalPrice = 0.0;
    public Double totalPrice = 0.0;


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
        empty.setText("Skanneerige toode, et lisada see ostukorvi");


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(getActivity(), ConfirmPaymentActivity.class);
                startIntent.putExtra("total", totalPrice);
                startActivity(startIntent);
            }
        });

         final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<ProductForCart> options =
                new FirebaseRecyclerOptions.Builder<ProductForCart>()
                .setQuery(cartListRef.child("User View")
                .child(userId).child("Products"), ProductForCart.class).build();

        FirebaseRecyclerAdapter<ProductForCart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<ProductForCart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final ProductForCart productForCart) {
                DecimalFormat df = new DecimalFormat("#.##");
                String productName = productForCart.getProduct();
                String productQuantity = productForCart.getQuantity().toString();

                String productPrice = df.format(productForCart.getPrice() * productForCart.getQuantity());
                oneItemTotalPrice = Double.valueOf(productPrice);
                totalPrice = totalPrice + oneItemTotalPrice;

                cartViewHolder.txtProductName.setText(productName);
                cartViewHolder.txtProductQuantity.setText("Kogus: " +productQuantity);
                cartViewHolder.txtProductPrice.setText("Hind: " +productForCart.getPrice().toString() + "€");
                total.setText("Kokku: " + df.format(totalPrice));
                cartViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cartListRef.child("User View").child(userId).child("Products").child(productForCart.getProductId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    DecimalFormat df = new DecimalFormat("#.##");
                                    String productPrice = df.format(productForCart.getPrice() * productForCart.getQuantity());
                                    oneItemTotalPrice = Double.valueOf(productPrice);
                                    totalPrice = totalPrice - oneItemTotalPrice;

                                    total.setText("Kokku: " + df.format(totalPrice));
                                    Toast.makeText(getContext(), "Item removed successfully", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);
                return holder;
            }
        };
        // ToDo: total price arvutusloogika ülevaadata (eraldi meetod äkki teha selleks)

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        cartListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    empty.setVisibility(View.INVISIBLE);
                }else{
                    empty.setText("Skanneerige toode, et lisada see ostukorvi");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });









        //empty.setText(resultString);



        return view;

    }
    public void updateEditText(String resultt) {

       // empty.setText(resultt);
    }










}
