package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class Medication {

        private String medicationName;
        private String dosage;
        private LocalDate startDate;
        private LocalDate endDate;
        private String frequency;
        private String notes;

        public String getMedicationName() {
            return medicationName;
        }

        public void setMedicationName(String medicationName) {
            this.medicationName = medicationName;
        }

        public String getDosage() {
            return dosage;
        }

        public void setDosage(String dosage) {
            this.dosage = dosage;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public String getFrequency() {
            return frequency;
        }

        public void setFrequency(String frequency) {
            this.frequency = frequency;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Medication(String medicationName, String dosage, String frequency, LocalDate startDate, LocalDate endDate, String notes) {
            this.medicationName = medicationName;
            this.dosage = dosage;
            this.startDate = startDate;
            this.endDate = endDate;
            this.frequency = frequency;
            this.notes = notes;

        }

        public static void loadMedTrackerScene(Stage stage) throws IOException {

            FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("MedicationTracker.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Medication Tracker");
            stage.setScene(scene);
            stage.show();
        }


    }

