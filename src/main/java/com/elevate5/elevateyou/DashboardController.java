package com.elevate5.elevateyou;


import com.elevate5.elevateyou.session.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


public class DashboardController {


    private Map<String, Object> weightLog;

    @FXML
    private LineChart<String, Number> weightChart;
    @FXML
    private CategoryAxis weightXAxis;
    @FXML
    private NumberAxis weightYAxis;

    @FXML
    public void initialize() {

        weightLog = new LinkedHashMap<>(new TreeMap<>(SessionManager.getSession().getWeightEntryMap()));


        weightXAxis.setLabel("Date");
        weightYAxis.setLabel("Weight (lbs)");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        //series.setName("Weight");

        long minWeight = 1000;
        long maxWeight = 0;

        for (Map.Entry<String, Object> entry : weightLog.entrySet()) {
            System.out.printf(entry.getKey() + ": " + entry.getValue() + "\n");
            series.getData().add(new XYChart.Data<>(entry.getKey(), (Long) entry.getValue()));
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


    }


}
