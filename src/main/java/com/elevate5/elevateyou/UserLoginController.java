package com.elevate5.elevateyou;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class UserLoginController {

    @FXML
    private Button createAccountButton;

    @FXML
    private Button signInButton;

    @FXML
    private TextField userEmail;

    @FXML
    private PasswordField userPassword;

    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
        try{
            String email = userEmail.getText().trim();
            UserRecord userRecord = UserLogin.firebaseAuth.getUserByEmail(email);
            System.out.println("Successfully authenticated user: " + userRecord.getEmail()
                    + ", ID: "+ userRecord.getUid());
        } catch (FirebaseAuthException e) {
            System.out.println("Invalid email or password");
        } catch (IllegalArgumentException e){
            System.out.println("Invalid email or password" + e.getMessage());
        }
    }


}

