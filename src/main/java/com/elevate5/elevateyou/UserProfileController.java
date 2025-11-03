package com.elevate5.elevateyou;

import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.CalendarView;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class UserProfileController {

    @FXML
    private TextField ageField;

    @FXML
    private Button appointmentsButton;

    @FXML
    private Button calendarButton;

    @FXML
    private Button chatButton;

    @FXML
    private Button dashButton;

    @FXML
    private TextField emailField;

    @FXML
    private Button exerciseButton;

    @FXML
    private TextField feetField;

    @FXML
    private RadioButton femalegenButton;

    @FXML
    private TextField firstnameField;

    @FXML
    private Button foodButton;

    @FXML
    private Button friendsButton;

    @FXML
    private TextField inchesField;

    @FXML
    private Button journalButton;

    @FXML
    private TextField lastnameField;

    @FXML
    private Button logoutButton;

    @FXML
    private RadioButton malegenButton;

    @FXML
    private Button medButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button reviewsButton;

    @FXML
    private Button sleepButton;

    @FXML
    private Button tutorialsButton;

    @FXML
    private ImageView userbioButton;

    @FXML
    private TextArea userbioTextField;

    @FXML
    private TextField weightField;


    @FXML
    void calendarButtonClick(ActionEvent event) {

        try {
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            CalendarView.loadCalendarScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void dashboardButtonClick(ActionEvent event) {

        try {

            Stage stage = (Stage) dashButton.getScene().getWindow();

            Dashboard.loadDashboardScene(stage);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

    @FXML
    void foodButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) foodButton.getScene().getWindow();
            CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void journalButtonClick(ActionEvent event) {

        try {
            Stage stage = (Stage) journalButton.getScene().getWindow();

            JournalEntry.loadJournalScene(stage);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }


    @FXML
    void medicationButtonClick(ActionEvent event) {

        try {
            Stage stage = (Stage) medButton.getScene().getWindow();

            Medication.loadMedTrackerScene(stage);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void settingsButtonClick() throws IOException {

        try {
            Stage stage = (Stage) profileButton.getScene().getWindow();

            UserProfile.loadSettingsScene(stage);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    // This event is called to log the user out of the application and returns the user to the login screen
    @FXML
    private void logoutUser(ActionEvent event) throws IOException {

        Stage stage;

        Alert logoutalert = new Alert(Alert.AlertType.CONFIRMATION);

        logoutalert.setTitle("Logout");

        logoutalert.setHeaderText("You are about to logout!");
        logoutalert.setContentText("Are you sure you want to logout?");

        if (logoutalert.showAndWait().get() == ButtonType.OK) {

            stage = (Stage) logoutButton.getScene().getWindow();

            System.out.println("User logged out successfully");

            SessionManager.closeSession();

            stage.close();

            UserLogin.loadUserLoginScene(stage);
        }

    }

    @FXML
    protected void updateProfile(ActionEvent event) throws FirebaseAuthException, ExecutionException, InterruptedException {

        String documentID = SessionManager.getSession().getUser().getUid();

        if (documentID == null || documentID.isEmpty()) {
            showAlert(Alert.AlertType.ERROR,"UID not found!");
        }

        String firstName = firstnameField.getText();
        String lastName = lastnameField.getText();
        String age = ageField.getText();
        String feetheight = feetField.getText();
        String inchesheight = inchesField.getText();
        String weight = weightField.getText();
        String userbio = userbioTextField.getText();
        String gender = "";

        if (malegenButton.isSelected()) {
            gender = "Male";
        }

        else if (femalegenButton.isSelected()) {
            gender = "Female";
        }

        Map<String, Object> userdata = new HashMap<>();

        userdata.put("FirstName", firstName);
        userdata.put("LastName", lastName);
        userdata.put("Age", age);
        userdata.put("Gender", gender);
        userdata.put("Weight", weight);
        userdata.put("FeetHeight", feetheight);
        userdata.put("InchesHeight", inchesheight);
        userdata.put("UserBio", userbio);

        assert documentID != null;
        DocumentReference docRef = App.fstore.collection("Users").document(documentID);

        ApiFuture<WriteResult> future = docRef.set(userdata, SetOptions.merge());

        future.get();

        showAlert(Alert.AlertType.CONFIRMATION,"Your profile has been updated successfully!");
    }

    @FXML
    protected void uploadProfilePhoto(ActionEvent event) {

    }

    private void showAlert (Alert.AlertType alertType, String message) {

        Alert alert = new Alert(alertType);
        alert.setTitle(alert.getTitle());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}