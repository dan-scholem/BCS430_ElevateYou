package com.elevate5.elevateyou.model;

import java.util.ArrayList;

public class User {

    private String userID;
    private String firstName;
    private String lastName;
    private String email;
    //private String password;
    //private String passwordHash;
    private Integer userAge;
    private String userGender;
    private Integer feetHeight;
    private Integer inchesHeight;
    private Integer weight;
    private String userBio;
    private String profileImageURL;
    private ArrayList<String> friendsList;
    private ArrayList<FriendRequestModel> friendRequestsList;
    private ArrayList<String> blockList;

    public User(){
        this.userID = "0";
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.userAge = 0;
        this.userGender = "";
        this.feetHeight = 0;
        this.inchesHeight = 0;
        this.weight = 0;
        this.userBio = "";
        this.profileImageURL = "";
    }

    public User(String firstName, String lastName, String email, String profileImageURL, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        //this.passwordHash = passwordHash;
        this.profileImageURL = profileImageURL;
        this.userAge = 0;
        this.userGender = "";
        this.feetHeight = 0;
        this.inchesHeight = 0;
        this.weight = 0;
        this.userBio = "";
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
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
    /*
    public void setPassword(String password) {
        this.password = password;
    }
     */
    public Integer getUserAge() {
        return userAge;
    }

    public void setUserAge(Integer userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public Integer getHeight() {
        return feetHeight;
    }

    public void setHeight(Integer height) {
        this.feetHeight  = height;
    }

    public Integer getInchesHeight() {
        return inchesHeight;
    }

    public void setInchesHeight(Integer inchesHeight) {
        this.inchesHeight = inchesHeight;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
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

    public ArrayList<String> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

    public Integer getFeetHeight() {
        return feetHeight;
    }

    public void setFeetHeight(Integer feetHeight) {
        this.feetHeight = feetHeight;
    }

    public ArrayList<FriendRequestModel> getFriendRequestsList() {
        return friendRequestsList;
    }

    public void setFriendRequestsList(ArrayList<FriendRequestModel> friendRequestsList) {
        this.friendRequestsList = friendRequestsList;
    }

    public ArrayList<String> getBlockList() {
        return blockList;
    }

    public void setBlockList(ArrayList<String> blockList) {
        this.blockList = blockList;
    }

    @Override
    public String toString() {
        return fullName() + " " + getEmail();
    }
}
