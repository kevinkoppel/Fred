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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class ConfirmPaymentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    String userId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);


        Button backButton = findViewById(R.id.button3);
        recyclerView = findViewById(R.id.cartList2);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getCurrentUser().getUid();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("User View").child(userId);
                cartListRef.removeValue();



                Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
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
              /*  oneItemTotalPrice = Double.valueOf(productPrice);
                totalPrice = totalPrice + oneItemTotalPrice;*/

                cartViewHolder.txtProductName.setText(productName);
                cartViewHolder.txtProductQuantity.setText("Kogus: " +productQuantity);
                cartViewHolder.txtProductPrice.setText("Hind: " +productForCart.getPrice().toString() + "€");
             //   total.setText("Kokku: " + totalPrice.toString());
                cartViewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cartListRef.child("User View").child(userId).child("Products").child(productForCart.getProductId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                  /*  DecimalFormat df = new DecimalFormat("#.##");
                                    totalPrice = totalPrice - oneItemTotalPrice;
                                    df.format(totalPrice);
                                    total.setText("Kokku: " + totalPrice.toString());*/
                                    Toast.makeText(getApplicationContext(), "Item removed successfully", Toast.LENGTH_LONG).show();
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
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

}
