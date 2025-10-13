package com.elevate5.elevateyou;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;

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
    private void initialize() throws ExecutionException, InterruptedException {
        medNameColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("medicationName"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("dosage"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("frequency"));
        startdateColumn.setCellValueFactory(new PropertyValueFactory<Medication, LocalDate>("startDate"));
        enddateColumn.setCellValueFactory(new PropertyValueFactory<Medication, LocalDate>("endDate"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<Medication, String>("notes"));

        MedicationTable.setItems(medications);

        frequencyField.setItems(FXCollections.observableArrayList("As needed", "Once a day", "Twice a day", "Three times a day"));

        loadMedications();

    }

     private void loadMedications() {

        medications.clear();

        try {
            CollectionReference medCollectionRef = App.fstore.collection("Medications").document(App.theUser.getEmail()).collection("UserMedications");

            ApiFuture<QuerySnapshot> query = medCollectionRef.get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {
                String medname = document.getString("medicationName");
                String dosage = document.getString("dosage");
                String frequency = document.getString("frequency");
                String notes = document.getString("notes");

                String startDateStr = document.getString("startDate");
                String endDateStr = document.getString("endDate");

                LocalDate startDate = null;
                LocalDate endDate = null;

                if (startDateStr != null) {
                    startDate = LocalDate.parse(startDateStr);
                }
                if (endDateStr != null) {
                    endDate = LocalDate.parse(endDateStr);
                }

                Medication newmedication = new Medication(medname, dosage, frequency, startDate, endDate, notes);

                medications.add(newmedication);

            }

        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Error loading medication" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


// This method is for adding a new medication into the table after the Add button is clicked

    @FXML
    protected void addMedication(ActionEvent event) throws ExecutionException, InterruptedException {

        String medname = mednameField.getText();
        String dosage = dosageField.getText();
        String frequency = frequencyField.getValue();
        LocalDate startdate = startdateField.getValue();
        LocalDate enddate = enddateField.getValue();
        String notes = notesField.getText();

        if (medname.isEmpty() || dosage.isEmpty() || frequency == null || startdate == null || enddate == null || notes.isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Form");
            alert.setHeaderText(null);
            alert.setContentText("Please fill out all the required fields!");
            alert.showAndWait();
            return;
        }

        /** Adding the medication to Firestore, based on the user **/

        Map<String, Object> meds = new HashMap<>();

        meds.put("medicationName", mednameField.getText());
        meds.put("dosage", dosageField.getText());
        meds.put("frequency", frequencyField.getValue());
        meds.put("startDate", startdateField.getValue().toString());
        meds.put("endDate", enddateField.getValue().toString());
        meds.put("notes", notesField.getText());

        DocumentReference docRef = App.fstore.collection("Medications").document(App.theUser.getEmail());

        ApiFuture<DocumentReference> result = docRef.collection("UserMedications").add(meds);

        Medication newmedication = new Medication(medname, dosage, frequency, startdate, enddate, notes);

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
                success.showAndWait();
            }

            else {
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
        frequencyField.setValue(null);
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


