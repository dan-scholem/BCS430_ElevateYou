package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class CaloriesWaterIntake {
    private CaloriesWaterIntake() {}

    public static void loadCaloriesWaterIntakeScene(Stage stage) throws IOException {
        FXMLLoader fxml = new FXMLLoader(App.class.getResource("CaloriesWaterIntake.fxml"));
        Scene scene = new Scene(fxml.load());
        stage.setTitle("Nutrition");
        stage.setScene(scene);
        stage.show();
    }
}
