package com.example.myfirstapp;

import android.view.View;

interface ItemClickListener {
    void onRecyclerViewItemClicked(int position);
    void onClick(View view, int position, boolean isLongClick);
}
