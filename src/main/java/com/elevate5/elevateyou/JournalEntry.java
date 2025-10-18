package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class JournalEntry {

    private String documentID;
    private String journaltitle;
    private String entrycontent;
    private LocalDate entryDate;
    private String mood;

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String toString() {
        return journaltitle;
    }

    public void setTitle(String title) {
        this.journaltitle = journaltitle;
    }

    public String getEntryContent() {
        return entrycontent;
    }

    public void setEntryContent(String entrycontent) {
        this.entrycontent = entrycontent;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entrydate) {
        this.entryDate = entrydate;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public JournalEntry(String documentID, String title, LocalDate entrydate, String entrycontent, String mood) {
        this.documentID = documentID;
        this.journaltitle = title;
        this.entryDate = entrydate;
        this.entrycontent = entrycontent;
        this.mood = mood;
    }

    public static void loadJournalScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("PersonalJournal.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Personal Journal");
        stage.setScene(scene);
        stage.show();
    }
}