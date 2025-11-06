package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.UserLogin;
import com.elevate5.elevateyou.model.FirestoreContext;
import com.elevate5.elevateyou.model.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.auth.FirebaseAuth;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddFriendView extends Application {

    public static Firestore fstore;
    private final FirestoreContext contxtFirebase = new FirestoreContext();

    @FXML
    private TextField searchField;

    @FXML
    private VBox searchResultBox;


    @FXML
    public void initialize() {
        try {
            fstore = contxtFirebase.firebase();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


    public void initData(String searchString){
        searchField.setText(searchString);
        searchField.textProperty().setValue(searchString);
        displaySearchResults();
    }


    @FXML
    private void userSearchButtonClick(ActionEvent event) {
        displaySearchResults();
    }

    public void displaySearchResults(){
        searchResultBox.getChildren().clear();
        String searchString = searchField.getText().toLowerCase();
        ApiFuture<QuerySnapshot> future = fstore.collection("Users").get();

        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();

            if (!documents.isEmpty()) {

                for (QueryDocumentSnapshot document : documents) {
                    String docFirstName = document.getString("FirstName");
                    String docLastName = document.getString("LastName");
                    String docEmail = document.getString("Email");
                    String docUserId = document.getString("UserId");
                    String docProfilePicUrl = document.getString("ProfilePicUrl");
                    if(docProfilePicUrl == null){
                        docProfilePicUrl = "https://icons.iconarchive.com/icons/iconarchive/childrens-book-animals/48/Duck-icon.png";
                    }
                    assert docFirstName != null;
                    assert docLastName != null;
                    if(docFirstName.toLowerCase().contains(searchString.toLowerCase()) ||
                            docLastName.toLowerCase().contains(searchString.toLowerCase())){
                        //
                        Button resultButton = generateSearchResultButton(docFirstName, docLastName, docEmail, docUserId, docProfilePicUrl);

                        searchResultBox.getChildren().add(resultButton);
                    }

                }
            }
        }catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Button generateSearchResultButton(String docFirstName, String docLastName, String docEmail, String docUserId, String docProfilePicUrl) {

        Button resultButton = new Button();
        resultButton.wrapTextProperty().setValue(true);
        resultButton.setPrefWidth(searchResultBox.getWidth());
        resultButton.setUserData(new User(docFirstName, docLastName, docEmail, "", docUserId));
        resultButton.setText("            " + docFirstName + " " + docLastName);
        resultButton.setAlignment(Pos.TOP_LEFT);
        resultButton.setStyle("-fx-background-color: white;" +
                "-fx-border-color: black;" +
                "-fx-border-radius: 0px;" +
                "-fx-font-size: 30px;" +
                "-fx-pref-height: 40px;" +
                "-fx-pref-width: 500px; " +
                "-fx-graphic: url('" + docProfilePicUrl + "');"
        );
        resultButton.setOnAction((e) -> {
            System.out.println(resultButton.getUserData().toString());
        });
        return resultButton;
    }



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("AddFriendView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800 , 500);
        stage.setTitle("Search Users");
        stage.setScene(scene);
        stage.show();

    }
}
