package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.*;
import com.elevate5.elevateyou.model.AppointmentModel;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.viewmodel.AppointmentViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class AppointmentView {

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
    private Button addAppointmentButton;
    @FXML
    private Button deleteAppointmentButton;
    @FXML
    private Button searchDoctorsButton;
    @FXML
    private TableView<AppointmentModel> appointmentTable;
    @FXML
    private TableColumn<AppointmentModel, String> apptDateCol;
    @FXML
    private TableColumn<AppointmentModel, String> apptTimeCol;
    @FXML
    private TableColumn<AppointmentModel, String> apptDocNameCol;
    @FXML
    private TableColumn<AppointmentModel, String> apptTypeCol;
    @FXML
    private TableColumn<AppointmentModel, String> apptPhoneCol;
    @FXML
    private TableColumn<AppointmentModel, String> apptAddressCol;
    @FXML
    private TableColumn<AppointmentModel, String> apptNotesCol;

    private ObservableList<AppointmentModel> appointmentData;
    private AppointmentViewModel  appointmentViewModel = new AppointmentViewModel();
    private Session session;

    @FXML
    public void initialize() {
        session = SessionManager.getSession();

        if(session.getUserAppointmentManager() != null){
            appointmentData = FXCollections.observableArrayList(session.getUserAppointmentManager().getAppointments());
        }


        apptDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        apptTimeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        apptDocNameCol.setCellValueFactory(new PropertyValueFactory<>("docName"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptPhoneCol.setCellValueFactory(new PropertyValueFactory<>("docPhone"));
        apptAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        apptNotesCol.setCellValueFactory(new PropertyValueFactory<>("notes"));

        appointmentTable.setItems(appointmentData);

    }

    @FXML
    private void addAppointmentButtonAction(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("AddAppointmentView.fxml"));

        Scene addAppointmentScene = new Scene(fxmlLoader.load(),500,500);
        Stage addAppointmentStage = new Stage();
        addAppointmentStage.initOwner(((Node)event.getSource()).getScene().getWindow());
        addAppointmentStage.setScene(addAppointmentScene);
        addAppointmentStage.setResizable(false);
        addAppointmentStage.setTitle("Add Appointment");
        addAppointmentStage.showAndWait();

        appointmentData = FXCollections.observableArrayList(session.getUserAppointmentManager().getAppointments());
        appointmentTable.setItems(appointmentData);
        appointmentTable.refresh();


    }

    @FXML
    private void deleteAppointmentButtonAction(ActionEvent event) throws IOException {

        AppointmentModel selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        appointmentViewModel.deleteAppointment(selectedAppointment);
        appointmentData.remove(selectedAppointment);
        appointmentTable.refresh();

    }

    @FXML
    private void searchDoctorsButtonAction(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("DoctorSearchView.fxml"));

        Scene searchDoctorsScene = new Scene(fxmlLoader.load(),900,400);
        Stage searchDoctorsStage = new Stage();
        searchDoctorsStage.initOwner(((Node)event.getSource()).getScene().getWindow());
        searchDoctorsStage.initModality(Modality.WINDOW_MODAL);
        searchDoctorsStage.setScene(searchDoctorsScene);
        searchDoctorsStage.setTitle("Search Doctors");
        searchDoctorsStage.showAndWait();

        appointmentData = FXCollections.observableArrayList(session.getUserAppointmentManager().getAppointments());
        appointmentTable.setItems(appointmentData);
        appointmentTable.refresh();

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
    public static void loadAppointmentScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("AppointmentView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Appointments");
        stage.setScene(scene);
        stage.show();
    }


}
