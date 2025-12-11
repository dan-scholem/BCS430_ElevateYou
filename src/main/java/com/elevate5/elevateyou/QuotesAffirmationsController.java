package com.elevate5.elevateyou;

import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.AppointmentView;
import com.elevate5.elevateyou.view.CalendarView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.ArrayList;


public class QuotesAffirmationsController implements Initializable {

        @FXML
        private TextField affirmationField;

        @FXML
        private Button appointmentsButton;

        @FXML
        private Button calendarButton;

        @FXML
        private Button chatButton;

        @FXML
        private Button dashButton;

        @FXML
        private Button exerciseButton;

        @FXML
        private Button foodButton;

        @FXML
        private Button friendsButton;

        @FXML
        private Button journalButton;

        @FXML
        private Button logoutButton;

        @FXML
        private Button medButton;

        @FXML
        private Label quotelabel;

        @FXML
        private Button quotesaffirmationBtn;

        @FXML
        private Button reviewsButton;

        @FXML
        private TextArea quoteaffirmationArea;

        @FXML
        private Button sleepButton;

        @FXML
        private Button tutorialsButton;

        @FXML
        private ComboBox<String> affirmationthemes;

        @FXML
        private ComboBox<String> quotethemes;

        @FXML
        private ListView<String> customAffirmationsList;

        private List<String> customAffirmations;

        private String currentEmail;

        public void setCurrentEmail(String email) {
            this.currentEmail = email;

            loadCustomAffirmations(email);
        }

        @Override
        public void initialize(URL url, ResourceBundle rb) {

            /** Obtain email from Firebase session **/

            Session usersession = SessionManager.getSession();

            if (usersession != null && usersession.getUser() != null) {

                this.currentEmail = usersession.getUser().getEmail();

                System.out.println("Email from this session: " + currentEmail);

                loadCustomAffirmations(currentEmail);
            }

            else {
                System.out.println("User session not found");
            }

                affirmationthemes.setItems(FXCollections.observableArrayList("Self-Love", "Confidence", "Mindfulness", "Health", "Growth"));

                quotethemes.setItems(FXCollections.observableArrayList("Inspirational","Success", "Motivational"));



        }

        // Method that uses an API to fetch a positive quote that the user can read
        @FXML
        private void fetchrandomQuote (ActionEvent event) throws IOException, InterruptedException {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://quotes-api12.p.rapidapi.com/quotes"))
                        .header("x-rapidapi-key", "607d095e27msh7f1200d0b409a4ap1d4380jsnddd3430a3d0c")
                        .header("x-rapidapi-host", "quotes-api12.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());

                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

                String quoteResponse = jsonObject.get("quote").getAsString();
                String quoteAuthor = jsonObject.get("author").getAsString();

                quoteaffirmationArea.setText(quoteResponse + "\n" + " - " + quoteAuthor);
                quoteaffirmationArea.setWrapText(true);
                quoteaffirmationArea.setEditable(false);

        }

        /** Method to display a quote given the selected theme **/
        @FXML
        private void displayThemedQuote() throws IOException, InterruptedException {

                String quotheme = quotethemes.getValue();

                if (quotheme == null || quotheme.isEmpty()) {

                    quoteaffirmationArea.setText("Please select a theme.");

                    return;
                }

                if (quotheme.equals("Motivational")) {

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://motivational-content.p.rapidapi.com/quotes"))
                            .header("x-rapidapi-key", "607d095e27msh7f1200d0b409a4ap1d4380jsnddd3430a3d0c")
                            .header("x-rapidapi-host", "motivational-content.p.rapidapi.com")
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .build();
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

                    JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();

                    if (!jsonArray.isEmpty()) {

                        int quoteArray = new Random().nextInt(jsonArray.size());

                        JsonObject quoteObject = jsonArray.get(quoteArray).getAsJsonObject();

                        String quoteResponse = quoteObject.get("quote").getAsString();

                        quoteaffirmationArea.setText(quoteResponse);
                        quoteaffirmationArea.setWrapText(true);
                        quoteaffirmationArea.setEditable(false);

                    }

                }

                else {
                    String apiquotetheme = quotheme.toLowerCase();

                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create("https://quotes-api12.p.rapidapi.com/quotes/random?type=" + apiquotetheme))
                            .header("x-rapidapi-key", "607d095e27msh7f1200d0b409a4ap1d4380jsnddd3430a3d0c")
                            .header("x-rapidapi-host", "quotes-api12.p.rapidapi.com")
                            .method("GET", HttpRequest.BodyPublishers.noBody())
                            .build();
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    System.out.println(response.body());

                    JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

                    String quoteResponse = jsonObject.get("quote").getAsString();
                    String quoteAuthor = jsonObject.get("author").getAsString();

                    quoteaffirmationArea.setText(quoteResponse + " - " + quoteAuthor);
                    quoteaffirmationArea.setWrapText(true);
                    quoteaffirmationArea.setEditable(false);
                }

        }


