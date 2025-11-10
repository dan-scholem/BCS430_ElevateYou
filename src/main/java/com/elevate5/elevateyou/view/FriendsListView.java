package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.*;
import com.elevate5.elevateyou.model.FirestoreContext;
import com.elevate5.elevateyou.model.FriendRequestModel;
import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FriendsListView extends Application {

    private ArrayList<User> friends;
    @FXML
    private Button appointmentsButton;

    @FXML
    private Button calendarButton;

    @FXML
    private Button dashButton;

    @FXML
    private Button exerciseButton;

    @FXML
    private Button foodButton;

    @FXML
    private Button journalButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button medButton;

    @FXML
    private Button sleepButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private BorderPane friendViewPane;

    @FXML
    private VBox friendsBox;

    public static Firestore fstore;
    private final FirestoreContext contxtFirebase = new FirestoreContext();

    private ArrayList<String> friendUids = new ArrayList<>();


    @FXML
    public void initialize() {
        try {
            fstore = contxtFirebase.firebase();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        if(fstore!=null){
            friendUids.add("C0UElqfKSSU1MNqwjxQobBLctg62");
            friendUids.add("NrpEXOpnWYXQx6IkMY2aZfSfTD42");
        }else{
            friendUids = SessionManager.getSession().getCurrUser().getFriendsList();
        }

        displayFriendsList();

    }


    @FXML
    private void friendsListButtonClick(ActionEvent event) {
        displayFriendsList();
    }


    private void displayFriendsList(){
        searchField.clear();
        friendsBox.getChildren().clear();
        try {
                for (String id : friendUids) {
                    DocumentReference docRef;
                    if(fstore == null){
                        docRef = App.fstore.collection("Users").document(id);
                    }else{
                        docRef = fstore.collection("Users").document(id);
                    }
                    ApiFuture<DocumentSnapshot> future = docRef.get();
                    DocumentSnapshot document = future.get();
                    updateFriendsBox(document);
                }

        }catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public HBox generateSearchResultBox(String docFirstName, String docLastName, String docEmail, String docUserId, String docProfilePicUrl, DocumentSnapshot document) {

        HBox userBox = new HBox();
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setSpacing(10);
        userBox.setStyle("-fx-border-color: black; -fx-padding: 10;");

        Button resultButton = generateResultButton(docFirstName, docLastName, docEmail, docUserId, docProfilePicUrl);
        userBox.getChildren().add(resultButton);

        if(!friendUids.contains(docUserId)){
            Button requestFriendButton = generateRequestButton(docUserId);
            userBox.getChildren().add(requestFriendButton);
        }else {
            Button removeFriendButton = generateRemoveFriendButton(docUserId);
            userBox.getChildren().add(removeFriendButton);
        }

        return userBox;
    }

    private Button generateResultButton(String docFirstName, String docLastName, String docEmail, String docUserId, String docProfilePicUrl) {
        Button resultButton = new Button();
        resultButton.wrapTextProperty().setValue(true);
        resultButton.setPrefWidth(friendsBox.getWidth());
        resultButton.setUserData(new User(docFirstName, docLastName, docEmail, docProfilePicUrl, docUserId));
        resultButton.setTooltip(new Tooltip(docUserId));
        resultButton.setText("    " + docFirstName + " " + docLastName);
        resultButton.setAlignment(Pos.TOP_LEFT);
        resultButton.setStyle("-fx-background-color: white;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 0px;" +
                "-fx-font-size: 30px;" +
                "-fx-pref-height: 40px;" +
                "-fx-pref-width: 500px; " +
                "-fx-padding: 0px 0px 0px 100px;" +
                "-fx-graphic: url('" + docProfilePicUrl + "');"
        );
        resultButton.setOnAction((e) -> {
            System.out.println(resultButton.getUserData().toString());
        });

        return resultButton;
    }

    private Button generateRequestButton(String docUserId){
        Button requestFriendButton = new Button();
        requestFriendButton.setText("Request Friend");
        requestFriendButton.setOnAction((event) -> {
            FriendRequestModel friendRequest = new FriendRequestModel(SessionManager.getSession().getUserID(),  docUserId);
            System.out.println("Sender: " + friendRequest.getSenderID() + ", Receiver: " + friendRequest.getReceiverID());
            DocumentReference senderDocRef;
            DocumentReference receiverDocRef;
            if(fstore == null){

                senderDocRef = App.fstore.collection("Users").document(SessionManager.getSession().getUserID());
                ArrayList<String> senderFriendRequestsList = SessionManager.getSession().getCurrUser().getSentFriendRequestsList();
                senderFriendRequestsList.add(friendRequest.getReceiverID());
                Map<String, Object> senderFriendsMap = new HashMap<>();
                senderFriendsMap.put("SentFriendRequests", senderFriendRequestsList);

                receiverDocRef = App.fstore.collection("Users").document(docUserId);
                ApiFuture<DocumentSnapshot> future = receiverDocRef.get();
                ArrayList<String> receiverFriendRequestsList = new ArrayList<>();
                try {
                    DocumentSnapshot receiverDoc = future.get();
                    receiverFriendRequestsList = new ArrayList<>((List<String>) receiverDoc.get("ReceivedFriendRequests"));
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }catch(NullPointerException e){
                    System.out.println("NullPointerException");
                }
                receiverFriendRequestsList.add(friendRequest.getSenderID());
                Map<String, Object> receiverFriendsMap = new HashMap<>();
                receiverFriendsMap.put("ReceivedFriendRequests", receiverFriendRequestsList);

                senderDocRef.update(senderFriendsMap);
                receiverDocRef.update(receiverFriendsMap);

                displayFriendsList();
            }
        });

        return requestFriendButton;
    }

    private Button generateRemoveFriendButton(String docUserId){

        Button removeFriendButton = new Button("Remove Friend");
        removeFriendButton.setOnAction((event) -> {

            SessionManager.getSession().getCurrUser().getFriendsList().remove(docUserId);
            Map<String, Object> senderFriendsMap = new HashMap<>();
            senderFriendsMap.put("Friends", SessionManager.getSession().getCurrUser().getFriendsList());
            DocumentReference senderDocRef = App.fstore.collection("Users").document(SessionManager.getSession().getUserID());
            senderDocRef.update(senderFriendsMap);

            DocumentReference receiverDocRef = App.fstore.collection("Users").document(docUserId);
            ApiFuture<DocumentSnapshot> future = receiverDocRef.get();
            DocumentSnapshot receiverDoc;
            try {
                receiverDoc = future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
            ArrayList<String> docFriendsList = new ArrayList<>((List<String>) receiverDoc.get("Friends"));
            docFriendsList.remove(SessionManager.getSession().getUserID());
            Map<String, Object> docFriendsMap = new HashMap<>();
            docFriendsMap.put("Friends", docFriendsList);
            receiverDocRef.update(docFriendsMap);

            displayFriendsList();
        });

        return removeFriendButton;
    }

    @FXML
    private void searchButtonClick(ActionEvent event) throws IOException {
        friendsBox.getChildren().clear();
        ApiFuture<QuerySnapshot> future;
        if(fstore == null){
            future = App.fstore.collection("Users").get();
        }else{
            future = fstore.collection("Users").get();
        }

        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();

            if (!documents.isEmpty()) {

                for (QueryDocumentSnapshot document : documents) {
                    updateFriendsBox(document);

                }
            }
        }catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private void updateFriendsBox(DocumentSnapshot document){
        String docFirstName = document.getString("FirstName");
        String docLastName = document.getString("LastName");
        String docEmail = document.getString("Email");
        String docUserId = document.getId();
        String docProfilePicUrl = document.getString("ProfilePicUrl");
        if(docProfilePicUrl == null){
            docProfilePicUrl = "https://icons.iconarchive.com/icons/iconarchive/childrens-book-animals/48/Duck-icon.png";
        }
        assert docFirstName != null;
        assert docLastName != null;
        if(docFirstName.toLowerCase().contains(searchField.getText().toLowerCase()) ||
                docLastName.toLowerCase().contains(searchField.getText().toLowerCase())){
            //
            HBox resultBox = generateSearchResultBox(docFirstName, docLastName, docEmail, docUserId, docProfilePicUrl, document);

            friendsBox.getChildren().add(resultBox);
        }
    }


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
    private void exerciseButtonClick(ActionEvent event) {
        System.out.println("[NAV] Exercise clicked"); // visibility in console

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/elevate5/elevateyou/ui/exercise.fxml")
            );
            Node view = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene((javafx.scene.Parent) view, 900, 600));
            stage.setTitle("Exercise Tracker");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Exercise:\n" + e.getMessage()).showAndWait();
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

    public static void loadFriendsScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("FriendsListView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Friends");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("FriendsListView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1218 , 738);
        stage.setTitle("Friends List");
        stage.setScene(scene);
        stage.show();
    }
}
