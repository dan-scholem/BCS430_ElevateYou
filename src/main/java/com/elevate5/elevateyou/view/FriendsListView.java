package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.viewmodel.FriendsListViewModel;
import com.google.cloud.firestore.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FriendsListView {

    @FXML
    private TextField searchField;
    @FXML
    private VBox friendsBox;
    @FXML
    private Label numRequestsLabel;
    @FXML
    private Label optionLabel;
    @FXML
    private BorderPane friendViewPane;
    @FXML
    private AnchorPane mainPane;

    private FriendsListViewModel friendsListViewModel;


    @FXML
    public void initialize() {



    }

    @FXML
    private void friendsListButtonClick(ActionEvent event) throws ExecutionException, InterruptedException {
        displayFriendsList();
    }

    @FXML
    private void requestsButtonClick(ActionEvent event) throws ExecutionException, InterruptedException {
        displayRequestsList();
    }

    @FXML
    private void searchButtonClick(ActionEvent event) throws IOException {
        displaySearchResults();
    }


    private void displayFriendsList() throws ExecutionException, InterruptedException {
        friendsBox.getChildren().clear();
        optionLabel.setText("Friends");
        friendViewPane.setCenter(mainPane);
        ArrayList<DocumentSnapshot> friendsList = friendsListViewModel.getUserDocs(friendsListViewModel.getFriendUids());
        displayList(friendsList);
        searchField.clear();
    }

    private void displayRequestsList() throws ExecutionException, InterruptedException {
        friendsBox.getChildren().clear();
        optionLabel.setText("Requests");
        friendViewPane.setCenter(mainPane);
        ArrayList<DocumentSnapshot> requestsList = friendsListViewModel.getUserDocs(friendsListViewModel.getRequestUids());
        displayList(requestsList);
        searchField.clear();
    }

    private void displaySearchResults(){
        friendsBox.getChildren().clear();
        optionLabel.setText("Search");
        friendViewPane.setCenter(mainPane);
        ArrayList<DocumentSnapshot> searchResultsList = friendsListViewModel.searchUsers();
        displayList(searchResultsList);
    }

    private void updateNumRequestsLabel() {
        if (friendsListViewModel.getRequestUids() != null &&
                !friendsListViewModel.getRequestUids().isEmpty()) {
            numRequestsLabel.setText(String.valueOf(friendsListViewModel.getRequestUids().size()));
            numRequestsLabel.setVisible(true);
        } else {
            numRequestsLabel.setVisible(false);
        }
    }

    private void displayList(ArrayList<DocumentSnapshot> documents) {
        for (DocumentSnapshot doc : documents) {
            String docFirstName = doc.getString("FirstName");
            String docLastName = doc.getString("LastName");
            String docEmail = doc.getString("Email");
            String docUserId = doc.getId();
            String docProfilePicUrl = doc.getString("ProfilePicUrl");
            String docBio = doc.getString("UserBio");
            if (docProfilePicUrl == null) {
                docProfilePicUrl = "https://icons.iconarchive.com/icons/iconarchive/childrens-book-animals/48/Duck-icon.png";
            }
            assert docFirstName != null;
            assert docLastName != null;
            HBox resultBox = generateSearchResultBox(docFirstName, docLastName, docEmail, docUserId, docProfilePicUrl, docBio);
            friendsBox.getChildren().add(resultBox);
        }
        if(friendsBox.getChildren().size() == 0){
            HBox noResultBox = generateNoResultBox();
            friendsBox.getChildren().add(noResultBox);
        }
        updateNumRequestsLabel();
    }

    public HBox generateSearchResultBox(String docFirstName, String docLastName, String docEmail, String docUserId, String docProfilePicUrl, String docBio) {

        HBox userBox = new HBox();
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setSpacing(10);
        userBox.setStyle("-fx-border-color: black; -fx-padding: 10;");

        Button resultButton = generateResultButton(docFirstName, docLastName, docEmail, docUserId, docProfilePicUrl, docBio);
        userBox.getChildren().add(resultButton);

        if (friendsListViewModel.containsFriendUid(docUserId)) {
            Button removeFriendButton = generateRemoveFriendButton(docUserId);
            userBox.getChildren().add(removeFriendButton);
        } else if (friendsListViewModel.containsReceivedFriendRequestUid(docUserId)) {
            Button acceptRequestButton = generateAcceptRequestButton(docUserId);
            userBox.getChildren().add(acceptRequestButton);
            Button denyRequestButton = generateDenyRequestButton(docUserId);
            userBox.getChildren().add(denyRequestButton);
        } else if(friendsListViewModel.containsSentFriendRequestUid(docUserId)) {
            Button requestFriendButton = generateRequestButton(docUserId);
            userBox.getChildren().add(requestFriendButton);
        } else{
            Button requestFriendButton = generateRequestButton(docUserId);
            userBox.getChildren().add(requestFriendButton);
            Button blockUserButton =  generateBlockButton(docUserId);
            userBox.getChildren().add(blockUserButton);
        }

        return userBox;
    }

    private Button generateResultButton(String docFirstName, String docLastName, String docEmail, String docUserId, String docProfilePicUrl, String docBio) {
        Button resultButton = new Button();
        resultButton.wrapTextProperty().setValue(true);
        resultButton.setPrefWidth(friendsBox.getWidth());
        User thisUser = new User(docFirstName, docLastName, docEmail, docProfilePicUrl, docUserId, docBio);
        resultButton.setUserData(thisUser);
        resultButton.setTooltip(new Tooltip(docUserId));
        resultButton.setText("    " + docFirstName + " " + docLastName);
        resultButton.setAlignment(Pos.TOP_LEFT);
        resultButton.setCursor(Cursor.HAND);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elevate5/elevateyou/ProfileView.fxml"));
            try {
                Parent profileRoot = loader.load();
                ProfileView profileView = loader.getController();
                profileView.setUser(thisUser);
                optionLabel.setText(thisUser.fullName());
                friendViewPane.setCenter(profileRoot);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return resultButton;
    }

    private Button generateRequestButton(String docUserId) {
        Button requestFriendButton = new Button();
        requestFriendButton.setText("Request Friend");
        requestFriendButton.setOnAction((event) -> {

            friendsListViewModel.sendFriendRequest(docUserId);
            displaySearchResults();

        });

        if (friendsListViewModel.containsSentFriendRequestUid(docUserId)) {
            requestFriendButton.setDisable(true);
            requestFriendButton.setText("Request Sent!");
        }
        if(friendsListViewModel.containsBlockedUser(docUserId)){
            requestFriendButton.setDisable(true);
        }
        requestFriendButton.setStyle("""
                -fx-font-family: "Segoe UI";
                    -fx-font-weight: bold;
                    -fx-background-color: #E29578;
                    -fx-background-radius: 30px;
                    -fx-text-fill: #000000;
                    -fx-border-color: #FFDDD2;
                    -fx-border-radius: 20px;""");

        return requestFriendButton;
    }


    private Button generateAcceptRequestButton(String docUserId) {
        Button acceptRequestButton = new Button("Accept");
        acceptRequestButton.setOnAction(event -> {

            friendsListViewModel.acceptFriendRequest(docUserId);
            try {
                displayFriendsList();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        acceptRequestButton.setStyle("""
             -fx-font-family: "Segoe UI";
             -fx-font-weight: bold;
             -fx-background-color: #E29578;
             -fx-background-radius: 30px;
             -fx-text-fill: #000000;
             -fx-border-color: #FFDDD2;
             -fx-border-radius: 20px;""");

        return acceptRequestButton;
    }

    private Button generateDenyRequestButton(String docUserId) {
        Button denyRequestButton = new Button("Deny");
        denyRequestButton.setOnAction(event -> {

            friendsListViewModel.denyFriendRequest(docUserId);
            try {
                displayRequestsList();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        denyRequestButton.setStyle("""
                -fx-font-family: "Segoe UI";
                    -fx-font-weight: bold;
                    -fx-background-color: #E29578;
                    -fx-background-radius: 30px;
                    -fx-text-fill: #000000;
                    -fx-border-color: #FFDDD2;
                    -fx-border-radius: 20px;""");

        return denyRequestButton;
    }


    private Button generateRemoveFriendButton(String docUserId) {

        Button removeFriendButton = new Button("Remove Friend");
        removeFriendButton.setOnAction((event) -> {

            friendsListViewModel.removeFriend(docUserId);
            try {
                displayFriendsList();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        removeFriendButton.setStyle("""
                -fx-font-family: "Segoe UI";
                    -fx-font-weight: bold;
                    -fx-background-color: #E29578;
                    -fx-background-radius: 30px;
                    -fx-text-fill: #000000;
                    -fx-border-color: #FFDDD2;
                    -fx-border-radius: 20px;""");

        return removeFriendButton;
    }

    private Button generateBlockButton(String docUserId) {

        Button blockButton = new Button("");
        blockButton.setPrefWidth(100);
        if(friendsListViewModel.containsBlockedUser(docUserId)) {
            blockButton.setText("Unblock");
        }else{
            blockButton.setText("Block");
        }
        blockButton.setOnAction((event) -> {

            if(friendsListViewModel.containsBlockedUser(docUserId)) {
                friendsListViewModel.unblockUser(docUserId);
            }else{
                friendsListViewModel.blockUser(docUserId);
            }
            displaySearchResults();

        });

        blockButton.setStyle("""
                -fx-font-family: "Segoe UI";
                    -fx-font-weight: bold;
                    -fx-background-color: #E29578;
                    -fx-background-radius: 30px;
                    -fx-text-fill: #000000;
                    -fx-border-color: #FFDDD2;
                    -fx-border-radius: 20px;""");

        return blockButton;
    }

    private HBox generateNoResultBox(){
        HBox noResultBox =  new HBox();
        noResultBox.setAlignment(Pos.CENTER);
        noResultBox.setPadding(new Insets(50));
        Label noResultLabel = new Label("Empty!");
        noResultBox.getChildren().add(noResultLabel);
        return noResultBox;
    }


    public void setViewModel(FriendsListViewModel friendsListViewModel) throws ExecutionException, InterruptedException {
        this.friendsListViewModel = friendsListViewModel;
        updateNumRequestsLabel();
        displayFriendsList();
        searchField.textProperty().bindBidirectional(friendsListViewModel.searchStringProperty());
    }


}
