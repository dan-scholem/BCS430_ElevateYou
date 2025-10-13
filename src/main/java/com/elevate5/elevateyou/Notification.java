package com.elevate5.elevateyou;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Notification {
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("Notification.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Notification");
        stage.setScene(scene);
        stage.show();
    }
}
