package com.elevate5.elevateyou;

import com.elevate5.elevateyou.model.FirestoreContext;
import com.elevate5.elevateyou.model.User;

import com.elevate5.elevateyou.session.SessionManager;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;



import java.io.IOException;


public class App extends Application {

    public static Firestore fstore;
    public static FirebaseAuth fauth;
    private final FirestoreContext contxtFirebase = new FirestoreContext();
    public static User theUser;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {


        try {
            fstore = contxtFirebase.firebase();
            fauth = FirebaseAuth.getInstance();
            theUser = new User();
            UserLogin.loadUserLoginScene(stage);
        }

        catch (IOException e) {

            showAlert(Alert.AlertType.INFORMATION, "Failed to load User Login");
        }
    }

    @Override
    public void stop() {
        if(SessionManager.getSession() != null){
            SessionManager.closeSession();
        }
        Platform.exit();
        System.exit(0);
    }

    private void showAlert(Alert.AlertType alertType, String message) {

        Alert alert = new Alert(alertType);
        alert.setTitle(alert.getTitle());
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}
