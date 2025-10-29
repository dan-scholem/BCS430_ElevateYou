package com.elevate5.elevateyou;

import com.elevate5.elevateyou.model.SleepModel;
import com.elevate5.elevateyou.service.SleepArticleService;
import com.elevate5.elevateyou.service.SleepService;
import com.elevate5.elevateyou.view.CalendarView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SleepController {

    @FXML private ListView<String> EntryList;
    @FXML private TextArea entryTextArea;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField startHourField;
    @FXML private TextField startMinuteField;
    @FXML private TextField endHourField;
    @FXML private TextField endMinuteField;
    @FXML private Button useNowStart;
    @FXML private Button useNowEnd;
    @FXML private CheckBox fragmented;
    @FXML private CheckBox lateExercise;
    @FXML private TextField napMinutesField;

    @FXML private Button dashButton;
    @FXML private Button medButton;
    @FXML private Button foodButton;
    @FXML private Button calendarButton;
    @FXML private Button logoutButton;
    @FXML private Button journalButton;

    private final SleepService sleepService = new SleepService();
    private final SleepArticleService articleService = new SleepArticleService();

    private final ObservableList<String> dayKeys = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_KEY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneId ZONE = ZoneId.systemDefault();

    @FXML
    private void initialize() {
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today);
        endDatePicker.setValue(today);

        EntryList.setItems(dayKeys);
        refreshListAndArticlePreview(null);

    }

    @FXML
    private void onEntryRowClicked(MouseEvent e) {
        String key = EntryList.getSelectionModel().getSelectedItem();
        if (key == null) return;

        try {
            List<SleepModel> segments = sleepService.getSegmentsForDay(key);
            if (!segments.isEmpty()) {
                SleepModel s = segments.get(0);
                parseIsoToFields(s.getStartIso(), true);
                parseIsoToFields(s.getEndIso(), false);

                if (napMinutesField != null) {
                    Integer nap = s.getNap();
                    napMinutesField.setText(String.valueOf(nap == null ? 0 : nap));
                }

                Map<String, Object> art = articleService.getArticle(key);
                if (art == null || art.isEmpty()) {
                    entryTextArea.clear();
                } else {
                    Object articleObj = art.get("article");
                    entryTextArea.setText(articleObj == null ? "" : String.valueOf(articleObj));                }
            } else {
                entryTextArea.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfo("Load failed: " + ex.getMessage());
        }
    }

    @FXML
    private void onUseNowStart() {
        LocalDate d = startDatePicker.getValue() != null ? startDatePicker.getValue() : LocalDate.now();
        LocalTime t = LocalTime.now();
        startDatePicker.setValue(d);
        startHourField.setText(String.format("%02d", t.getHour()));
        startMinuteField.setText(String.format("%02d", t.getMinute()));
    }

    @FXML
    private void onUseNowEnd() {
        LocalDate d = endDatePicker.getValue() != null ? endDatePicker.getValue() : LocalDate.now();
        LocalTime t = LocalTime.now();
        endDatePicker.setValue(d);
        endHourField.setText(String.format("%02d", t.getHour()));
        endMinuteField.setText(String.format("%02d", t.getMinute()));
    }

    private int getNapMinutesOrZero(){
        String s = napMinutesField.getText();
        if (s == null || s.isBlank()) return 0;
        try {
            int v = Integer.parseInt(s);
            return Math.max(0, v);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @FXML
    private void addEntry() {
        try {
            SleepModel m = buildModelFromInputs();
            String dateKey = extractAnchorDateFromStart();
            sleepService.addSegment(dateKey, m);
            articleService.generateAndSave(dateKey);
            showInfo("Successfully Added.");

            refreshListAndArticlePreview(dateKey);
            clearFactorsOnly();
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfo("Add failed: " + ex.getMessage());
        }
    }

    @FXML
    private void updateEntry() {
        try {
            String dateKey = EntryList.getSelectionModel().getSelectedItem();
            if (dateKey == null) dateKey = extractAnchorDateFromStart();

            SleepModel m = buildModelFromInputs();
            sleepService.overwriteDay(dateKey, Collections.singletonList(m));
            articleService.generateAndSave(dateKey);

            showInfo("Successfully Updated.");
            refreshListAndArticlePreview(dateKey);
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfo("Update failed: " + ex.getMessage());
        }
    }

    @FXML
    private void deleteEntry() {
        try {
            String dateKey = EntryList.getSelectionModel().getSelectedItem();
            if (dateKey == null) {
                showInfo("Select a day to delete.");
                return;
            }
            sleepService.deleteDay(dateKey);
            articleService.deleteArticle(dateKey);

            showInfo("Deleted day: " + dateKey);
            refreshListAndArticlePreview(null);
            clearAllInputs();
        } catch (Exception ex) {
            ex.printStackTrace();
            showInfo("Delete failed: " + ex.getMessage());
        }
    }

    @FXML
    private void clearEntry() {
        clearAllInputs();
        entryTextArea.clear();
        EntryList.getSelectionModel().clearSelection();
    }

    @FXML
    private void dashboardButtonClick() {
        try {
            Stage stage = (Stage) dashButton.getScene().getWindow();
            Dashboard.loadDashboardScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void medicationButtonClick() {
        try {
            Stage stage = (Stage) medButton.getScene().getWindow();
            Medication.loadMedTrackerScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void foodButtonClick() {
        try {
            Stage stage = (Stage) foodButton.getScene().getWindow();
            CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void journalButtonClick() throws IOException {
        try {
            Stage stage = (Stage) journalButton.getScene().getWindow();
            JournalEntry.loadJournalScene(stage);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    @FXML
    private void calendarButtonClick() {
        try {
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            CalendarView.loadCalendarScene(stage);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    @FXML
    private void logoutUser() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            UserLogin.loadUserLoginScene(stage);
        } catch (IOException e) { throw new RuntimeException(e); }
    }

    private SleepModel buildModelFromInputs() {
        LocalDate sd = require(startDatePicker.getValue(), "Start date required");
        LocalDate ed = require(endDatePicker.getValue(),   "End date required");

        int sh = clampHour(parseIntOrZero(startHourField.getText()));
        int sm = clampMinute(parseIntOrZero(startMinuteField.getText()));
        int eh = clampHour(parseIntOrZero(endHourField.getText()));
        int em = clampMinute(parseIntOrZero(endMinuteField.getText()));

        ZonedDateTime start = sd.atTime(sh, sm).atZone(ZONE);
        ZonedDateTime end   = ed.atTime(eh, em).atZone(ZONE);
        if (!end.isAfter(start)) throw new IllegalArgumentException("End must be after start.");

        int duration = (int) Duration.between(start, end).toMinutes();
        int nap = getNapMinutesOrZero();
        int total = duration + nap;

        Map<String, Object> factors = new HashMap<>();
        factors.put("fragmented", fragmented != null && fragmented.isSelected());
        factors.put("lateExercise", lateExercise != null && lateExercise.isSelected());

        SleepModel m = new SleepModel();
        m.setStartIso(start.toOffsetDateTime().toString());
        m.setEndIso(end.toOffsetDateTime().toString());
        m.setDurationMin(duration);
        m.setNap(nap);
        m.setTotalMin(total);
        m.setFactors(factors);
        return m;
    }

    private String extractAnchorDateFromStart() {
        LocalDate sd = require(startDatePicker.getValue(), "Start date required");
        return DATE_KEY.format(sd);
    }

    private void parseIsoToFields(String iso, boolean isStart) {
        if (iso == null || iso.isBlank()) return;
        OffsetDateTime odt = OffsetDateTime.parse(iso);
        ZonedDateTime zdt = odt.atZoneSameInstant(ZONE);
        if (isStart) {
            startDatePicker.setValue(zdt.toLocalDate());
            startHourField.setText(String.format("%02d", zdt.getHour()));
            startMinuteField.setText(String.format("%02d", zdt.getMinute()));
        } else {
            endDatePicker.setValue(zdt.toLocalDate());
            endHourField.setText(String.format("%02d", zdt.getHour()));
            endMinuteField.setText(String.format("%02d", zdt.getMinute()));
        }
    }

    private void refreshListAndArticlePreview(String highlightDateKey) {
        try {
            Map<String, List<SleepModel>> all = sleepService.getAllSegments();
            dayKeys.setAll(sleepService.getAllSegments().keySet());
            if (highlightDateKey != null) {
                EntryList.getSelectionModel().select(highlightDateKey);
                EntryList.scrollTo(highlightDateKey);
                Map<String, Object> art = articleService.getArticle(highlightDateKey);
                if (art == null || art.isEmpty()) {
                    entryTextArea.clear();
                } else {
                    Object articleObj = art.get("article");
                    entryTextArea.setText(articleObj == null ? "" : String.valueOf(articleObj));                }
            } else {
                EntryList.getSelectionModel().clearSelection();
                entryTextArea.clear();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearAllInputs() {
        LocalDate today = LocalDate.now();
        startDatePicker.setValue(today);
        endDatePicker.setValue(today);
        startHourField.clear();
        startMinuteField.clear();
        endHourField.clear();
        endMinuteField.clear();
        napMinutesField.clear();
        clearFactorsOnly();
    }

    private void clearFactorsOnly() {
        if (fragmented != null) fragmented.setSelected(false);
        if (lateExercise != null) lateExercise.setSelected(false);
    }

    private int parseIntOrZero(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 0; }
    }
    private int clampHour(int h)   { return Math.max(0, Math.min(23, h)); }
    private int clampMinute(int m) { return Math.max(0, Math.min(59, m)); }

    private <T> T require(T v, String msg) {
        if (v == null) throw new IllegalArgumentException(msg);
        return v;
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}