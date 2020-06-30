package com.example.pharma.model;

public class AddressModel {
    private String address_text;
    private double address_lat;
    private double address_lng;

    public  AddressModel(){

    }
    public AddressModel(String address_text, double address_lat, double address_lng) {
        this.address_text = address_text;
        this.address_lat = address_lat;
        this.address_lng = address_lng;
    }

    public String getAddress_text() {
        return address_text;
    }

    public void setAddress_text(String address_text) {
        this.address_text = address_text;
    }

    public double getAddress_lat() {
        return address_lat;
    }

    public void setAddress_lat(double address_lat) {
        this.address_lat = address_lat;
    }

    public double getAddress_lng() {
        return address_lng;
    }

    public void setAddress_lng(double address_lng) {
        this.address_lng = address_lng;
    }
}
