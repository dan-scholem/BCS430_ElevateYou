package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Dashboard  {

    public static void loadDashboardScene(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("/com/elevate5/elevateyou/LandingView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Dashboard");
            stage.setScene(scene);
            stage.show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