        /** Method that uses an API to fetch a daily affirmation that the user can read **/
        @FXML
        private void fetchrandomAffirmation(ActionEvent event) throws IOException, InterruptedException {

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://affirmation-api.p.rapidapi.com/affirmations/random"))
                        .header("x-rapidapi-key", "607d095e27msh7f1200d0b409a4ap1d4380jsnddd3430a3d0c")
                        .header("x-rapidapi-host", "affirmation-api.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());

                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

                String affirmationResponse = jsonObject.get("affirmation").getAsString();

                quoteaffirmationArea.setText("❤️ " + affirmationResponse + "❤️ ");
                quoteaffirmationArea.setWrapText(true);
                quoteaffirmationArea.setEditable(false);

        }

    /** Method to display an affirmation given the selected theme **/
        @FXML
        private void displayThemedAffirmation(ActionEvent event) throws IOException, InterruptedException {

            String affirmtheme = affirmationthemes.getValue();

            if (affirmtheme == null || affirmtheme.isEmpty()) {

                quoteaffirmationArea.setText("Please select a theme.");

                return;

            }

            String apiaffirmationtheme = affirmtheme.toLowerCase();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://affirmation-api.p.rapidapi.com/affirmations/random/" + apiaffirmationtheme))
                        .header("x-rapidapi-key", "607d095e27msh7f1200d0b409a4ap1d4380jsnddd3430a3d0c")
                        .header("x-rapidapi-host", "affirmation-api.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());

                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

                if(jsonObject.has("affirmation")) {

                    String affirmResponse = jsonObject.get("affirmation").getAsString();
                    quoteaffirmationArea.setText(affirmResponse);
                    quoteaffirmationArea.setWrapText(true);
                    quoteaffirmationArea.setEditable(false);

                }

                else {
                    quoteaffirmationArea.setText("No affirmations for the chosen theme.");
                }


        }


        // Method that allows the user to add a personal affirmation
        @FXML
        private void addAffirmation(ActionEvent event) throws IOException, InterruptedException {

            System.out.println("Current email is: " + currentEmail);

            String personalaffirmation = affirmationField.getText().trim();

                if (!personalaffirmation.isEmpty()) {
                        customAffirmationsList.getItems().add(personalaffirmation);
                        affirmationField.clear();
                        quoteaffirmationArea.setText("Personal affirmation successfully entered!");

                    System.out.println("Saving affirmation: " + personalaffirmation);

                        saveCustomAffirmations(currentEmail);
                }

                else {
                        quoteaffirmationArea.setText("Please enter an affirmation to add!");
                }
        }

        private void saveCustomAffirmations(String useremail) {

            try {

                if (useremail == null || useremail.isEmpty()) {
                    System.out.println("User email is null or empty!");
                    return;
                }

                Path filePath = Paths.get("src/main/resources/com/elevate5/elevateyou/personalaffirmations.txt");

                List<String> affirmationlines = new ArrayList<>();

                if (Files.exists(filePath)) {

                    affirmationlines = new ArrayList<>(Files.readAllLines(filePath));

                }

                affirmationlines.removeIf(line -> line.startsWith(useremail));

                for (String useraffirmation : customAffirmationsList.getItems()) {

                    affirmationlines.add(useremail + ": " + useraffirmation);
                }

                Files.createDirectories(filePath.getParent());
                Files.write(filePath, affirmationlines);

                System.out.println("Affirmations for: " + useremail);

                }
            catch (Exception e) {

                    System.out.println("Error saving affirmation to file: " + e.getMessage());
                }

            }

            private void loadCustomAffirmations(String useremail) {

            try {

                if (useremail == null || useremail.isEmpty()) {

                    System.out.println("User email is null or empty!");

                    return;
                }

                Path filePath = Paths.get("src/main/resources/com/elevate5/elevateyou/personalaffirmations.txt");

                customAffirmationsList.getItems().clear();

                if (Files.exists(filePath)) {

                    List<String> affirmationlines = Files.readAllLines(filePath);

                    for (String fileline : affirmationlines) {
                        if (fileline.startsWith(useremail)) {
                            String customaffirmation = fileline.substring(useremail.length() + 1);
                            customAffirmationsList.getItems().add(customaffirmation);
                        }
                    }

                    System.out.println("Affirmations for user email: " + useremail);
                }

            }
            catch (Exception e) {

                System.out.println("Error loading personal affirmation" + e.getMessage());
            }
        }

        @FXML
        private void viewAffirmation() {

            String selectedaffirmation = customAffirmationsList.getSelectionModel().getSelectedItem();

            if (selectedaffirmation != null) {
                quoteaffirmationArea.setText(" ✨ PERSONAL AFFIRMATION: ✨\n\n" + selectedaffirmation);
            }

            else {
                quoteaffirmationArea.setText("Please select an affirmation to view.");
            }
        }

        /** Method that allows the user to delete a selected personal affirmation **/
        @FXML
        private void deleteAffirmation(ActionEvent event) throws IOException, InterruptedException {

            int affirmationChoice = customAffirmationsList.getSelectionModel().getSelectedIndex();

            if (affirmationChoice >= 0) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setTitle("Confirm Deletion");

                alert.setHeaderText("Delete Affirmation");

                alert.setContentText("Do you want to delete this affirmation?");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {

                    customAffirmationsList.getItems().remove(affirmationChoice);

                    quoteaffirmationArea.setText("Affirmation removed from the list.");

                    saveCustomAffirmations(currentEmail);
                }


            else {
                    quoteaffirmationArea.setText("Please choose an affirmation to delete.");
                }

            }

        }

        /** Buttons to navigate to each of the feature screens **/
        @FXML
        protected void dashboardButtonClick() {

                try {

                        Stage stage = (Stage) dashButton.getScene().getWindow();

                        Dashboard.loadDashboardScene(stage);

                } catch (Exception e) {

                        throw new RuntimeException(e);
                }
        }

        @FXML
        protected void medicationButtonClick() throws IOException {

                try {
                        Stage stage = (Stage) medButton.getScene().getWindow();

                        Medication.loadMedTrackerScene(stage);
                } catch (IOException e) {

                        throw new RuntimeException(e);
                }

        }

        @FXML
        protected void calendarButtonClick() throws IOException {

                try {
                        Stage stage = (Stage) calendarButton.getScene().getWindow();
                        CalendarView.loadCalendarScene(stage);
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }
        }

        @FXML
        protected void foodButtonClick() {
                try {
                        Stage stage = (Stage) foodButton.getScene().getWindow();
                        CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @FXML
        protected void journalButtonClick() throws IOException {

                try {
                        Stage stage = (Stage) journalButton.getScene().getWindow();

                        JournalEntry.loadJournalScene(stage);
                } catch (IOException e) {

                        throw new RuntimeException(e);
                }

        }

        @FXML
        public void appointmentButtonClick() throws IOException {
                try {
                        Stage stage = (Stage) appointmentsButton.getScene().getWindow();
                        AppointmentView.loadAppointmentScene(stage);
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
        }

        @FXML
        public void sleepButtonClick() {
                try {
                        Stage s = (Stage) sleepButton.getScene().getWindow();
                        Sleep.loadSleepScene(s);
                } catch (Exception e) {
                        throw new RuntimeException();
                }
        }

        @FXML
        protected void quoteaffirmationButtonClick() throws IOException {

                try {
                        Stage stage = (Stage) quotesaffirmationBtn.getScene().getWindow();

                        QuotesAffirmations.loadQuotesAffirmationsScene(stage);
                }

                catch (IOException e) {

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

}
