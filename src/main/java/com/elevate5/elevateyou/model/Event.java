package com.elevate5.elevateyou.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Event {

    private String date;
    private String time;
    private String eventName;
    private String eventDescription;


    public Event(){

    }

    public Event(String date, String time, String eventName, String eventDescription){
        this.date = date;
        this.time = time;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
    }

    public String getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date.toString();
    }

    public String getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time.toString();
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

    public String formatTime(String time){
        String formattedTime;
        String[] timeArray = time.split(":");
        int hour = Integer.parseInt(timeArray[0]);
        if(hour == 0){
            formattedTime = "12:" + timeArray[1] + "am";
        }else if(hour > 12){
            hour -= 12;
            formattedTime = hour + ":" + timeArray[1] + "pm";
        }else{
            formattedTime = time + "am";
        }
        return formattedTime;
    }

    @Override
    public String toString(){
        return formatTime(time) + " " + eventName;
    }

}
