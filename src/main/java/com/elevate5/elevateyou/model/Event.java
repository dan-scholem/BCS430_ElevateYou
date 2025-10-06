package com.elevate5.elevateyou.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Event {

    private LocalDate date;
    private LocalTime time;
    private String eventName;
    private String eventDescription;


    public Event(LocalDate date, LocalTime time, String eventName, String eventDescription){
        this.date = date;
        this.time = time;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

}
