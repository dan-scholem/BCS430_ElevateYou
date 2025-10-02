package com.elevate5.elevateyou;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Dashboard  {

    public static void loadDashboardScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1072 , 669);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();
    }
}
