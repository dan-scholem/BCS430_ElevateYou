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
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AddFriendView extends Application {

    public static Firestore fstore;
    public static FirebaseAuth fauth;
    private final FirestoreContext contxtFirebase = new FirestoreContext();
    public static User theUser;

    @FXML
    private void userSearchButtonClick(ActionEvent event) {
        ApiFuture<QuerySnapshot> future = fstore.collection("Users").get();

        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents;

        try {
            documents = future.get().getDocuments();

            if (!documents.isEmpty()) {

                for (QueryDocumentSnapshot document : documents) {
                    System.out.println(document.getId());
                }
            }
        }catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {

        try {
            fstore = contxtFirebase.firebase();
            fauth = FirebaseAuth.getInstance();
            theUser = new User();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("AddFriendView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 300 , 400);
        stage.setTitle("Search Users");
        stage.setScene(scene);
        stage.show();

    }
}
