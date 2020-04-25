package com.example.myfirstapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ProductList extends ArrayAdapter<Product> {

    private Activity context;
    private List<Product> productList;

    public ProductList(Activity context, List<Product> productList){
        super(context, R.layout.popup_window, productList);
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listViewItem = inflater.inflate(R.layout.popup_window, null, true);

        TextView textViewName = listViewItem.findViewById(R.id.result);

        Product product = productList.get(position);
        textViewName.setText(product.getProduct());

        return listViewItem;
    }
}
