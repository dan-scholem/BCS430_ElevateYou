package com.elevate5.elevateyou;

import com.elevate5.elevateyou.model.Event;
import com.elevate5.elevateyou.model.EventManager;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.CalendarView;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.SetOptions;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
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
    private TextField ageField;

    @FXML
    private TextField feetField;

    @FXML
    private TextField inchesField;

    @FXML
    private TextField weightField;

    @FXML
    private TextArea userbioTextField;

    @FXML
    private RadioButton genderButton1, genderButton2;

    @FXML
    private Button calendarButton;

    @FXML
    private Button dashButton;

    @FXML
    private Button foodButton;

    @FXML
    private Button journalButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button medButton;

    @FXML
    private Button profileButton;

    @FXML
    private Label agemessage;

    @FXML
    private Label emailmessage;

    @FXML
    private Label heightmessage;

    @FXML
    private Label weightmessage;

    @FXML
    private Button profileImageButton;

    private String photoFile;


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
                    //App.theUser.setPassword(userPassword.getText());
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
    public boolean addUser() throws FirebaseAuthException, ExecutionException, InterruptedException {

        UserRecord user = App.fauth.getUserByEmail(userEmail.getText());
        DocumentReference docRef = App.fstore.collection("Users").document(user.getUid());

        Map<String, Object> data = new HashMap<>();
        data.put("Email", userEmail.getText());
        data.put("FirstName", firstName.getText());
        data.put("LastName", lastName.getText());
        data.put("Age", "");
        data.put("Gender", "");
        data.put("Weight", "");
        data.put("HeightInFeet", "");
        data.put("HeightInInches", "");
        data.put("UserBio", "");

        DocumentReference eventDocRef = App.fstore.collection("Events").document(user.getUid());
        EventManager eventData = new EventManager();

        try {

            ApiFuture<WriteResult> future = docRef.set(data, SetOptions.merge());

            future.get();

            //asynchronously write data
            ApiFuture<WriteResult> result = docRef.set(data);
            result = eventDocRef.set(eventData);
        } catch (Exception ex) {
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

        } catch (FirebaseAuthException ex) {

            return false;
        }

    }

    @FXML
    protected void updateUser(ActionEvent event) throws ExecutionException, InterruptedException {

        String documentID = SessionManager.getSession().getUser().getUid();

        if (documentID == null || documentID.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "UID not found!");
            return;
        }

        /** Clearing the validation messages **/

        emailmessage.setText("");
        agemessage.setText("");
        heightmessage.setText("");
        weightmessage.setText("");

        boolean validationError = false;

        /** Updating the map in Firestore **/
        Map<String, Object> userinfo = new HashMap<>();

        /** Validate the user's first name **/

        String firstname = firstName.getText().trim();
        if (!firstname.isEmpty()) {

            userinfo.put("FirstName", firstname);
        }

        /** Validate the user's last name **/

        String lastname = lastName.getText().trim();
        if (!lastname.isEmpty()) {

            userinfo.put("LastName", lastname);
        }

        /** Validate user's gender choice **/

        if (genderButton1.isSelected()) {

            userinfo.put("Gender", "Male");
        } else if (genderButton2.isSelected()) {

            userinfo.put("Gender", "Female");
        }

        /** If the user bio field is not empty, the enter bio is added to the map **/

        String bioInfo = userbioTextField.getText().trim();

        if (!bioInfo.isEmpty()) {

            userinfo.put("UserBio", bioInfo);
        }

        /** Validates the user's age **/

        String userAge = ageField.getText().trim();

        if (!userAge.isEmpty()) {

            try {

                int userage = Integer.parseInt(userAge);

                if (userage < 0 || userage > 100) {

                    agemessage.setText("Please enter a valid age!");
                    agemessage.setStyle("-fx-text-fill: red;");
                    validationError = true;

                } else {
                    userinfo.put("Age", String.valueOf(userage));
                }

            } catch (NumberFormatException e) {

                agemessage.setText("Age must be a valid number!");
                agemessage.setStyle("-fx-text-fill: red;");
                validationError = true;
            }

        }

        /** Validate the user's height **/

        String heightfeet = feetField.getText().trim();
        String heightinches = inchesField.getText().trim();

        if (!heightfeet.isEmpty() || !heightinches.isEmpty()) {

            if (heightfeet.isEmpty() || heightinches.isEmpty()) {
                heightmessage.setText("Enter both feet and inches!");
                heightmessage.setStyle("-fx-text-fill: red;");
                validationError = true;
            } else {
                try {

                    int userheight = Integer.parseInt(heightfeet);
                    int inchesheight = Integer.parseInt(heightinches);

                    if (userheight < 4 || userheight > 8 || inchesheight < 0 || inchesheight > 11) {

                        heightmessage.setText("Height in feet, must be between 0-8 and inches must be 0-11!");
                        heightmessage.setStyle("-fx-text-fill: red;");
                        validationError = true;
                    } else {
                        userinfo.put("HeightInFeet", String.valueOf(userheight));
                        userinfo.put("HeightInInches", String.valueOf(inchesheight));
                    }
                } catch (NumberFormatException e) {
                    heightmessage.setText("Height in feet and inches must be a valid numbers!");
                    heightmessage.setStyle("-fx-text-fill: red;");
                    validationError = true;
                }
            }

        }

        /** Validate the user's weight **/

        String weight = weightField.getText().trim();

        if (!weight.isEmpty()) {

            try {

                int userweight = Integer.parseInt(weight);

                if (userweight < 0 || userweight > 250) {

                    weightmessage.setText("Please enter a valid weight amount (0-250)");
                    weightmessage.setStyle("-fx-text-fill: red;");
                    validationError = true;
                } else {
                    userinfo.put("Weight", String.valueOf(userweight));
                }

            } catch (NumberFormatException e) {

                weightmessage.setText("Weight must be a valid number!");
                weightmessage.setStyle("-fx-text-fill: red;");
                validationError = true;
            }
        }

        /** Validates the user's email address **/

        String email = userEmail.getText().trim().toLowerCase();

        if (!email.isEmpty()) {

            if (!isEmailValid(email)) {

                emailmessage.setText("Please enter a valid email address!");
                emailmessage.setStyle("-fx-text-fill: red;");
                validationError = true;
            }
        }

        if (validationError) {
            showAlert(Alert.AlertType.ERROR, "Please fix invalid fields!");
            return;
        }

        if (userinfo.isEmpty() && email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No fields to update!");
            return;
        }

        /** Email Validation in Firebase Auth **/
        try {
            if (!email.isEmpty()) {

                try {
                    // Updates the user's email in Firebase
                    UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(documentID)
                            .setEmail(email);

                    UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);

                    System.out.println("Successfully updated user email: " + email);

                    userinfo.put("Email", email);

                } catch (FirebaseAuthException e) {
                    emailmessage.setText("Please enter a valid email address!");
                    emailmessage.setStyle("-fx-text-fill: red;");

                    System.out.println("Failed to update user email:" + email);

                    return;

                }

            }

            if (!userinfo.isEmpty()) {
                DocumentReference docRef = App.fstore.collection("Users").document(documentID);
                ApiFuture<WriteResult> future = docRef.set(userinfo, SetOptions.merge());
                future.get();
            }

            showAlert(Alert.AlertType.CONFIRMATION, "Profile successfully updated!");


            /** Fields are cleared if update is successful **/

            firstName.clear();
            lastName.clear();
            userEmail.clear();
            ageField.clear();
            feetField.clear();
            inchesField.clear();
            weightField.clear();
            userbioTextField.clear();

            if (genderButton1.isSelected()) {
                genderButton1.setSelected(false);
            }
            if (genderButton2.isSelected()) {
                genderButton2.setSelected(false);
            }

        } catch (InterruptedException | ExecutionException e) {

            showAlert(Alert.AlertType.ERROR, "Failed to update user profile");
        }
    }



        /** This method checks to see if the email the user entered is valid  **/

        private boolean isEmailValid (String email){

            String emailregex = "^[a-z0-9-.]+@[a-z]{1,20}\\.[a-z]{2,}$";

            return email.matches(emailregex);
        }

        // Checks if the user already exists when trying to register an account
        public void checkUserEmail (String email){
            try {

                UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

                if (userRecord.getEmail().equals(userEmail.getText())) {

                    showAlert(Alert.AlertType.INFORMATION, "User already exists!");

                } else {

                    showAlert(Alert.AlertType.ERROR, "Invalid email address!");

                }

            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }

        // This event is called to log the user out of the application and returns the user to the login screen
        @FXML
        private void logoutUser (ActionEvent event) throws IOException {

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
        protected void uploadProfilePhoto (ActionEvent event){

            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png"));

            fileChooser.setTitle("Upload Profile Image");

            File imagefile = fileChooser.showOpenDialog(profileImageButton.getScene().getWindow());

            if (imagefile != null) {

                try {
                    photoFile = imagefile.toURI().toString();

                    ImageView imageView = new ImageView(photoFile);

                    imageView.setPreserveRatio(true);

                    imageView.setFitWidth(50);
                    imageView.setFitHeight(50);

                    profileButton.setPrefSize(50, 50);

                    profileButton.setGraphic(imageView);

                    showAlert(Alert.AlertType.INFORMATION, "Profile photo uploaded successfully.");
                }

                catch (Exception e) {

                    showAlert(Alert.AlertType.ERROR, "Unable to upload profile image." + e.getMessage());
                }



            }


        }

        @FXML
        protected void dashboardButtonClick () {

            try {

                Stage stage = (Stage) dashButton.getScene().getWindow();

                Dashboard.loadDashboardScene(stage);

            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void medicationButtonClick () throws IOException {

            try {
                Stage stage = (Stage) medButton.getScene().getWindow();

                Medication.loadMedTrackerScene(stage);
            } catch (IOException e) {

                throw new RuntimeException(e);
            }

        }

        @FXML
        protected void calendarButtonClick () throws IOException {

            try {
                Stage stage = (Stage) calendarButton.getScene().getWindow();
                CalendarView.loadCalendarScene(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void foodButtonClick () {
            try {
                Stage stage = (Stage) foodButton.getScene().getWindow();
                CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @FXML
        protected void journalButtonClick () throws IOException {

            try {
                Stage stage = (Stage) journalButton.getScene().getWindow();

                JournalEntry.loadJournalScene(stage);
            } catch (IOException e) {

                throw new RuntimeException(e);
            }

        }

        @FXML
        protected void settingsButtonClick () throws IOException {

            try {
                Stage stage = (Stage) profileButton.getScene().getWindow();

                UserProfile.loadSettingsScene(stage);
            } catch (IOException e) {

                throw new RuntimeException(e);
            }

        }

        private void showAlert (Alert.AlertType alertType, String message){

            Alert alert = new Alert(alertType);
            alert.setTitle(alert.getTitle());
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        }

    @FXML
    private void openEmergencyCard() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/elevate5/elevateyou/EmergencyCard.fxml")
            );
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Emergency Card");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setResizable(false);
            dialog.setScene(new Scene(root));
            dialog.show();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Failed to open Emergency Card:\n" + e.getMessage()
            ).showAndWait();
        }
    }
    @FXML private Button exportButton;
    @FXML private Button importButton;
    private final UserBackupService backupService = new UserBackupService();
    private String uid;
    @FXML
    public void initialize() {
        if (SessionManager.getSession() != null &&
                SessionManager.getSession().getUserID() != null) {
            uid = SessionManager.getSession().getUserID();
        } else {
            uid = "dev-demo-uid";
        }

        if (uid == null) {
            if (exportButton != null) exportButton.setDisable(true);
            if (importButton != null) importButton.setDisable(true);
        }
    }
    @FXML
    private void onExportAllData(ActionEvent event) {
        if (uid == null) {
            showAlert(Alert.AlertType.ERROR, "Not logged in", "No user session found.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Export Data");
        confirm.setHeaderText("Export all your data?");
        confirm.setContentText("This will export your profile, medications, events, water, etc. "
                + "to a JSON file. It does NOT delete anything in the cloud.");
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            String json = backupService.exportUserData(uid);

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Backup File");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json"),
                    new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
            );
            chooser.setInitialFileName("ElevateYou-backup-" + uid + ".json");

            Stage stage = (Stage) exportButton.getScene().getWindow();
            File file = chooser.showSaveDialog(stage);
            if (file == null) return;

            try (FileWriter fw = new FileWriter(file, false)) {
                fw.write(json);
            }

            showAlert(Alert.AlertType.INFORMATION,
                    "Export finished",
                    "Backup saved to:\n" + file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Export failed",
                    "Error: " + e.getMessage());
        }
    }
    @FXML
    private void onImportAllData(ActionEvent event) {
        if (uid == null) {
            showAlert(Alert.AlertType.ERROR, "Not logged in", "No user session found.");
            return;
        }

        Alert warn = new Alert(Alert.AlertType.CONFIRMATION);
        warn.setTitle("Import Data");
        warn.setHeaderText("Restore data from backup?");
        warn.setContentText(
                "This will OVERWRITE your current data with the contents of the selected backup file.\n\n"
                        + "Make sure the file was exported by ElevateYou and belongs to you."
        );
        if (warn.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        try {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose Backup File");
            chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON Files (*.json)", "*.json"),
                    new FileChooser.ExtensionFilter("All Files (*.*)", "*.*")
            );

            Stage stage = (Stage) importButton.getScene().getWindow();
            File file = chooser.showOpenDialog(stage);
            if (file == null) return;

            String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);

            backupService.importUserData(uid, json);

            showAlert(Alert.AlertType.INFORMATION,
                    "Import finished",
                    "Data has been restored from:\n" + file.getAbsolutePath()
                            + "\n\nYou may need to reopen some screens to see the changes.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                    "Import failed",
                    "Error: " + e.getMessage());
        }
    }

}
