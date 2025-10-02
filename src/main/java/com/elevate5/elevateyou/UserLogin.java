package com.elevate5.elevateyou;

import com.elevate5.elevateyou.util.DatabaseConnection;
import com.elevate5.elevateyou.util.DatabaseUtil;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class UserLogin extends Application {

    public static Firestore firestore;
    public static FirebaseAuth firebaseAuth;
    private final com.elevate5.elevateyou.model.FirestoreContext firestoreContext = new com.elevate5.elevateyou.model.FirestoreContext();

        public static void main(String[] args) {

            /*
            try(Connection conn = DatabaseConnection.connectTestDatabase()){
                assert conn != null;
                DatabaseUtil.createTables(conn);
            } catch(SQLException e){
                System.out.println(e.getMessage());
            }
             */

            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws IOException {

            firestore = firestoreContext.firebase();
            firebaseAuth = FirebaseAuth.getInstance();

            FXMLLoader fxmlLoader = new FXMLLoader(com.elevate5.elevateyou.UserLogin.class.getResource("UserLogin.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 669);
            primaryStage.setTitle("ElevateYou");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(true);
        }

    }