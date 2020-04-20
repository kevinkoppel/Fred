package com.example.myfirstapp;

public class Store {

    public String storeName;
    public Double longitude;
    public Double latitude;


    public Store() {
    }

    public Store(String storeName, Double storeLongitude, Double storeLatitude) {
        this.storeName = storeName;
        this.longitude = storeLongitude;
        this.latitude = storeLatitude;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setStoreLongitude(Double storeLongitude) {
        this.longitude = storeLongitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
