package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.UserLogin;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorSearchView extends Application {







    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("DoctorSearchView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1008 , 631);
        stage.setTitle("Search Doctors");
        stage.setScene(scene);
        stage.show();
        //stage.setResizable(false);
    }
}
