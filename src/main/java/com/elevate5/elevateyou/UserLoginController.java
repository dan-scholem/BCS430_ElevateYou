package com.elevate5.elevateyou;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

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
    public void onCreateAccountButtonClick(ActionEvent actionEvent) {

        try {
            Stage stage = (Stage) createAccountButton.getScene().getWindow();

            CreateAccount.loadCreateAccountScene(stage);
        }

        catch (IOException e) {

            e.printStackTrace();
        }
    }

}

