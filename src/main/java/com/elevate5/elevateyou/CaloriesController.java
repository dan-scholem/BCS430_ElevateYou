package com.elevate5.elevateyou;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class CaloriesController {

    // ===== KPIs (Calories) =====
    @FXML private Label todayConsumed, dailyGoal, remaining;

    // ===== Calories inputs/table =====
    @FXML private DatePicker foodDate;
    @FXML private TextField foodName, foodCalories, goalField;
    @FXML private TableView<FoodEntry> foodTable;
    @FXML private TableColumn<FoodEntry, LocalDate> foodDateCol;
    @FXML private TableColumn<FoodEntry, String>    foodNameCol;
    @FXML private TableColumn<FoodEntry, Integer>   foodCalCol;

    private final ObservableList<FoodEntry> foods = FXCollections.observableArrayList();
    private int goal = 2000;

    // ===== Water inputs/table =====
    @FXML private DatePicker waterDate;
    @FXML private TextField  waterOunces;
    @FXML private TableView<WaterEntry> waterTable;
    @FXML private TableColumn<WaterEntry, LocalDate> waterDateCol;
    @FXML private TableColumn<WaterEntry, Integer>   waterOzCol;
    @FXML private Label waterTotal; // shows "X oz"

    private final ObservableList<WaterEntry> waters = FXCollections.observableArrayList();

    // ===== Activity inputs/table =====
    @FXML private DatePicker actDate;
    @FXML private ComboBox<String> actType;
    @FXML private TextField actMinutes, actCalories;
    @FXML private TableView<ActivityEntry> actTable;
    @FXML private TableColumn<ActivityEntry, LocalDate> actDateCol;
    @FXML private TableColumn<ActivityEntry, String>     actTypeCol;
    @FXML private TableColumn<ActivityEntry, Integer>    actMinCol;
    @FXML private TableColumn<ActivityEntry, Integer>    actCalCol;

    private final ObservableList<ActivityEntry> activities = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // ----- calories defaults -----
        if (foodDate != null) foodDate.setValue(LocalDate.now());
        if (foodTable != null) {
            foodDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            foodNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            foodCalCol.setCellValueFactory(new PropertyValueFactory<>("calories"));
            foodTable.setItems(foods);
        }
        dailyGoal.setText(String.valueOf(goal));

        // ----- water defaults -----
        if (waterDate != null) waterDate.setValue(LocalDate.now());
        if (waterTable != null) {
            waterDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            waterOzCol.setCellValueFactory(new PropertyValueFactory<>("ounces"));
            waterTable.setItems(waters);
        }
        recalcWater(); // initialize water total label if present

        // ----- activity defaults -----
        if (actDate != null) actDate.setValue(LocalDate.now());
        if (actType != null) actType.getItems().addAll("Walk", "Run", "Cycling", "Gym", "Swim", "Yoga");
        if (actTable != null) {
            actDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            actTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            actMinCol.setCellValueFactory(new PropertyValueFactory<>("minutes"));
            actCalCol.setCellValueFactory(new PropertyValueFactory<>("calories"));
            actTable.setItems(activities);
        }

        recalcKpis();
    }

    // ===== actions (Calories) =====
    @FXML
    private void addFood() {
        try {
            LocalDate d = foodDate.getValue();
            String n = foodName.getText();
            int c = Integer.parseInt(foodCalories.getText().trim());
            if (d != null && n != null && !n.isBlank() && c >= 0) {
                foods.add(new FoodEntry(d, n, c));
                foodName.clear(); foodCalories.clear();
                recalcKpis();
            }
        } catch (NumberFormatException e) {
            alert("Enter a valid number for calories.");
        }
    }

    @FXML
    private void setGoal() {
        try {
            goal = Integer.parseInt(goalField.getText().trim());
            goalField.clear();
            dailyGoal.setText(String.valueOf(goal));
            recalcKpis();
        } catch (NumberFormatException e) {
            alert("Enter a valid number for goal.");
        }
    }

    // ===== actions (Water) =====
    @FXML
    private void addWater() {
        try {
            LocalDate d = waterDate.getValue();
            int oz = Integer.parseInt(waterOunces.getText().trim());
            if (d != null && oz > 0) {
                waters.add(new WaterEntry(d, oz));
                waterOunces.clear();
                recalcWater();
            }
        } catch (NumberFormatException e) {
            alert("Enter a valid number for ounces.");
        }
    }

    private void recalcWater() {
        if (waterTotal == null) return; // label not on current tab
        int totalToday = waters.stream()
                .filter(w -> w.getDate().equals(LocalDate.now()))
                .mapToInt(WaterEntry::getOunces).sum();
        waterTotal.setText(totalToday + " oz");
    }

    // ===== actions (Activity) =====
    @FXML
    private void addActivity() {
        try {
            LocalDate d = actDate.getValue();
            String t = actType.getValue();
            int m = Integer.parseInt(actMinutes.getText().trim());
            int c = Integer.parseInt(actCalories.getText().trim());
            if (d != null && t != null && !t.isBlank() && m >= 0 && c >= 0) {
                activities.add(new ActivityEntry(d, t, m, c));
                actMinutes.clear(); actCalories.clear();
                recalcKpis();
            }
        } catch (NumberFormatException e) {
            alert("Enter valid numbers for minutes and burned calories.");
        }
    }

    // ===== helpers =====
    private void recalcKpis() {
        int consumedToday = foods.stream()
                .filter(f -> f.getDate().equals(LocalDate.now()))
                .mapToInt(FoodEntry::getCalories).sum();

        int burnedToday = activities.stream()
                .filter(a -> a.getDate().equals(LocalDate.now()))
                .mapToInt(ActivityEntry::getCalories).sum();

        todayConsumed.setText(String.valueOf(consumedToday));
        remaining.setText(String.valueOf(goal - consumedToday + burnedToday));
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    // ===== models =====
    public static class FoodEntry {
        private final LocalDate date;
        private final String name;
        private final int calories;
        public FoodEntry(LocalDate date, String name, int calories) {
            this.date = date; this.name = name; this.calories = calories;
        }
        public LocalDate getDate() { return date; }
        public String getName() { return name; }
        public int getCalories() { return calories; }
    }

    public static class WaterEntry {
        private final LocalDate date;
        private final int ounces;
        public WaterEntry(LocalDate date, int ounces) {
            this.date = date; this.ounces = ounces;
        }
        public LocalDate getDate() { return date; }
        public int getOunces() { return ounces; }
    }

    public static class ActivityEntry {
        private final LocalDate date;
        private final String type;
        private final int minutes;
        private final int calories;
        public ActivityEntry(LocalDate date, String type, int minutes, int calories) {
            this.date = date; this.type = type; this.minutes = minutes; this.calories = calories;
        }
        public LocalDate getDate() { return date; }
        public String getType() { return type; }
        public int getMinutes() { return minutes; }
        public int getCalories() { return calories; }
    }
}
