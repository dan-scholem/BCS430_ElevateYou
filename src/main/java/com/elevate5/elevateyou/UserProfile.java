package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserProfile {

    private String docID;

    private String firstName;

    private String lastName;

    private String userEmail;

    private String userAge;

    private String gender;

    private String userHeight;

    private String userWeight;

    private String userBio;

    /** Getters and setters */

    public String getDocID() {
        return docID;
    }

    public void setDocumentID(String documentID) {
        this.docID = documentID;
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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAge() {
        return userAge;
    }

    public void setUserAge(String userAge) {
        this.userAge = userAge;
    }

    public String getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(String userHeight) {
        this.userHeight = userHeight;
    }

    public String getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(String userWeight) {
        this.userWeight = userWeight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    public UserProfile(String docID, String firstName, String lastName, String userEmail, String userAge, String gender, String userHeight, String userWeight, String userBio) {
        this.docID = docID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userAge = userAge;
        this.gender = gender;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        this.userBio = userBio;
    }

    public static void loadSettingsScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("UserProfile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Profile Settings");
        stage.setScene(scene);
        stage.show();
    }
}
