package com.elevate5.elevateyou;

import com.elevate5.elevateyou.service.NotificationService;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.CalendarView;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
//import com.sun.webkit.BackForwardList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Node;

import java.io.IOException;

public class DashboardController {

    @FXML
    public Text welcometext;
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
    private Button reviewsButton;

    @FXML
    private Button sleepButton;

    @FXML
    private Button tutorialsButton;

    @FXML
    private Button notificationsButton;
    @FXML
    private HBox topRightBar;

/**
    private Session session;

    @FXML
    public void initialize(){
        session = SessionManager.getSession();
        String uid = session.getUser().getUid();
        try {
            DocumentReference docRef = App.fstore.collection("Users").document(uid);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot doc = future.get();
            String name = (String) doc.get("FirstName");
            welcometext.setText("Good Morning, " + name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        setNotificationsButton();

    }
    **/

    // This event is called to log the user out of the application and returns the user to the login screen
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
        } catch (IOException e){
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
    public void setNotificationsButton() {
        try {
            // Get current UID (fallback to dev uid for development)
            String uid = (SessionManager.getSession() != null && SessionManager.getSession().getUserID() != null)
                    ? SessionManager.getSession().getUserID()
                    : "";

            NotificationService svc = new NotificationService(uid);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/elevate5/elevateyou/Notification.fxml"));
            Node bell = loader.load();

            NotificationController ctrl = loader.getController();
            ctrl.setService(svc);

            topRightBar.getChildren().add(bell);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
