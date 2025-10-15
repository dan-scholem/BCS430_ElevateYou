package com.elevate5.elevateyou;

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
}