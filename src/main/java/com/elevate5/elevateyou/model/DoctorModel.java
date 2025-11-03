package com.elevate5.elevateyou.model;

public class DoctorModel {

    private String firstName;
    private String lastName;
    private String specialty;
    private String phoneNumber;
    private String address;

    public DoctorModel(String firstName, String lastName, String specialty, String phoneNumber, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.specialty = specialty;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return this.firstName + " " + this.lastName + " " + this.specialty + " " + this.phoneNumber + " " + this.address;
    }

}
