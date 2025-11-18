package com.elevate5.elevateyou.ui;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ExerciseController {

    @FXML private ToggleButton runningBtn;
    @FXML private ToggleButton treadmillBtn;
    @FXML private ToggleButton bicycleBtn;
    @FXML private ToggleButton weightsBtn;
    @FXML private ToggleButton swimmingBtn;
    @FXML private ToggleButton yogaBtn;

    @FXML private Label selectedExerciseLabel;
    @FXML private TextField goalTitleField;
    @FXML private TextArea goalNotesArea;
    @FXML private Spinner<Integer> targetSetsSpinner;
    @FXML private Spinner<Integer> completedSetsSpinner;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    private ToggleGroup exerciseGroup;

    @FXML
    private void initialize() {

        exerciseGroup = new ToggleGroup();

        runningBtn.setToggleGroup(exerciseGroup);
        treadmillBtn.setToggleGroup(exerciseGroup);
        bicycleBtn.setToggleGroup(exerciseGroup);
        weightsBtn.setToggleGroup(exerciseGroup);
        swimmingBtn.setToggleGroup(exerciseGroup);
        yogaBtn.setToggleGroup(exerciseGroup);

        exerciseGroup.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            String name = getSelectedExercise();
            selectedExerciseLabel.setText(name != null ? name : "—");
            statusLabel.setText("");
        });

        targetSetsSpinner.setValueFactory(new IntegerSpinnerValueFactory(1, 500, 10));
        completedSetsSpinner.setValueFactory(new IntegerSpinnerValueFactory(0, 500, 0));

        completedSetsSpinner.valueProperty().addListener((o, a, b) -> updateProgress());
        targetSetsSpinner.valueProperty().addListener((o, a, b) -> updateProgress());

        updateProgress();
    }

    private void updateProgress() {
        int target = safe(targetSetsSpinner.getValue());
        int done = Math.min(safe(completedSetsSpinner.getValue()), target);
        double pct = (target > 0) ? (double) done / target : 0.0;
        progressBar.setProgress(pct);
    }

    private int safe(Integer v) { return v == null ? 0 : v; }

    private String getSelectedExercise() {
        Toggle t = exerciseGroup.getSelectedToggle();
        if (t == null) return null;
        return ((ToggleButton) t).getText();
    }

    @FXML private void onIncCompleted() {
        int v = safe(completedSetsSpinner.getValue());
        completedSetsSpinner.getValueFactory().setValue(v + 1);
    }

    @FXML private void onDecCompleted() {
        int v = safe(completedSetsSpinner.getValue());
        if (v > 0) completedSetsSpinner.getValueFactory().setValue(v - 1);
    }

    @FXML private void onReset() {
        exerciseGroup.selectToggle(null);
        selectedExerciseLabel.setText("—");
        goalTitleField.clear();
        goalNotesArea.clear();
        targetSetsSpinner.getValueFactory().setValue(10);
        completedSetsSpinner.getValueFactory().setValue(0);
        progressBar.setProgress(0);
        statusLabel.setText("");
    }

    @FXML
    private void onSave() {

        String workout = getSelectedExercise();
        if (workout == null) {
            toast("Please choose a workout.");
            return;
        }

        String goalTitle = goalTitleField.getText() == null ? "" : goalTitleField.getText().trim();
        if (goalTitle.isEmpty()) {
            toast("Please enter a goal title.");
            return;
        }

        int target = safe(targetSetsSpinner.getValue());
        int done = Math.min(safe(completedSetsSpinner.getValue()), target);
        double pct = (target > 0) ? (double) done / target : 0.0;

        Map<String, Object> data = new HashMap<>();
        data.put("workout", workout);
        data.put("goalTitle", goalTitle);
        data.put("notes", goalNotesArea.getText());
        data.put("targetSets", target);
        data.put("completedSets", done);
        data.put("progress", pct);
        data.put("status", done >= target ? "completed" : "in_progress");
        data.put("updatedAt", Instant.now().toString());
        data.put("createdAt", Instant.now().toString());

        String uid = null, email = null;
        if (SessionManager.getSession() != null && SessionManager.getSession().getUser() != null) {
            uid = SessionManager.getSession().getUser().getUid();
            email = SessionManager.getSession().getUser().getEmail();
        }

        data.put("uid", uid);
        data.put("email", email);

        try {
            if (uid == null || uid.isEmpty()) {
                toast("No logged-in user — cannot save.");
                return;
            }

            DocumentReference doc = App.fstore
                    .collection("users").document(uid)
                    .collection("workoutGoals").document();

            ApiFuture<WriteResult> future = doc.set(data);
            future.get();

            toast("Saved ✔ (" + done + "/" + target + ")");
        } catch (Exception e) {
            e.printStackTrace();
            toast("Save failed: " + e.getMessage());
        }
    }

    @FXML
    private void onBackToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/elevate5/elevateyou/Dashboard.fxml")
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1218, 738));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            toast("Failed to open Dashboard: " + e.getMessage());
        }
    }

    private void toast(String msg) {
        statusLabel.setText(msg);
    }
}
