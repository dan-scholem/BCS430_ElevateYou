package com.elevate5.elevateyou;

import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.CalendarView;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class JournalEntryController implements Initializable {

    @FXML
    private Button addEntryButton;

    @FXML
    private Button appointmentsButton;

    @FXML
    private Button calendarButton;

    @FXML
    private Button chatButton;

    @FXML
    private Button dashButton;

    @FXML
    private Button deleteEntryButton;

    @FXML
    private DatePicker entryDate;

    @FXML
    private ListView<JournalEntry> EntryList;

    @FXML
    private TextArea entryTextArea;

    @FXML
    private TextField entryTitle;

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
    private Button quotesaffirmationBtn;

    @FXML
    private Button reviewsButton;

    @FXML
    private Button sleepButton;

    @FXML
    private Button tutorialsButton;

    @FXML
    private Button updateEntryButton;

    @FXML
    private Button clearEntryButton;

    @FXML
    private ComboBox<String> moodOptions;

    private final ObservableList<JournalEntry> journalentries = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        entryDate.setValue(LocalDate.now());

        EntryList.setItems(journalentries);

        moodOptions.setItems(FXCollections.observableArrayList("😊 Happy", "🙁 Sad", "😰 Nervous", "😌 Calm", "😄 Excited", "🥱 Tired"));

        loadEntries();
    }


    /** Loads the journal entries in the Listview **/
    private void loadEntries() {

        journalentries.clear();

        try {
            CollectionReference medCollectionRef = App.fstore.collection("Journal Entries").document(App.theUser.getEmail()).collection("UserJournals");

            ApiFuture<QuerySnapshot> query = medCollectionRef.get();

            List<QueryDocumentSnapshot> documents = query.get().getDocuments();

            for (QueryDocumentSnapshot document : documents) {

                String docID = document.getId();
                String journaltitle = document.getString("title");
                String entry = document.getString("entry");
                String entryDateStr = document.getString("entryDate");
                String mood = document.getString("mood");

                LocalDate entryDate = null;

                if (entryDateStr != null) {

                    entryDate = LocalDate.parse(entryDateStr);
                }

                    JournalEntry journalentry = new JournalEntry(docID, journaltitle, entryDate, entry, mood);

                    journalentries.add(journalentry);

                }

            }

            catch(ExecutionException | InterruptedException e) {
                System.out.println("Error loading journal entry" + e.getMessage());
                throw new RuntimeException(e);
            }
    }

    /** Adds a new journal entry **/
    @FXML
    protected void addEntry (ActionEvent event) throws ExecutionException, InterruptedException {

        String title = entryTitle.getText();
        String journalText = entryTextArea.getText();
        LocalDate date = entryDate.getValue();
        String mood = moodOptions.getValue();

        if (journalText.isEmpty() || title.isEmpty() || date == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Form");
            alert.setHeaderText(null);
            alert.setContentText("Please fill out all the required fields!");
            alert.showAndWait();

            return;
        }

            /** Adding the journal entry to Firestore, based on the user **/
            Map<String, Object> entries = new HashMap<>();

            entries.put("title", entryTitle.getText());
            entries.put("entryDate", entryDate.getValue().toString());
            entries.put("entry", entryTextArea.getText());
            entries.put("mood", moodOptions.getValue());

            try {

                DocumentReference docRef = App.fstore.collection("Journal Entries").document(App.theUser.getEmail());

                ApiFuture<DocumentReference> result = docRef.collection("UserJournals").add(entries);

                DocumentReference journalDocRef = result.get();

                String journalDocID = journalDocRef.getId();

                JournalEntry newentry = new JournalEntry(journalDocID, title, date, journalText, mood);

                EntryList.getItems().add(newentry);

                entryTitle.clear();
                entryTextArea.clear();
                moodOptions.setValue(null);



                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("New Entry");
                success.setHeaderText(null);
                success.setContentText("Journal entry successfully added!");
                success.showAndWait();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    /** Updates the journal entry **/
    private JournalEntry selectedEntry;

    @FXML
    protected void updateEntry (ActionEvent event) {

            if (this.selectedEntry == null) {
                System.out.println("Error: No selection chosen to update.");
                Alert select = new Alert(Alert.AlertType.WARNING,"Please select a journal entry to update!", ButtonType.OK);
                select.showAndWait();
                return;
            }
            try {
                this.selectedEntry.setTitle(entryTitle.getText());
                this.selectedEntry.setEntryDate(entryDate.getValue());
                this.selectedEntry.setEntryContent(entryTextArea.getText());
                this.selectedEntry.setMood(moodOptions.getValue());

                Map<String, Object> journalupdates = new HashMap<>();

                journalupdates.put("title", entryTitle.getText());
                journalupdates.put("entry", entryTextArea.getText());
                journalupdates.put("entryDate", entryDate.getValue().toString());
                journalupdates.put("mood", moodOptions.getValue());

                DocumentReference docRef = App.fstore.collection("Journal Entries").document(App.theUser.getEmail())
                        .collection("UserJournals").document(this.selectedEntry.getDocumentID());

                ApiFuture<WriteResult> future = docRef.update(journalupdates);

                future.get();

                EntryList.refresh();

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Updated Journal Entry");
                success.setHeaderText(null);
                success.setContentText("Journal entry updated successfully!");
                success.showAndWait();


            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    /** Event for clicking the row being updated **/
    @FXML
    protected void entryrowClicked(MouseEvent event) {

        JournalEntry clickedEntry = EntryList.getSelectionModel().getSelectedItem();

        if (clickedEntry != null) {

            this.selectedEntry = clickedEntry;

            entryTitle.setText(clickedEntry.toString());
            entryTextArea.setText(clickedEntry.getEntryContent());
            entryDate.setValue(clickedEntry.getEntryDate());
            moodOptions.setValue(clickedEntry.getMood());
        }

        else {
            this.selectedEntry = null;
        }
    }

    /** This method will delete the journal entry from the table **/
    @FXML
    protected void deleteEntry (ActionEvent event) {

        JournalEntry chosenEntry = EntryList.getSelectionModel().getSelectedItem();

        if (chosenEntry == null) {

            Alert select = new Alert(Alert.AlertType.INFORMATION);
            select.setTitle("No Selection");
            select.setHeaderText(null);
            select.setContentText("Please choose an entry to delete");
            select.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete this journal entry?");
        alert.setContentText("Do you want to delete the journal entry for " + chosenEntry + "?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {

                DocumentReference docRef = App.fstore.collection("Journal Entries").document(App.theUser.getEmail())
                        .collection("UserJournals").document(chosenEntry.getDocumentID());

                ApiFuture<WriteResult> future = docRef.delete();

                future.get();

                EntryList.getItems().remove(chosenEntry);

                EntryList.refresh();

                chosenEntry.setTitle(entryTitle.getText());
                chosenEntry.setEntryContent(entryTextArea.getText());
                chosenEntry.setEntryDate(entryDate.getValue());
                chosenEntry.setMood(moodOptions.getValue());

                Alert deletealert = new Alert(Alert.AlertType.INFORMATION);
                deletealert.setTitle("Delete Journal Entry");
                deletealert.setHeaderText(null);
                deletealert.setContentText("Journal entry successfully deleted!");
                deletealert.showAndWait();

                entryTitle.clear();
                entryTextArea.clear();

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
        entryTitle.clear();
        entryTextArea.clear();
        moodOptions.setValue(null);
    }

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
    protected void foodButtonClick() {
        try {
            Stage stage = (Stage) foodButton.getScene().getWindow();
            CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
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
