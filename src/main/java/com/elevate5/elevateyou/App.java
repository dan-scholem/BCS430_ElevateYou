package com.elevate5.elevateyou;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.*;

import java.io.IOException;

public class App extends Application {

    public static void main(String[] args) {

        connect();
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

    //connect to sqlite database (testdatabase.db)
    public static void connect(){
        //path to the database
        var url = "jdbc:sqlite:testdatabase.db";
        Connection conn = null;
        try{
            //open connection to database
            conn = DriverManager.getConnection(url);
            System.out.println("Connected to database successfully");

            //statement for queries
            Statement stmt = conn.createStatement();

            //create table and enter values for testing
            //stmt.executeUpdate("drop table if exists users");
            //stmt.executeUpdate("create table users (id integer, name string)");
            //stmt.executeUpdate("insert into users (id, name) values (1, 'John')");
            //stmt.executeUpdate("insert into users (id, name) values (2, 'Michael')");

            //execute query selecting all from users table
            ResultSet rs = stmt.executeQuery("select * from users");
            //print data to console
            while(rs.next()){
                System.out.println(rs.getInt("id") + " " + rs.getString("name"));
            }
            stmt.close();
        } catch (SQLException e){ System.out.println(e.getMessage()); }
        finally{
            try{
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException ex){ System.out.println(ex.getMessage()); }
        }
    }
}
