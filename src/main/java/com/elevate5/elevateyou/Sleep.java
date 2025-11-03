package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Sleep {
    public static void loadSleepScene(Stage stage) {
        try {
            Parent root = FXMLLoader.load(
                    Sleep.class.getResource("/com/elevate5/elevateyou/Sleep.fxml")
            );
            stage.setTitle("ElevateYou - Sleep");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Sleep.fxml", e);
        }
    }
}