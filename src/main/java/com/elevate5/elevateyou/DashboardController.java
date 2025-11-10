package com.elevate5.elevateyou;

import com.elevate5.elevateyou.service.NotificationService;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.AppointmentView;
import com.elevate5.elevateyou.view.CalendarView;
import com.elevate5.elevateyou.view.FriendsListView;
import com.elevate5.elevateyou.viewmodel.FriendsListViewModel;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.Scene;

import java.io.IOException;

public class DashboardController {

    @FXML
    public Label welcometext;
    @FXML
    private Button appointmentsButton;

    @FXML
    private Button calendarButton;

    @FXML
    private Button chatButton;

    @FXML
    private Button dashButton;

    @FXML
    private Button exerciseButton;

    @FXML
    private Button foodButton;

    @FXML
    private Button friendsButton;

    @FXML
    private Button journalButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button medButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button reviewsButton;

    @FXML
    private Button sleepButton;

    @FXML
    private Button tutorialsButton;

    @FXML
    private HBox topRightBar;



    private Session session;

    @FXML
    public void initialize() {
        session = SessionManager.getSession();
        String uid = session.getUser().getUid();
        try {
            DocumentReference docRef = App.fstore.collection("Users").document(uid);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot doc = future.get();
            String name = (String) doc.get("FirstName");
            if (name != null) {
                welcometext.setText("Good Morning, " + name);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Fallback wiring in case the FXML 'onAction' was lost
        if (exerciseButton != null) {
            exerciseButton.setOnAction(this::exerciseButtonClick);
        } else {
            System.out.println("[NAV] exerciseButton is null — check fx:id in Dashboard.fxml");
        }

        setNotificationsButton();
    }

    // Logout
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
    protected void settingsButtonClick() throws IOException {

        try {
            Stage stage = (Stage) profileButton.getScene().getWindow();

            UserProfile.loadSettingsScene(stage);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    @FXML
    public void setNotificationsButton() {
        try {
            String uid = (SessionManager.getSession() != null && SessionManager.getSession().getUserID() != null)
                    ? SessionManager.getSession().getUserID()
                    : "";
            String email = (SessionManager.getSession() != null && SessionManager.getSession().getUser() != null)
                    ? SessionManager.getSession().getUser().getEmail()
                    : "";

            NotificationService svc = new NotificationService(uid, email);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elevate5/elevateyou/Notification.fxml"));
            Node bell = loader.load();

            NotificationController ctrl = loader.getController();
            ctrl.setService(svc);

            topRightBar.getChildren().add(bell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Exercise navigation
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

    @FXML
    protected void friendsButtonClick() {
        try {
            Stage stage = (Stage) friendsButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/elevate5/elevateyou/FriendsListView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            FriendsListView controller = fxmlLoader.getController();
            controller.setViewModel(new FriendsListViewModel());
            stage.setTitle("Friends");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
