package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CaloriesWaterIntake {

    public static void loadCaloriesWaterIntakeScene(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("CaloriesWaterIntake.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);
        stage.setTitle("Calories & Water Intake");
        stage.setScene(scene);
        stage.show();
    }
}
