package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserProfile {

    public static void loadSettingsScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserProfile.class.getResource("UserProfile.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Profile Settings");
        stage.setScene(scene);
        stage.show();
    }
}
