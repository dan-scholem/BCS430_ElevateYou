package com.elevate5.elevateyou.model.calendardata;

import com.elevate5.elevateyou.model.Event;

import java.time.LocalDate;
import java.util.ArrayList;

public class DayData {

    private LocalDate date;
    private ArrayList<Event> events;

    public DayData(LocalDate date) {
        this.date = date;
        this.events = new ArrayList<>();
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public void deleteEvent(Event event) {
        this.events.remove(event);
    }


}
