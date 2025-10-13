package com.elevate5.elevateyou;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class MedicationController {

    @FXML
    private Button dashButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button medButton;

    @FXML
    private Button addButton;

    @FXML
    private Button clearButton;

    @FXML
    private DatePicker startdateField;

    @FXML
    private DatePicker enddateField;

    @FXML
    private Button deleteButton;

    @FXML
    private TextField dosageField;

    @FXML
    private ComboBox<String> frequencyField;

    @FXML
    private TextField mednameField;

    @FXML
    private TextArea notesField;

    @FXML
    private Button updateButton;

    @FXML
    private TableView<Medication> MedicationTable;
    @FXML
    private TableColumn<Medication, String> medNameColumn;
    @FXML
    private TableColumn<Medication, String> dosageColumn;
    @FXML
    private TableColumn<Medication, String> frequencyColumn;
    @FXML
    private TableColumn<Medication, LocalDate> startdateColumn;
    @FXML
    private TableColumn<Medication, LocalDate> enddateColumn;
    @FXML
    private TableColumn<Medication, String> notesColumn;

    private final ObservableList<Medication> medications = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        medNameColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("medicationName"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("dosage"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("frequency"));
        startdateColumn.setCellValueFactory(new PropertyValueFactory<Medication, LocalDate>("startDate"));
        enddateColumn.setCellValueFactory(new PropertyValueFactory<Medication, LocalDate>("endDate"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("notes"));
        MedicationTable.setItems(medications);

        frequencyField.setItems(FXCollections.observableArrayList("As needed", "Once a day", "Twice a day", "Three times a day"));

    }

// This method is for adding a new medication into the table after the Add button is clicked

    @FXML
    protected void addMedication(ActionEvent event) {
        Medication newmedication = new Medication(mednameField.getText(), dosageField.getText(), frequencyField.getValue(),
                startdateField.getValue(),
                enddateField.getValue(),
                notesField.getText());
        medications.add(newmedication);

        mednameField.clear();
        dosageField.clear();
        frequencyField.setValue(null);
        startdateField.setValue(null);
        enddateField.setValue(null);
        notesField.clear();
    }
/** This method will delete the medication from the table **/
    @FXML
    protected void deleteMedication(ActionEvent event) {

        Medication chosenMed = MedicationTable.getSelectionModel().getSelectedItem();

        if (chosenMed != null) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete this medication?");
            alert.setContentText("Do you want to delete the medication entry for " + chosenMed.getMedicationName() + "?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                MedicationTable.getItems().remove(chosenMed);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Successful Deletion");
                success.setHeaderText(null);
                success.setContentText("Medication entry successfully deleted!");
            } else {
                System.out.println("Deletion cancelled");
            }

        } else {
            Alert select = new Alert(Alert.AlertType.INFORMATION);
            select.setTitle("No Selection");
            select.setHeaderText(null);
            select.setContentText("Please choose an entry to delete");
            select.showAndWait();
        }
    }

    /** Clears the fields **/
    @FXML
    protected void clearEntry (ActionEvent event) {
        mednameField.clear();
        dosageField.clear();
        frequencyField.getItems().clear();
        startdateField.setValue(null);
        enddateField.setValue(null);
        notesField.clear();

    }

    /** This event is called to log the user out of the application and returns the user to the login screen **/
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
    protected void medicationButtonClick() {

        try {

            Stage stage = (Stage) medButton.getScene().getWindow();

            Medication.loadMedTrackerScene(stage);

        } catch (Exception e) {

            throw new RuntimeException(e);
        }
    }

}


