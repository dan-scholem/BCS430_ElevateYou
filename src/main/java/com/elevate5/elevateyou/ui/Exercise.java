package com.elevate5.elevateyou.ui;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Exercise {
    // ===== Model fields (unchanged) =====
    public final String name;
    public final long durationSec;
    public final double calories;
    public final String startIso;
    public final String endIso;
    public final long createdAtEpoch;

    public Exercise(String name, long durationSec, double calories, Instant start, Instant end) {
        this.name = name;
        this.durationSec = durationSec;
        this.calories = calories;
        this.startIso = iso(start);
        this.endIso = iso(end);
        this.createdAtEpoch = Instant.now().getEpochSecond();
    }

    private static String iso(Instant t) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME
                .withZone(ZoneId.systemDefault())
                .format(t);
    }

    // Firestore-friendly map (unchanged)
    public Map<String, Object> toMap(String userEmail) {
        Map<String, Object> m = new HashMap<>();
        m.put("userEmail", userEmail);
        m.put("name", name);
        m.put("durationSec", durationSec);
        m.put("calories", calories);
        m.put("startIso", startIso);
        m.put("endIso", endIso);
        m.put("createdAtEpoch", createdAtEpoch);
        return m;
    }

    @Override
    public String toString() {
        return name + " | " + durationSec + "s | " + (int) calories + " kcal | " + startIso + " → " + endIso;
    }

    // ===== NEW: static loader to show the Exercise screen =====
    public static void loadExerciseScene(Stage stage) throws IOException {
        FXMLLoader fxml = new FXMLLoader(
                Exercise.class.getResource("/com/elevate5/elevateyou/ui/exercise.fxml")
        );
        Parent root = fxml.load();

        // If you ever need the controller:
        // ExerciseController controller = fxml.getController();

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Exercise Tracker");
        stage.show();
    }
}
