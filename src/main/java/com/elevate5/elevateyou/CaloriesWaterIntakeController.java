package com.elevate5.elevateyou;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CaloriesWaterIntakeController {

    // ===== inputs =====
    @FXML private DatePicker calDate;
    @FXML private TextField foodText;
    @FXML private TextField caloriesText;

    @FXML private DatePicker waterDate;
    @FXML private TextField waterOunces;

    // ===== calories table =====
    @FXML private TableView<CalRow> caloriesTable;
    @FXML private TableColumn<CalRow, String> colCalDate;
    @FXML private TableColumn<CalRow, String> colFood;
    @FXML private TableColumn<CalRow, String> colCals;
    @FXML private Label lblCalTotal;
    @FXML private BarChart<String, Number> caloriesChart;

    // ===== water table =====
    @FXML private TableView<WaterRow> waterTable;
    @FXML private TableColumn<WaterRow, String> colWaterDate;
    @FXML private TableColumn<WaterRow, String> colOunces;
    @FXML private Label lblWaterTotal;

    // ===== advice =====
    @FXML private Label lblAdvice;

    private final ObservableList<CalRow> calRows = FXCollections.observableArrayList();
    private final ObservableList<WaterRow> waterRows = FXCollections.observableArrayList();
    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // simple suggestions (not a dropdown control)
    private final Map<String, Integer> foodCals = new LinkedHashMap<>();

    @FXML
    private void initialize() {
        calDate.setValue(LocalDate.now());
        waterDate.setValue(LocalDate.now());

        // seed foods (you can edit freely)
        foodCals.put("Chicken (4 oz)", 185);
        foodCals.put("Rice (1 cup cooked)", 205);
        foodCals.put("Steak (4 oz)", 275);
        foodCals.put("Peanut Butter (2 tbsp)", 190);
        foodCals.put("Protein Powder (1 scoop)", 120);
        foodCals.put("Shrimp (4 oz)", 120);
        foodCals.put("Salad (2 cups)", 80);
        foodCals.put("Apple (1 medium)", 95);

        // tables
        colCalDate.setCellValueFactory(d -> d.getValue().dateProperty());
        colFood.setCellValueFactory(d -> d.getValue().foodProperty());
        colCals.setCellValueFactory(d -> d.getValue().calProperty());
        caloriesTable.setItems(calRows);

        colWaterDate.setCellValueFactory(d -> d.getValue().dateProperty());
        colOunces.setCellValueFactory(d -> d.getValue().ozProperty());
        waterTable.setItems(waterRows);

        // numeric guards
        caloriesText.setTextFormatter(numericFormatter(false)); // ints only
        waterOunces.setTextFormatter(numericFormatter(false));

        // auto-fill calories if the food matches our simple map
        foodText.textProperty().addListener((obs, old, text) -> {
            if (text == null || text.isBlank()) return;
            foodCals.entrySet().stream()
                    .filter(e -> e.getKey().toLowerCase().startsWith(text.toLowerCase()))
                    .findFirst()
                    .ifPresent(e -> caloriesText.setText(String.valueOf(e.getValue())));
        });

        // recompute totals when the selected date changes
        calDate.valueProperty().addListener((obs, o, n) -> updateCalorieTotal());
        waterDate.valueProperty().addListener((obs, o, n) -> updateWaterTotal());

        refreshChartsAndAdvice();
    }

    // ===== add entries =====
    @FXML
    private void addCalories() {
        var date = calDate.getValue();
        var food = foodText.getText().trim();
        var calsText = caloriesText.getText().trim();

        if (date == null || food.isBlank() || calsText.isBlank()) {
            info("Please fill date, food, and calories.");
            return;
        }
        int cals;
        try {
            cals = Integer.parseInt(calsText);
            if (cals <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            info("Calories must be a positive number.");
            return;
        }

        calRows.add(new CalRow(DF.format(date), food, String.valueOf(cals)));
        foodText.clear();
        caloriesText.clear();

        updateCalorieTotal();
        refreshChartsAndAdvice();
    }

    @FXML
    private void addWater() {
        var date = waterDate.getValue();
        var ozText = waterOunces.getText().trim();

        if (date == null || ozText.isBlank()) {
            info("Please fill date and ounces.");
            return;
        }
        int oz;
        try {
            oz = Integer.parseInt(ozText);
            if (oz <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            info("Ounces must be a positive integer.");
            return;
        }

        waterRows.add(new WaterRow(DF.format(date), String.valueOf(oz)));
        waterOunces.clear();

        updateWaterTotal();
        refreshChartsAndAdvice();
    }

    // ===== totals =====
    private void updateCalorieTotal() {
        String d = DF.format(calDate.getValue());
        int sum = calRows.stream()
                .filter(r -> r.dateProperty().get().equals(d))
                .mapToInt(r -> Integer.parseInt(r.calProperty().get()))
                .sum();
        lblCalTotal.setText(String.valueOf(sum));
    }

    private void updateWaterTotal() {
        String d = DF.format(waterDate.getValue());
        int sum = waterRows.stream()
                .filter(r -> r.dateProperty().get().equals(d))
                .mapToInt(r -> Integer.parseInt(r.ozProperty().get()))
                .sum();
        lblWaterTotal.setText(String.valueOf(sum));
    }

    // ===== chart + advice =====
    private void refreshChartsAndAdvice() {
        // aggregate calories by date
        Map<String, Integer> perDay = new TreeMap<>();
        for (CalRow r : calRows) {
            perDay.merge(r.dateProperty().get(), Integer.parseInt(r.calProperty().get()), Integer::sum);
        }

        caloriesChart.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Daily Calories");
        perDay.forEach((date, total) -> s.getData().add(new XYChart.Data<>(date, total)));
        caloriesChart.getData().add(s);

        // very simple advice
        int todayCals = perDay.getOrDefault(DF.format(LocalDate.now()), 0);
        int todayWater = waterRows.stream()
                .filter(w -> w.dateProperty().get().equals(DF.format(LocalDate.now())))
                .mapToInt(w -> Integer.parseInt(w.ozProperty().get()))
                .sum();

        StringBuilder tip = new StringBuilder();
        int CAL_GOAL = 2000;
        int WATER_GOAL = 64;

        if (todayCals < CAL_GOAL - 200) tip.append("You're below your calorie goal; add a protein snack.\n");
        else if (todayCals > CAL_GOAL + 200) tip.append("You're above your calorie goal; consider a lighter dinner.\n");
        else tip.append("Nice! You're close to your calorie goal.\n");

        if (todayWater < WATER_GOAL) tip.append("Drink more water (goal ≈ ").append(WATER_GOAL).append(" oz).");
        else tip.append("Great hydration today!");

        lblAdvice.setText(tip.toString());
    }

    // ===== helpers =====
    private TextFormatter<String> numericFormatter(boolean allowDecimal) {
        return new TextFormatter<>(chg -> {
            if (chg.isDeleted()) return chg;
            String s = chg.getControlNewText();
            return s.matches(allowDecimal ? "\\d*(\\.\\d*)?" : "\\d*") ? chg : null;
        });
    }
    private void info(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    // ===== tiny row classes (kept inside controller so you don't need extra files) =====
    public static class CalRow {
        private final SimpleStringProperty date = new SimpleStringProperty();
        private final SimpleStringProperty food = new SimpleStringProperty();
        private final SimpleStringProperty cal  = new SimpleStringProperty();
        public CalRow(String date, String food, String cal) {
            this.date.set(date); this.food.set(food); this.cal.set(cal);
        }
        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty foodProperty() { return food; }
        public SimpleStringProperty calProperty()  { return cal;  }
    }
    public static class WaterRow {
        private final SimpleStringProperty date = new SimpleStringProperty();
        private final SimpleStringProperty oz   = new SimpleStringProperty();
        public WaterRow(String date, String oz) {
            this.date.set(date); this.oz.set(oz);
        }
        public SimpleStringProperty dateProperty() { return date; }
        public SimpleStringProperty ozProperty()   { return oz;   }
    }
}
