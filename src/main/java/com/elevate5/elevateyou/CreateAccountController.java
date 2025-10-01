package com.elevate5.elevateyou;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class CreateAccountController {

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Button createAccountButton;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private Button signInButton;

    @FXML
    private TextField userEmail;

    @FXML
    private PasswordField userPassword;

    public Button getCreateAccountButton() {
        return createAccountButton;
    }

    public void setCreateAccountButton(Button createAccountButton) {
        this.createAccountButton = createAccountButton;
    }

    public PasswordField getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(PasswordField confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public TextField getFirstName() {
        return firstName;
    }

    public void setFirstName(TextField firstName) {
        this.firstName = firstName;
    }

    public TextField getLastName() {
        return lastName;
    }

    public void setLastName(TextField lastName) {
        this.lastName = lastName;
    }

    public Button getSignInButton() {
        return signInButton;
    }

    public void setSignInButton(Button signInButton) {
        this.signInButton = signInButton;
    }

    public TextField getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(TextField userEmail) {
        this.userEmail = userEmail;
    }

    public PasswordField getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(PasswordField userPassword) {
        this.userPassword = userPassword;
    }

    @FXML
    public void onSignInButtonClick(ActionEvent actionEvent) {

            try {
                Stage stage = (Stage) signInButton.getScene().getWindow();

                CreateAccount.loadUserLoginScene(stage);
            }

            catch (IOException e) {

                e.printStackTrace();
            }

        }
    }
