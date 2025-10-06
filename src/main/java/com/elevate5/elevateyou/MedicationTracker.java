package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MedicationTracker {

    public static void loadMedTrackerScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("MedicationTracker.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Medication Tracker");
        stage.setScene(scene);
        stage.show();
    }
}
