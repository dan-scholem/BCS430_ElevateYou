package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class JournalEntry {

    private String title;
    private String entrycontent;
    private LocalDate entrydate;
    private String mood;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEntryContent() {
        return entrycontent;
    }

    public void setEntryContent(String entrycontent) {
        this.entrycontent = entrycontent;
    }

    public LocalDate getEntryDate() {
        return entrydate;
    }

    public void setEntrydate(LocalDate entrydate) {
        this.entrydate = entrydate;
    }


    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
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