package com.elevate5.elevateyou.model;

public class LocationEntryModel {

    private String postalCode;
    private String city;
    private String state;

    public LocationEntryModel(String postalCode, String city, String state) {
        this.postalCode = postalCode;
        this.city = city;
        this.state = state;
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

    @Override
    public String toString() {
        return city + ", " + state + ", " + postalCode;
    }

}
