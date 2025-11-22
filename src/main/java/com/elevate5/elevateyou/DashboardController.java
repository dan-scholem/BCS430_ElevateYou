package com.elevate5.elevateyou;

import com.elevate5.elevateyou.model.NotificationModel;
import com.elevate5.elevateyou.service.NotificationService;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.AppointmentView;
import com.elevate5.elevateyou.view.CalendarView;
import com.elevate5.elevateyou.view.FriendsListView;
import com.elevate5.elevateyou.view.ProfileView;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
    private Button quotesaffirmationBtn;

    @FXML
    private Button sleepButton;

    @FXML
    private Button articlesButton;

    @FXML
    private HBox topRightBar;

    @FXML
    private ImageView userImage;

    @FXML
    private BorderPane mainPane;

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

        setNotificationService();
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
            stage.setTitle("Dashboard");
            Dashboard.loadDashboardScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void medicationButtonClick() throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MedicationTracker.fxml"));
            Parent medicationRoot = loader.load();
            mainPane.setCenter(medicationRoot);
            Stage stage = (Stage) medButton.getScene().getWindow();
            stage.setTitle("Medication Tracker");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void calendarButtonClick() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CalendarView.fxml"));
            Parent calendarRoot = fxmlLoader.load();
            mainPane.setCenter(calendarRoot);
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            stage.setTitle("Calendar");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void foodButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CaloriesWaterIntake.fxml"));
            Parent nutritionRoot = fxmlLoader.load();
            mainPane.setCenter(nutritionRoot);
            Stage stage = (Stage) foodButton.getScene().getWindow();
            stage.setTitle("Nutrition Tracker");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void journalButtonClick() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PersonalJournal.fxml"));
            Parent journalRoot = fxmlLoader.load();
            mainPane.setCenter(journalRoot);
            Stage stage = (Stage) journalButton.getScene().getWindow();
            stage.setTitle("Journal");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void settingsButtonClick() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserProfile.fxml"));
            Parent profileRoot = fxmlLoader.load();
            mainPane.setCenter(profileRoot);
            Stage stage = (Stage) profileButton.getScene().getWindow();
            stage.setTitle("Profile");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML private Button bellButton;
    @FXML private Label badgeLabel;
    @FXML private VBox dropdownList;

    private NotificationService service;

    /** Injected by DashboardController after FXML load. */
    private void setNotificationService() {
        String uid = (SessionManager.getSession() != null && SessionManager.getSession().getUserID() != null)
                ? SessionManager.getSession().getUserID()
                : "";
        String email = (SessionManager.getSession() != null && SessionManager.getSession().getUser() != null)
                ? SessionManager.getSession().getUser().getEmail()
                : "";

        this.service = new NotificationService(uid, email);
        refreshBadge();
    }

    /** Toggle dropdown; refresh on open. */
    @FXML
    private void onBellClicked() {
        boolean show = !dropdownList.isVisible();
        dropdownList.setVisible(show);
        dropdownList.setManaged(show);
        if (show) {
            refreshList();
        }
    }

    /** Badge shows total count; hidden when zero. */
    private void refreshBadge() {
        if (service == null) return;
        List<NotificationModel> items = service.latest(200);
        int count = items.size();
        if (count <= 0) {
            badgeLabel.setText("");
            badgeLabel.setVisible(false);
            badgeLabel.setManaged(false);
        } else {
            badgeLabel.setText(String.valueOf(count));
            badgeLabel.setVisible(true);
            badgeLabel.setManaged(true);
        }
    }

    /** Render all items (no scroll, so keep list small in service.latest). */
    private void refreshList() {
        dropdownList.getChildren().clear();
        List<NotificationModel> items = service.latest(50);
        if (items.isEmpty()) {
            dropdownList.getChildren().add(new Label("No notifications found"));
            return;
        }
        for (NotificationModel n : items) {
            Label row = new Label(n.title + ": " + n.body); // e.g., "Appointment: You have Dentist at 2025-10-21 09:00"
            row.setWrapText(true);
            row.getStyleClass().add("notif-item"); // hook for CSS
            dropdownList.getChildren().add(row);
        }
    }

    // Exercise navigation (FIXED PATH)
    @FXML
    private void exerciseButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("ui/exercise.fxml") // <-- NOTE /ui/ here
            );
            Parent exerciseRoot = loader.load();
            mainPane.setCenter(exerciseRoot);
            Stage stage = (Stage) exerciseButton.getScene().getWindow();
            stage.setTitle("Exercise Tracker");
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Exercise:\n" + e.getMessage()).showAndWait();
        }
    }

    @FXML
    public void appointmentButtonClick() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("AppointmentView.fxml"));
            Parent appointmentRoot = fxmlLoader.load();
            mainPane.setCenter(appointmentRoot);
            Stage stage = (Stage) appointmentsButton.getScene().getWindow();
            stage.setTitle("Appointments");
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
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void quoteaffirmationButtonClick() throws IOException {

        try {
            Stage stage = (Stage) quotesaffirmationBtn.getScene().getWindow();

            Session session = SessionManager.getSession();

            String userEmail = session.getUser().getEmail();

            System.out.println("User email: " + userEmail);

            QuotesAffirmations.loadQuotesAffirmationsScene(stage);
        }

        catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

    @FXML
    protected void articlesButtonClick() throws IOException {
        try {
            Stage stage = (Stage) articlesButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/elevate5/elevateyou/ArticleView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Articles");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void chatButtonClick(){
        try {
            Stage stage = (Stage) chatButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/elevate5/elevateyou/LiveChat.fxml")
            );
            javafx.scene.Parent root = loader.load();
            stage.setScene(new Scene(root, 1056, 756));
            stage.setTitle("Live Chat");
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to open Live Chat", e);
        }
    }

}
