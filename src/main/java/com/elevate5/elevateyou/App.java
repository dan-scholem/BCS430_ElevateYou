package com.elevate5.elevateyou;

import com.elevate5.elevateyou.util.DatabaseConnection;
import com.elevate5.elevateyou.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {

        /*
        try(Connection conn = DatabaseConnection.connectTestDatabase()){
            assert conn != null;
            DatabaseUtil.createTables(conn);
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

         */

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("LandingView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        primaryStage.setTitle("ElevateYou");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
