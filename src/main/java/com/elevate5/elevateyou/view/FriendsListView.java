package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.*;
import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.session.SessionManager;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class FriendsListView extends Application {

    private ArrayList<User> friends;
    @FXML
    private Button appointmentsButton;

    @FXML
    private Button calendarButton;

    @FXML
    private Button dashButton;

    @FXML
    private Button exerciseButton;

    @FXML
    private Button foodButton;

    @FXML
    private Button journalButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button medButton;

    @FXML
    private Button sleepButton;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private void searchButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("SearchUsersView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800 , 500);
        Stage addFriendStage = new Stage();
        SearchUsersView controller = fxmlLoader.getController();
        if(searchField.getText().isEmpty()){
            searchField.setText("");
        }
        controller.initData(searchField.getText());
        addFriendStage.setTitle("Search Users");
        addFriendStage.setScene(scene);
        addFriendStage.show();
    }


    @FXML
    private void logoutUser(ActionEvent event) throws IOException {
        Stage stage;
        Alert logoutalert = new Alert(Alert.AlertType.CONFIRMATION);
        logoutalert.setTitle("Logout");
        logoutalert.setHeaderText("You are about to logout!");
        logoutalert.setContentText("Are you sure you want to logout?");

        if (logoutalert.showAndWait().get() == ButtonType.OK) {
            stage = (Stage) logoutButton.getScene().getWindow();
            System.out.println("User logged out successfully");
            SessionManager.closeSession();
            stage.close();
            UserLogin.loadUserLoginScene(stage);
        }
    }

    @FXML
    protected void dashboardButtonClick() {
        try {
            Stage stage = (Stage) dashButton.getScene().getWindow();
            Dashboard.loadDashboardScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void medicationButtonClick() throws IOException {
        try {
            Stage stage = (Stage) medButton.getScene().getWindow();
            Medication.loadMedTrackerScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void calendarButtonClick() throws IOException {
        try {
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            CalendarView.loadCalendarScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void foodButtonClick() {
        try {
            Stage stage = (Stage) foodButton.getScene().getWindow();
            CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void journalButtonClick() throws IOException {
        try {
            Stage stage = (Stage) journalButton.getScene().getWindow();
            JournalEntry.loadJournalScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void exerciseButtonClick(ActionEvent event) {
        System.out.println("[NAV] Exercise clicked"); // visibility in console

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/elevate5/elevateyou/ui/exercise.fxml")
            );
            Node view = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene((javafx.scene.Parent) view, 900, 600));
            stage.setTitle("Exercise Tracker");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Exercise:\n" + e.getMessage()).showAndWait();
        }
    }
    @FXML
    public void appointmentButtonClick() throws IOException {
        try {
            Stage stage = (Stage) appointmentsButton.getScene().getWindow();
            AppointmentView.loadAppointmentScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void sleepButtonClick() {
        try {
            Stage s = (Stage) sleepButton.getScene().getWindow();
            Sleep.loadSleepScene(s);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("FriendsListView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1218 , 738);
        stage.setTitle("Friends List");
        stage.setScene(scene);
        stage.show();
    }
}
