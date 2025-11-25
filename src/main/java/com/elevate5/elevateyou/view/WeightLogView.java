package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.session.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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
                HBox hBox = new HBox();
                hBox.setSpacing(20);
                Label dateLabel = new Label(entry.getKey() + ":");
                dateLabel.setPadding(new Insets(5,0,0,10));
                TextField weightField = new TextField();
                weightField.setText(entry.getValue().toString());
                weightField.setPrefWidth(40);
                weightField.setDisable(true);
                weightField.setStyle("-fx-opacity: 1.0; -fx-border-color: transparent; -fx-background-color: transparent;-fx-border-width: 0;");
                StackPane saveEditPane = new StackPane();
                Button saveButton = new Button("Save");
                Button editButton = new Button("Edit");
                saveEditPane.getChildren().addAll(editButton, saveButton);
                StackPane.setAlignment(saveButton, Pos.CENTER);
                StackPane.setAlignment(editButton, Pos.CENTER);
                saveButton.setVisible(false);
                saveButton.setDisable(true);
                editButton.setOnAction(event -> {
                    editButton.setDisable(true);
                    editButton.setVisible(false);
                    saveButton.setDisable(false);
                    saveButton.setVisible(true);
                    weightField.setDisable(false);
                    weightField.setStyle("-fx-opacity: 1.0; -fx-border-color: black; -fx-background-color: white;-fx-border-width: 0.5;");
                    weightField.requestFocus();
                });
                saveButton.setOnAction(event -> {
                    saveButton.setDisable(true);
                    saveButton.setVisible(false);
                    editButton.setDisable(false);
                    editButton.setVisible(true);
                    editButton.requestFocus();
                    weightField.setDisable(true);
                    weightField.setStyle("-fx-opacity: 1.0; -fx-border-color: transparent; -fx-background-color: transparent;-fx-border-width: 0;");
                    SessionManager.getSession().getWeightEntryMap().put(entry.getKey(), Integer.parseInt(weightField.getText()));
                });
                hBox.getChildren().addAll(dateLabel, weightField,  saveEditPane);
                weightLogVBox.getChildren().add(hBox);
            }
        }
    }

    @FXML
    private void weightLogCloseButtonAction(ActionEvent event) {
        weightLogCloseButton.getScene().getWindow().hide();
    }


}
