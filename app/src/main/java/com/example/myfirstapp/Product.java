package com.example.myfirstapp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Product {
    public String productId;

    @JsonProperty("Barcode")
    public String barcode;

    @JsonProperty("ProductName")
    public String product;

    @JsonProperty("Price")
    public Double price;

    public Product()
    {}
    public Product(String productId, String barcode, String product, Double price)
    {
        this.productId = productId;
        this.barcode = barcode;
        this.product = product;
        this.price = price;

    }

    public String getProductId() {
        return productId;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getProduct() {
        return product;
    }
    public Double getPrice(){
        return price;
    }
}
