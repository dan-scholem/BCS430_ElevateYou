package com.elevate5.elevateyou.model;

public class User {

    private int userID;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    //private String passwordHash;
    private String profileImageURL;

    public User(){
        this.userID = 0;
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.profileImageURL = "";
    }

    public User(int userID, String firstName, String lastName, String email, String passwordHash, String profileImageURL) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        //this.passwordHash = passwordHash;
        this.profileImageURL = profileImageURL;
    }

    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
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
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    /*
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    */
    public String getProfileImageURL() {
        return profileImageURL;
    }
    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
    public String fullName() {
        if (lastName == null || lastName.isBlank()) return firstName;
        if (firstName == null || firstName.isBlank()) return lastName;
        return firstName + " " + lastName;
    }


}
