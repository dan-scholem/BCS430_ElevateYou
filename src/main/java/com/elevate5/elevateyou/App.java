package com.elevate5.elevateyou;

import com.elevate5.elevateyou.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {

        try(Connection conn = DatabaseConnection.connect()){
            assert conn != null;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select * from appointment_log");
            while (rs.next()) {
                System.out.println(rs.getInt("user_id") + " " + rs.getString("appointment_date") + " " + rs.getString("appointment_time") + " " + rs.getString("doc_name") + " " + rs.getString("appointment_notes"));
            }
            stmt.close();
        } catch(SQLException e){
            System.out.println(e.getMessage());
        }

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
