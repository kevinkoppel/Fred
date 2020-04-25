package com.example.myfirstapp;

public class ProductForCart {
    public String productId;


    public String barcode;


    public String product;


    public Double price;
    public Integer quantity;

    public ProductForCart()
    {}
    public ProductForCart(String productId, String barcode, String product, Double price, Integer quantity)
    {
        this.productId = productId;
        this.barcode = barcode;
        this.product = product;
        this.price = price;
        this.quantity = quantity;

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
    public Integer getQuantity(){
        return quantity;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
