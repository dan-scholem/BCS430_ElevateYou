package com.elevate5.elevateyou;

import com.elevate5.elevateyou.util.DatabaseConnection;
import com.elevate5.elevateyou.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class CreateAccount {

    @FXML
    public static void loadCreateAccountScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("CreateAccount.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 869 , 629);
        stage.setTitle("Create Account");
        stage.setScene(scene);
        stage.show();
    }

    /*
        public static void main(String[] args) {


            try(Connection conn = DatabaseConnection.connectTestDatabase()){
                assert conn != null;
                DatabaseUtil.createTables(conn);
            } catch(SQLException e){
                System.out.println(e.getMessage());
            }

             */

         /*   launch(args);

          */
        }



/*
    @Override
        public void start(Stage primaryStage) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader(com.elevate5.elevateyou.CreateAccount.class.getResource("CreateAccount.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 872, 629);
            primaryStage.setTitle("Create Account");
            primaryStage.setScene(scene);
            primaryStage.show();
            primaryStage.setResizable(false);
        }

 */



