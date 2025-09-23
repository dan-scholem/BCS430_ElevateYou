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

    //method for creating database tables
    public static void createTables(Connection conn) throws SQLException {

        Statement stmt = conn.createStatement();

        //create user table
        stmt.executeUpdate("drop table if exists users");
        stmt.executeUpdate("create table users (" +
                "user_id integer not null primary key, " +
                "name varchar(255) not null, " +
                "email varchar(255) not null unique, " +
                "hash_pass varchar(255) not null, " +
                "profile_image_url varchar(255))");
        stmt.close();
        //create profile table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists profile");
        stmt.executeUpdate("create table profile (" +
                "profile_id integer not null primary key, " +
                "user_id integer not null, " +
                "name varchar(255) not null, " +
                "email varchar(255) not null, " +
                "birthdate date not null, " +
                "gender varchar(255) not null, " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create food log table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists food_consumption");
        stmt.executeUpdate("create table food_consumption (" +
                "foodentry_id integer not null primary key, " +
                "user_id integer not null, " +
                "consumption_date date not null, " +
                "meal_type varchar(255) not null, " +
                "food_name varchar(255) not null, " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create workouts table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists workouts");
        stmt.executeUpdate("create table workouts (" +
                "workout_id integer not null primary key, " +
                "user_id integer not null, " +
                "workout_date date not null, " +
                "workout_time time not null, " +
                "workout_type varchar(50) not null, " +
                "notes varchar(255), " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create workout activity table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists workout_activity");
        stmt.executeUpdate("create table workout_activity (" +
                "activity_id integer not null primary key, " +
                "user_id integer not null, " +
                "activity_name varchar(255) not null, " +
                "activity_type varchar(50) not null, " +
                "sets varchar(50), " +
                "reps varchar(50), " +
                "calories_burned integer, " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create health metrics table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists health_metrics");
        stmt.executeUpdate("create table health_metrics (" +
                "metric_id integer not null primary key, " +
                "user_id integer not null, " +
                "metric_date date not null, " +
                "user_weight real not null, " +
                "user_height integer not null, " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create user goals table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists user_goals");
        stmt.executeUpdate("create table user_goals (" +
                "goals_id integer not null primary key, " +
                "user_id integer not null, " +
                "goal_type varchar(50) not null, " +
                "goal_start date not null, " +
                "goal_end date, " +
                "is_achieved integer not null default 0 check(is_achieved in (0,1)), " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create medications table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists medications");
        stmt.executeUpdate("create table medications (" +
                "medication_id integer not null primary key, " +
                "user_id integer not null, " +
                "medication_title varchar(255) not null, " +
                "med_dose varchar(255), " +
                "med_frequency varchar(50), " +
                "start_date date not null, " +
                "end_date date, " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create sleep record table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists sleep_record");
        stmt.executeUpdate("create table sleep_record (" +
                "sleep_id int not null primary key, " +
                "user_id integer not null, " +
                "sleep_date date not null, " +
                "start_sleep timestamp not null, " +
                "end_sleep timestamp not null, " +
                "sleep_duration integer, " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create motivation quotes table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists motivation_quotes");
        stmt.executeUpdate("create table motivation_quotes (" +
                "quote_id integer not null primary key, " +
                "quote_content varchar(255) not null, " +
                "quote_author varchar(50) not null)");
        stmt.close();
        //create journal entries table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists journal_entries");
        stmt.executeUpdate("create table journal_entries (" +
                "entry_id integer not null primary key, " +
                "user_id integer not null, " +
                "entry_date text not null default current_timestamp, " +
                "entry_title varchar(100) not null, " +
                "journal_context text not null, " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create appointment log table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists appointment_log");
        stmt.executeUpdate("create table appointment_log (" +
                "appointment_id integer not null primary key, " +
                "user_id integer not null, " +
                "appointment_date date not null, " +
                "appointment_time time not null, " +
                "doc_name varchar(255), " +
                "appointment_notes varchar(255), " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();
        //create reminders table
        stmt = conn.createStatement();
        stmt.executeUpdate("drop table if exists reminders");
        stmt.executeUpdate("create table reminders (" +
                "reminder_id integer not null primary key, " +
                "user_id integer not null, " +
                "reminder_content varchar(255) not null, " +
                "scheduled_time time not null, " +
                "is_active integer default 0 check ( is_active in (0,1) ), " +
                "foreign key (user_id) references users(user_id))");
        stmt.close();

    }
}
