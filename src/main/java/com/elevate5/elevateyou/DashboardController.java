package com.elevate5.elevateyou;


import com.elevate5.elevateyou.session.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


public class DashboardController {


    @FXML
    private LineChart<String, Number> weightChart;
    @FXML
    private CategoryAxis weightXAxis;
    @FXML
    private NumberAxis weightYAxis;
    @FXML
    private LineChart<String, Number> calorieChart;
    @FXML
    private CategoryAxis calorieXAxis;
    @FXML
    private NumberAxis calorieYAxis;
    @FXML
    private Label latestSleepArticleLabel;

    @FXML
    public void initialize() {

        Map<String, Object> weightLog = new LinkedHashMap<>(new TreeMap<>(SessionManager.getSession().getWeightEntryMap()));

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        long minWeight = 1000;
        long maxWeight = 0;

        for (Map.Entry<String, Object> entry : weightLog.entrySet()) {
            series.getData().add(new XYChart.Data<>(formatDate(entry.getKey()), (Long) entry.getValue()));
            if((Long)entry.getValue() < minWeight){
                minWeight = (Long)entry.getValue();
            }
            if((Long)entry.getValue() > maxWeight){
                maxWeight = (Long)entry.getValue();
            }
        }

        weightYAxis.setAutoRanging(false);
        weightYAxis.setLowerBound(minWeight - 10);
        weightYAxis.setUpperBound(maxWeight + 10);

        weightChart.getData().add(series);
        weightChart.setLegendVisible(false);

        Map<String, Object> calorieMap = new LinkedHashMap<>(new TreeMap<>(SessionManager.getSession().getCalorieDataMap()));

        XYChart.Series<String, Number> calorieSeries = new XYChart.Series<>();

        long minCals = 5000;
        long maxCals = 0;

        for (Map.Entry<String, Object> entry : calorieMap.entrySet()) {
            ObservableList<CaloriesWaterIntakeController.CalorieEntry> entryList = (ObservableList<CaloriesWaterIntakeController.CalorieEntry>) entry.getValue();
            int totalCals = 0;
            for (CaloriesWaterIntakeController.CalorieEntry calorieEntry : entryList) {
                totalCals += calorieEntry.getCalories();
            }
            if(totalCals < minCals){
                minCals = totalCals;
            }
            if(totalCals > maxCals){
                maxCals = totalCals;
            }
            calorieSeries.getData().add(new XYChart.Data<>(formatDate(entry.getKey()), totalCals));
        }

        calorieYAxis.setAutoRanging(false);
        calorieYAxis.setTickUnit(100);
        calorieYAxis.setLowerBound(minCals - 100);
        calorieYAxis.setUpperBound(maxCals + 100);

        calorieChart.getData().add(calorieSeries);
        calorieChart.setLegendVisible(false);

        latestSleepArticleLabel.setText(SessionManager.getSession().getLatestSleepArticle());
        latestSleepArticleLabel.setWrapText(true);

    }

    public String formatDate(String date){
        String[] dateSplit = date.split("-");
        return dateSplit[1] + "/" + dateSplit[2];
    }


}
