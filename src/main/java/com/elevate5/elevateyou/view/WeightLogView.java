package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class WeightLogView {

    @FXML
    private VBox weightLogVBox;

    @FXML
    private Button weightLogCloseButton;


    @FXML
    private void initialize() {
        if(!SessionManager.getSession().getWeightEntryMap().isEmpty()){
            Map<String, Object> sortedMap = new LinkedHashMap<>(new TreeMap<>(SessionManager.getSession().getWeightEntryMap()));
            for(Map.Entry<String, Object> entry : sortedMap.entrySet()){
                weightLogVBox.getChildren().add(new Label(entry.getKey() + "    " +  entry.getValue() + " lbs"));
            }
        }
    }

    @FXML
    private void weightLogCloseButtonAction(ActionEvent event) {
        weightLogCloseButton.getScene().getWindow().hide();
    }


}
