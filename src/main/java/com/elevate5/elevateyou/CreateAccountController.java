package com.elevate5.elevateyou;

import com.elevate5.elevateyou.model.Event;
import com.elevate5.elevateyou.model.EventManager;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.elevate5.elevateyou.App.fauth;
import static javafx.application.Application.launch;


public class CreateAccountController {

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

    private UserRecord user;


    @FXML
    public void onSignInButtonClick(ActionEvent actionEvent) {

        try {
            Stage stage = (Stage) signInButton.getScene().getWindow();

            UserLogin.loadUserLoginScene(stage);

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @FXML
    protected void onCreateAccountButtonClick() throws IOException, FirebaseAuthException, ExecutionException, InterruptedException {

        if (userEmail.getText().isEmpty() || userPassword.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Please fill all the required fields!");

            userEmail.clear();
            userPassword.clear();

        } else {

            if (registerUser()) {

                if (addUser()) {

                    App.theUser.setEmail(userEmail.getText());
                    App.theUser.setPassword(userPassword.getText());
                    App.theUser.setFirstName(firstName.getText());
                    App.theUser.setLastName(lastName.getText());

                    firstName.clear();
                    lastName.clear();
                    userEmail.clear();
                    userPassword.clear();

                    user = App.fauth.getUserByEmail(App.theUser.getEmail());
                    SessionManager.setSession(new Session(user));

                    Stage stage = (Stage) createAccountButton.getScene().getWindow();

                    Dashboard.loadDashboardScene(stage);

                    showAlert(Alert.AlertType.CONFIRMATION, "Registration successful!");

                }

            } else {

                checkUserEmail(userEmail.getText());

            }

        }

    }

// This method is called when registering a new user. This method will add the username and password to a collection in Firestore
public boolean addUser() throws FirebaseAuthException {

    UserRecord user = App.fauth.getUserByEmail(userEmail.getText());
    DocumentReference docRef = App.fstore.collection("Users").document(user.getUid());

    Map<String, Object> data = new HashMap<>();
    data.put("Email", userEmail.getText());
    //data.put("Password", userPassword.getText());
    data.put("FirstName", firstName.getText());
    data.put("LastName", lastName.getText());

    DocumentReference eventDocRef = App.fstore.collection("Events").document(user.getUid());
    EventManager eventData = new EventManager();

    try {
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
        result = eventDocRef.set(eventData);
    }

    catch (Exception ex) {
        System.out.println(ex.getMessage());
        return false;
    }

    return true;

}

// This method is called when adding a new authenticated user to Firebase Authentication
public boolean registerUser() {
    UserRecord.CreateRequest request = new UserRecord.CreateRequest()
            .setEmail(userEmail.getText())
            .setEmailVerified(false)
            .setPassword(userPassword.getText());

    UserRecord userRecord;

    try {

        fauth.createUser(request);

        return true;

    }

    catch (FirebaseAuthException ex) {

        return false;
    }

}

// Checks if the user already exists when trying to register an account
public void checkUserEmail(String email) {
    try {

        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

        if (userRecord.getEmail().equals(userEmail.getText())) {

            showAlert(Alert.AlertType.INFORMATION, "User already exists!");

        }

        else {

            showAlert(Alert.AlertType.ERROR, "Invalid email address!");

        }

    }

    catch (Exception e) {

        throw new RuntimeException(e);
    }
}

private void showAlert (Alert.AlertType alertType, String message) {

    Alert alert = new Alert(alertType);
    alert.setTitle(alert.getTitle());
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.show();
}

}