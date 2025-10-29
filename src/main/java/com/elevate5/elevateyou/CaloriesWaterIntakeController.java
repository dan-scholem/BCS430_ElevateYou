package com.elevate5.elevateyou;

import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.view.CalendarView;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiService;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.WriteResult;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.checkerframework.checker.units.qual.C;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class CaloriesWaterIntakeController {

    // ===== Inputs =====
    @FXML private DatePicker calDate;
    @FXML private TextField foodText;
    @FXML private TextField caloriesText;

    @FXML private DatePicker waterDate;
    @FXML private TextField waterOunces;

    // ===== Tables + columns =====
    @FXML private TableView<CalorieEntry> caloriesTable;
    @FXML private TableColumn<CalorieEntry, LocalDate> colCalDate;
    @FXML private TableColumn<CalorieEntry, String> colFood;
    @FXML private TableColumn<CalorieEntry, Integer> colCals;

    @FXML private TableView<WaterEntry> waterTable;
    @FXML private TableColumn<WaterEntry, LocalDate> colWaterDate;
    @FXML private TableColumn<WaterEntry, Integer> colOunces;

    // ===== UI bits =====
    @FXML private Label lblCalTotal;
    @FXML private Label lblWaterTotal;

    // ===== Sidebar buttons (only needed for onAction handlers that exist here) =====
    @FXML private Button dashButton;
    @FXML private Button medButton;
    @FXML private Button foodButton;
    @FXML private Button calendarButton;
    @FXML private Button logoutButton;
    @FXML private Button journalButton;

    private ObservableList<CalorieEntry> calorieData = FXCollections.observableArrayList();
    private ObservableList<WaterEntry> waterData   = FXCollections.observableArrayList();
    Map<String, ObservableList<CalorieEntry>> calorieDataMap = new HashMap<>();
    Map<String, ObservableList<WaterEntry>> waterDataMap = new HashMap<>();

    @FXML
    private void initialize() {
        // Defaults
        calDate.setValue(LocalDate.now());
        waterDate.setValue(LocalDate.now());

        //Initializes calorieDataMap with Firestore Data
        try{
            DocumentReference docRef = App.fstore.collection("Calories").document(SessionManager.getSession().getUserID());
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot doc = future.get();
            if(doc.exists()) {
                for(String key : Objects.requireNonNull(doc.getData()).keySet()) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) doc.getData().get(key);
                    ObservableList<CalorieEntry> loadedCaloriesData = FXCollections.observableArrayList();
                    for (int i = 0; i < data.size(); i++) {
                        String date = data.get(i).get("date").toString();
                        int calories = Integer.parseInt(data.get(i).get("calories").toString());
                        String food = data.get(i).get("food").toString();
                        CalorieEntry loadedEntry = new CalorieEntry(date, food, calories);
                        loadedCaloriesData.add(loadedEntry);
                        calorieDataMap.put(date, loadedCaloriesData);
                    }
                }
                calorieData = calorieDataMap.get(LocalDate.now().toString());
            }
            docRef = App.fstore.collection("Water").document(SessionManager.getSession().getUserID());
            future = docRef.get();
            doc = future.get();
            if(doc.exists()) {
                for(String key : Objects.requireNonNull(doc.getData()).keySet()) {
                    List<Map<String, Object>> data = (List<Map<String, Object>>) doc.getData().get(key);
                    ObservableList<WaterEntry> loadedWaterData = FXCollections.observableArrayList();
                    for (int i = 0; i < data.size(); i++) {
                        String date = data.get(i).get("date").toString();
                        int ounces = Integer.parseInt(data.get(i).get("ounces").toString());
                        WaterEntry loadedWaterEntry = new WaterEntry(date, ounces);
                        loadedWaterData.add(loadedWaterEntry);
                        waterDataMap.put(date, loadedWaterData);
                    }
                }
                waterData = waterDataMap.get(LocalDate.now().toString());
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Calories table
        colCalDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colFood.setCellValueFactory(new PropertyValueFactory<>("food"));
        colCals.setCellValueFactory(new PropertyValueFactory<>("calories"));
        caloriesTable.setItems(calorieData);
        if(calorieData == null || calorieData.isEmpty()) {
            caloriesTable.setPlaceholder(new Label("No Food Entries on " + calDate.getValue().toString() + " Yet!"));
        }

        // Water table
        colWaterDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOunces.setCellValueFactory(new PropertyValueFactory<>("ounces"));
        waterTable.setItems(waterData);
        if(waterData == null || waterData.isEmpty()) {
            waterTable.setPlaceholder(new Label("No Water Entries on " + waterDate.getValue().toString() + " Yet!"));
        }

        // Totals update when selection changes (or you can compute on whole table)
        caloriesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateCalorieTotal());
        waterTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateWaterTotal());

        //Listens for changed dates and updates table
        calDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            if(oldDate != null && caloriesTable.getItems() != null) {
                calorieDataMap.put(oldDate.toString(), FXCollections.observableArrayList(caloriesTable.getItems()));
            }
            if(calorieDataMap.containsKey(newDate.toString())) {
                calorieData = calorieDataMap.get(newDate.toString());
                if(!calorieData.isEmpty()) {
                    caloriesTable.setItems(calorieData);
                }else{
                    caloriesTable.getItems().clear();
                    caloriesTable.setPlaceholder(new Label("No Food Entries on " + newDate + " Yet!"));
                }
            }else{
                if(caloriesTable.getItems() == null){
                    caloriesTable.setPlaceholder(new Label("No Food Entries on " + newDate + " Yet!"));
                }else{
                    caloriesTable.getItems().clear();
                    caloriesTable.setPlaceholder(new Label("No Food Entries on " + newDate + " Yet!"));
                }
            }
            caloriesTable.refresh();
        });

        waterDate.valueProperty().addListener((obs, oldDate, newDate) -> {
            if(oldDate != null && waterTable.getItems() != null) {
                waterDataMap.put(oldDate.toString(), FXCollections.observableArrayList(waterTable.getItems()));
            }
           if(waterDataMap.containsKey(newDate.toString())) {
               waterData = waterDataMap.get(newDate.toString());
               if(waterData != null){
                   waterTable.setItems(waterData);
               }else{
                   waterTable.getItems().clear();
                   waterTable.setPlaceholder(new Label("No Water Entries on " + newDate + " Yet!"));
               }
           }else{
               if(waterTable.getItems() == null){
                   waterTable.setPlaceholder(new Label("No Water Entries on " + newDate + " Yet!"));
               }else{
                   waterTable.getItems().clear();
                   waterTable.setPlaceholder(new Label("No Water Entries on " + newDate + " Yet!"));
               }
           }
           waterTable.refresh();
        });

        updateCalorieTotal();
        updateWaterTotal();
    }

    // ===== Calories handlers =====
    @FXML
    private void addCalories() {
        LocalDate d = calDate.getValue();
        String food = foodText.getText().trim();
        String calsStr = caloriesText.getText().trim();

        if (d == null || food.isEmpty() || calsStr.isEmpty()) {
            alert("Please enter date, food and calories.");
            return;
        }
        int cals;
        try { cals = Integer.parseInt(calsStr); }
        catch (NumberFormatException ex) { alert("Calories must be a number."); return; }

        if(!calorieDataMap.containsKey(d.toString())) {
            calorieData = FXCollections.observableArrayList();
        }else{
            calorieData = calorieDataMap.get(d.toString());
        }
        calorieData.add(new CalorieEntry(d.toString(), food, cals));
        calorieDataMap.put(d.toString(), calorieData);
        DocumentReference calorieDocRef = App.fstore.collection("Calories").document(SessionManager.getSession().getUserID());
        ApiFuture<WriteResult> result = calorieDocRef.set(calorieDataMap);
        caloriesTable.setItems(calorieData);
        caloriesTable.refresh();
        clearCaloriesInputs();
        updateCalorieTotal();
    }

    @FXML
    private void deleteSelectedCalorie() {
        CalorieEntry sel = caloriesTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a calorie row to delete."); return; }
        calorieData.remove(sel);
        calorieDataMap.put(sel.getDate(), calorieData);
        try{
            DocumentReference docRef = App.fstore.collection("Calories").document(SessionManager.getSession().getUserID());
            ApiFuture<WriteResult> result = docRef.set(calorieDataMap);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        updateCalorieTotal();
    }

    @FXML
    private void clearCaloriesInputs() {
        foodText.clear();
        caloriesText.clear();
    }

    private void updateCalorieTotal() {
        CalorieEntry sel = caloriesTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            // total for the selected row's date
            int total = calorieData.stream()
                    .filter(e -> e.getDate().equals(sel.getDate()))
                    .mapToInt(CalorieEntry::getCalories)
                    .sum();
            lblCalTotal.setText(Integer.toString(total));
        } else {
            lblCalTotal.setText("0");
        }
    }

    // ===== Water handlers =====
    @FXML
    private void addWater() {
        LocalDate d = waterDate.getValue();
        String ozStr = waterOunces.getText().trim();
        if (d == null || ozStr.isEmpty()) { alert("Please enter date and ounces."); return; }
        int oz;
        try { oz = Integer.parseInt(ozStr); }
        catch (NumberFormatException ex) { alert("Ounces must be a number."); return; }

        if(!waterDataMap.containsKey(d.toString())) {
            waterData = FXCollections.observableArrayList();
        }else{
            waterData = waterDataMap.get(d.toString());
        }
        waterData.add(new WaterEntry(d.toString(), oz));
        waterDataMap.put(d.toString(), waterData);

        DocumentReference waterDocRef = App.fstore.collection("Water").document(SessionManager.getSession().getUserID());
        ApiFuture<WriteResult> result = waterDocRef.set(waterDataMap);
        waterData = waterDataMap.get(d.toString());
        waterTable.setItems(waterData);
        waterTable.refresh();
        clearWaterInputs();
        updateWaterTotal();
    }

    @FXML
    private void deleteSelectedWater() {
        WaterEntry sel = waterTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a water row to delete."); return; }
        waterData.remove(sel);
        waterDataMap.put(sel.getDate(), waterData);
        try{
            DocumentReference docRef = App.fstore.collection("Water").document(SessionManager.getSession().getUserID());
            ApiFuture<WriteResult> result = docRef.set(waterDataMap);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        updateWaterTotal();
    }

    @FXML
    private void clearWaterInputs() {
        waterOunces.clear();
    }

    private void updateWaterTotal() {
        WaterEntry sel = waterTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            int total = waterData.stream()
                    .filter(e -> e.getDate().equals(sel.getDate()))
                    .mapToInt(WaterEntry::getOunces)
                    .sum();
            lblWaterTotal.setText(Integer.toString(total));
        } else {
            lblWaterTotal.setText("0");
        }
    }

    // ===== Sidebar navigation =====
    @FXML
    private void dashboardButtonClick() {
        try {
            Stage stage = (Stage) dashButton.getScene().getWindow();
            Dashboard.loadDashboardScene(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void medicationButtonClick() {
        try {
            Stage stage = (Stage) medButton.getScene().getWindow();
            Medication.loadMedTrackerScene(stage);
        } catch (Exception e) {
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
    private void calendarButtonClick() {
        // no-op to avoid CalendarView dependency; keeps the sidebar identical without breaking anything
        try {
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            CalendarView.loadCalendarScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void logoutUser() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            UserLogin.loadUserLoginScene(stage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    // ===== simple models (table rows) =====
    public static class CalorieEntry {
        private final StringProperty date = new SimpleStringProperty();
        private final StringProperty food = new SimpleStringProperty();
        private final IntegerProperty calories = new SimpleIntegerProperty();

        public CalorieEntry(String date, String food, int calories) {
            this.date.set(date);
            this.food.set(food);
            this.calories.set(calories);
        }

        public String getDate() { return date.get(); }
        public StringProperty dateProperty() { return date; }

        public String getFood() { return food.get(); }
        public StringProperty foodProperty() { return food; }

        public int getCalories() { return calories.get(); }
        public IntegerProperty caloriesProperty() { return calories; }
    }

    public static class WaterEntry {
        private final StringProperty date = new SimpleStringProperty();
        private final IntegerProperty ounces = new SimpleIntegerProperty();

        public WaterEntry(String date, int ounces) {
            this.date.set(date);
            this.ounces.set(ounces);
        }

        public String getDate() { return date.get(); }
        public StringProperty dateProperty() { return date; }

        public int getOunces() { return ounces.get(); }
        public IntegerProperty ouncesProperty() { return ounces; }
    }
}
