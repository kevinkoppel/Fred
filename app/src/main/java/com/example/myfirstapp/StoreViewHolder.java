package com.example.myfirstapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView storeName, distanceToStore;

    public StoreViewHolder(@NonNull View itemView) {
        super(itemView);

        storeName = itemView.findViewById(R.id.store_name);
        distanceToStore = itemView.findViewById(R.id.store_distance);

    }

    @Override
    public void onClick(View v) {

    }
}
