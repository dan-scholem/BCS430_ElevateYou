package com.elevate5.elevateyou;

import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.AppointmentView;
import com.elevate5.elevateyou.view.CalendarView;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
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
    private Button calendarButton;

    @FXML
    private Button journalButton;

    @FXML
    private Button foodButton;

    @FXML
    private Button appointmentsButton;

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
    private Button quotesaffirmationBtn;

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

    private ObservableList<Medication> medications = FXCollections.observableArrayList();

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

        medications.addAll(SessionManager.getSession().getMedications());

    }


        /**
     * This method is for adding a new medication into the table after the Add button is clicked
     **/

    @FXML
    protected void addMedication(ActionEvent event) throws ExecutionException, InterruptedException {

        String medname = mednameField.getText();
        String dosage = dosageField.getText();
        String frequency = frequencyField.getValue();
        LocalDate startdate = startdateField.getValue();
        LocalDate enddate = enddateField.getValue();
        String notes = notesField.getText();

        if (medname.isEmpty() || dosage.isEmpty() || frequency == null || startdate == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Form");
            alert.setHeaderText(null);
            alert.setContentText("Please fill out all the required fields!");
            alert.showAndWait();
            return;
        }

        if (enddateField.getValue().isBefore(startdateField.getValue())) {
            Alert datealert = new Alert(Alert.AlertType.ERROR, "Please choose a later date!", ButtonType.OK);
            datealert.showAndWait();
            return;
        }

        /** Adding the medication to Firestore, based on the user **/

        Map<String, Object> meds = new HashMap<>();

        meds.put("medicationName", mednameField.getText());
        meds.put("dosage", dosageField.getText());
        meds.put("frequency", frequencyField.getValue());
        meds.put("startDate", startdateField.getValue().toString());

        if (enddate != null) {
            meds.put("endDate", enddateField.getValue().toString());
        }

        else {
            meds.put("endDate", null);
        }

        if (notes != null && notes.trim().isEmpty()) {
            meds.put("notes", notesField.getText());
        }

        else {
            meds.put("notes", "");
        }

        DocumentReference docRef = App.fstore.collection("Medications").document(App.theUser.getEmail());

        ApiFuture<DocumentReference> result = docRef.collection("UserMedications").add(meds);

        DocumentReference medDocRef = result.get();

        String medDocID = medDocRef.getId();

        Medication newmedication = new Medication(medDocID, medname, dosage, frequency, startdate, enddate, notes);

        medications.add(newmedication);
        SessionManager.getSession().setMedications(medications);

        mednameField.clear();
        dosageField.clear();
        frequencyField.setValue(null);
        startdateField.setValue(null);
        enddateField.setValue(null);
        notesField.clear();

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setTitle("New Medication");
        success.setHeaderText(null);
        success.setContentText("Medication entry successfully added!");
        success.showAndWait();
    }

    /** Updates the medication entry **/
    private Medication selectedMed;
    @FXML
    protected void updateMedication(ActionEvent event) {

            if (this.selectedMed == null) {
                System.out.println("Error: No selection chosen to update.");
                Alert select = new Alert(Alert.AlertType.WARNING,"Please select an entry to update!", ButtonType.OK);
                select.showAndWait();
                return;
            }

            if (enddateField.getValue().isBefore(startdateField.getValue())) {
                Alert datealert = new Alert(Alert.AlertType.ERROR, "Please choose a later date!", ButtonType.OK);
                datealert.showAndWait();
                return;
            }

            try {
                this.selectedMed.setMedicationName(mednameField.getText());
                this.selectedMed.setDosage(dosageField.getText());
                this.selectedMed.setFrequency(frequencyField.getValue());
                this.selectedMed.setStartDate(startdateField.getValue());
                this.selectedMed.setEndDate(enddateField.getValue());
                this.selectedMed.setNotes(notesField.getText());

                Map<String, Object> medupdates = new HashMap<>();

                medupdates.put("medicationName", mednameField.getText());
                medupdates.put("dosage", dosageField.getText());
                medupdates.put("frequency", frequencyField.getValue());
                medupdates.put("startDate", startdateField.getValue().toString());
                medupdates.put("endDate", enddateField.getValue().toString());
                medupdates.put("notes", notesField.getText());

                DocumentReference docRef = App.fstore.collection("Medications").document(App.theUser.getEmail())
                        .collection("UserMedications").document(this.selectedMed.getDocumentID());

                ApiFuture<WriteResult> future = docRef.update(medupdates);

                future.get();

                MedicationTable.refresh();
                SessionManager.getSession().setMedications(medications);

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Updated Medication");
                success.setHeaderText(null);
                success.setContentText("Medication entry updated successfully!");
                success.showAndWait();


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    /** Event for clicking the row being updated **/

    @FXML
    protected void medrowClicked(MouseEvent event) {

        Medication clickedMed = MedicationTable.getSelectionModel().getSelectedItem();

        if (clickedMed != null) {

            this.selectedMed = clickedMed;

            mednameField.setText(clickedMed.getMedicationName());
            dosageField.setText(clickedMed.getDosage());
            frequencyField.setValue(clickedMed.getFrequency());
            startdateField.setValue(clickedMed.getStartDate());
            enddateField.setValue(clickedMed.getEndDate());
            notesField.setText(clickedMed.getNotes());
        }

        else {
            this.selectedMed = null;
        }
    }

    /** This method will delete the medication from the table **/
    @FXML
    protected void deleteMedication(ActionEvent event) {

        Medication chosenMed = MedicationTable.getSelectionModel().getSelectedItem();

        if (chosenMed == null) {

            Alert select = new Alert(Alert.AlertType.INFORMATION);
            select.setTitle("No Selection");
            select.setHeaderText(null);
            select.setContentText("Please choose an entry to delete");
            select.showAndWait();
            return;
        }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Delete this medication?");
            alert.setContentText("Do you want to delete the medication entry for " + chosenMed.getMedicationName() + "?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {

                try {

                    DocumentReference docRef = App.fstore.collection("Medications").document(App.theUser.getEmail())
                            .collection("UserMedications").document(chosenMed.getDocumentID());

                    ApiFuture<WriteResult> future = docRef.delete();

                    future.get();

                    medications.remove(chosenMed);
                    MedicationTable.setItems(medications);
                    SessionManager.getSession().setMedications(medications);
                    MedicationTable.refresh();

                    chosenMed.setMedicationName(mednameField.getText());
                    chosenMed.setDosage(dosageField.getText());
                    chosenMed.setFrequency(frequencyField.getValue());
                    chosenMed.setStartDate(startdateField.getValue());
                    chosenMed.setEndDate(enddateField.getValue());
                    chosenMed.setNotes(notesField.getText());

                    Alert deletealert = new Alert(Alert.AlertType.INFORMATION);
                    deletealert.setTitle("Delete Medication");
                    deletealert.setHeaderText(null);
                    deletealert.setContentText("Medication entry successfully deleted!");
                    deletealert.showAndWait();

                    mednameField.clear();
                    dosageField.clear();
                    frequencyField.setValue(null);
                    startdateField.setValue(null);
                    enddateField.setValue(null);
                    notesField.clear();


                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }

            else {
                System.out.println("Deletion cancelled");
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
    protected void calendarButtonClick() throws IOException {

        try {
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            CalendarView.loadCalendarScene(stage);
        } catch (IOException e){
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
    public void appointmentButtonClick() throws IOException {
        try {
            Stage stage = (Stage) appointmentsButton.getScene().getWindow();
            AppointmentView.loadAppointmentScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void quoteaffirmationButtonClick() throws IOException {

        try {
            Stage stage = (Stage) quotesaffirmationBtn.getScene().getWindow();

            QuotesAffirmations.loadQuotesAffirmationsScene(stage);
        }

        catch (IOException e) {

            throw new RuntimeException(e);
        }

    }

}


