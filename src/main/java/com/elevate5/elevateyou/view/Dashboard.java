package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.App;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Dashboard extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1072, 669);
        primaryStage.setTitle("ElevateYou");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
