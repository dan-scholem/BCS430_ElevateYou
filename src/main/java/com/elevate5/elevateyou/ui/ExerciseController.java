package com.elevate5.elevateyou.ui;

import com.elevate5.elevateyou.Dashboard;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ExerciseController {

    @FXML private GridPane exerciseGrid;
    @FXML private ToggleButton treadmillBtn, weightsBtn, bicycleBtn, swimmingBtn;
    @FXML private Label selectedExerciseLabel, timeLabel, caloriesLabel;
    @FXML private Button startButton, stopButton, resetButton, dashboardButton;

    private final ToggleGroup choiceGroup = new ToggleGroup();
    private String currentExercise = null;

    private boolean running = false;
    private long startMillis;
    private final AnimationTimer timer = new AnimationTimer() {
        @Override public void handle(long now) {
            if (!running) return;
            long elapsedSec = (System.currentTimeMillis() - startMillis) / 1000;
            updateTimer(elapsedSec);
        }
    };

    @FXML
    private void initialize() {
        // Make choices mutually exclusive
        treadmillBtn.setToggleGroup(choiceGroup);
        weightsBtn.setToggleGroup(choiceGroup);
        bicycleBtn.setToggleGroup(choiceGroup);
        swimmingBtn.setToggleGroup(choiceGroup);

        // Initial UI state
        selectedExerciseLabel.setText("—");
        timeLabel.setText("00:00:00");
        caloriesLabel.setText("0 kcal");
        startButton.setDisable(true);
        stopButton.setDisable(true);
        resetButton.setDisable(true);
    }

    // Fired by all four ToggleButtons via onAction="#onChoice"
    @FXML
    private void onChoice() {
        ToggleButton sel = (ToggleButton) choiceGroup.getSelectedToggle();
        if (sel != null) {
            currentExercise = sel.getText();
            selectedExerciseLabel.setText(currentExercise);
            startButton.setDisable(false);
        } else {
            currentExercise = null;
            selectedExerciseLabel.setText("—");
            startButton.setDisable(true);
        }
    }

    @FXML
    private void onStart() {
        if (currentExercise == null) return;
        running = true;
        startMillis = System.currentTimeMillis();
        timer.start();
        startButton.setDisable(true);
        stopButton.setDisable(false);
        resetButton.setDisable(true);
    }

    @FXML
    private void onStop() {
        running = false;
        timer.stop();
        stopButton.setDisable(true);
        resetButton.setDisable(false);

        // TODO: Save finished session to Firestore here if desired
        // using SessionManager.getSession().getUser().getEmail() to identify the user.
    }

    @FXML
    private void onReset() {
        running = false;
        timer.stop();
        timeLabel.setText("00:00:00");
        caloriesLabel.setText("0 kcal");
        startButton.setDisable(currentExercise == null);
        stopButton.setDisable(true);
        resetButton.setDisable(true);
    }

    private void updateTimer(long elapsedSec) {
        long h = elapsedSec / 3600;
        long m = (elapsedSec % 3600) / 60;
        long s = elapsedSec % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", h, m, s));

        // Simple placeholder calorie math (~20 kcal/min => 0.33 kcal/sec)
        long kcal = Math.round(elapsedSec * 0.33);
        caloriesLabel.setText(kcal + " kcal");
    }

    //button for going back to the app dashboard
    @FXML
    private void dashboardButtonClick(ActionEvent event) {
        try {
            Stage stage = (Stage) dashboardButton.getScene().getWindow();
            Dashboard.loadDashboardScene(stage);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
