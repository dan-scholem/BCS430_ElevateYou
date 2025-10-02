package com.elevate5.elevateyou;

import com.elevate5.elevateyou.model.LogInModel;
import com.elevate5.elevateyou.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
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
import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserLoginController {

    private final LogInModel logInModel = new LogInModel();

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

    /*
    @FXML
    private void handleSignInButtonAction(ActionEvent event) {
        try{
            String email = userEmail.getText();
            String password = userPassword.getText();
            if(logInModel.passwordAuth(email, password)){
                UserRecord userRecord = UserLogin.firebaseAuth.getUserByEmail(email);
                System.out.println("Successfully authenticated user: " + userRecord.getEmail()
                        + ", ID: "+ userRecord.getUid());
                password = null;
            }else{
                System.out.println("Invalid email or password");
            }

        } catch (FirebaseAuthException e) {
            System.out.println("Invalid email or password");
        } catch (IllegalArgumentException e){
            System.out.println("Invalid email or password" + e.getMessage());
        } catch (NullPointerException e){
            System.out.println("Empty email or password" + e.getMessage());
        }
    }

     */

    @FXML
    protected void onSignInButtonClick() throws IOException, ExecutionException, InterruptedException {

        if (userEmail.getText().isEmpty() || userPassword.getText().isEmpty()) {

            showAlert(Alert.AlertType.ERROR, "Please enter your email and password!");

        }

        else if (signInUser()) {

            showAlert(Alert.AlertType.CONFIRMATION, "Sign in successful!");

        }

    }
    // This method is used to sign the user into the application. It will query Firestore to retrieve a document
    // and compare the user's email that is entered to the email in the collection
    public boolean signInUser() throws ExecutionException, InterruptedException {

        String enteredEmail = userEmail.getText();
        String enteredPassword = userPassword.getText();
        String documentEmail, documentPassword;

        // asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> future = App.fstore.collection("Users").get();

        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();

            if (!documents.isEmpty()) {

                for (QueryDocumentSnapshot document : documents) {

                    documentEmail = String.valueOf(document.getData().get("Email"));
                    documentPassword = String.valueOf(document.getData().get("Password"));

                    // user found
                    if (documentEmail.equals(enteredEmail) && documentPassword.equals(enteredPassword)) {
                        App.theUser = new User();
                        App.theUser.setEmail(enteredEmail);
                        App.theUser.setPassword(enteredPassword);

                        Stage stage = (Stage) signInButton.getScene().getWindow();

                        stage.close();

                        Dashboard.loadDashboardScene(stage);

                        showAlert(Alert.AlertType.CONFIRMATION, "Sign in successful!");

                    }


                }

            }

        } catch (InterruptedException | ExecutionException | IOException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    // showAlert method to display alerts
    private void showAlert(Alert.AlertType alertType, String message) {

        Alert alert = new Alert(alertType);
        alert.setTitle(alert.getTitle());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}

