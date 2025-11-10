package com.elevate5.elevateyou.model;

public class LocationEntryModel {

    private String postalCode;
    private String city;
    private String state;
    private double latitude;
    private double longitude;

    public LocationEntryModel(String postalCode, String city, String state, double latitude, double longitude) {
        this.postalCode = postalCode;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return city + ", " + state + ", " + postalCode;
    }

}
