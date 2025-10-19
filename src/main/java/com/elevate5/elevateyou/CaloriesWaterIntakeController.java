package com.elevate5.elevateyou;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

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

    private final ObservableList<CalorieEntry> calorieData = FXCollections.observableArrayList();
    private final ObservableList<WaterEntry> waterData   = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        // Defaults
        calDate.setValue(LocalDate.now());
        waterDate.setValue(LocalDate.now());

        // Calories table
        colCalDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colFood.setCellValueFactory(new PropertyValueFactory<>("food"));
        colCals.setCellValueFactory(new PropertyValueFactory<>("calories"));
        caloriesTable.setItems(calorieData);

        // Water table
        colWaterDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colOunces.setCellValueFactory(new PropertyValueFactory<>("ounces"));
        waterTable.setItems(waterData);

        // Totals update when selection changes (or you can compute on whole table)
        caloriesTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateCalorieTotal());
        waterTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> updateWaterTotal());

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

        calorieData.add(new CalorieEntry(d, food, cals));
        clearCaloriesInputs();
        updateCalorieTotal();
    }

    @FXML
    private void deleteSelectedCalorie() {
        CalorieEntry sel = caloriesTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a calorie row to delete."); return; }
        calorieData.remove(sel);
        updateCalorieTotal();
    }

    @FXML
    private void clearCaloriesInputs() {
        foodText.clear();
        caloriesText.clear();
        calDate.setValue(LocalDate.now());
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

        waterData.add(new WaterEntry(d, oz));
        clearWaterInputs();
        updateWaterTotal();
    }

    @FXML
    private void deleteSelectedWater() {
        WaterEntry sel = waterTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a water row to delete."); return; }
        waterData.remove(sel);
        updateWaterTotal();
    }

    @FXML
    private void clearWaterInputs() {
        waterOunces.clear();
        waterDate.setValue(LocalDate.now());
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
    private void calendarButtonClick() {
        // no-op to avoid CalendarView dependency; keeps the sidebar identical without breaking anything
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
        private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
        private final StringProperty food = new SimpleStringProperty();
        private final IntegerProperty calories = new SimpleIntegerProperty();

        public CalorieEntry(LocalDate date, String food, int calories) {
            this.date.set(date);
            this.food.set(food);
            this.calories.set(calories);
        }

        public LocalDate getDate() { return date.get(); }
        public ObjectProperty<LocalDate> dateProperty() { return date; }

        public String getFood() { return food.get(); }
        public StringProperty foodProperty() { return food; }

        public int getCalories() { return calories.get(); }
        public IntegerProperty caloriesProperty() { return calories; }
    }

    public static class WaterEntry {
        private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
        private final IntegerProperty ounces = new SimpleIntegerProperty();

        public WaterEntry(LocalDate date, int ounces) {
            this.date.set(date);
            this.ounces.set(ounces);
        }

        public LocalDate getDate() { return date.get(); }
        public ObjectProperty<LocalDate> dateProperty() { return date; }

        public int getOunces() { return ounces.get(); }
        public IntegerProperty ouncesProperty() { return ounces; }
    }
}
