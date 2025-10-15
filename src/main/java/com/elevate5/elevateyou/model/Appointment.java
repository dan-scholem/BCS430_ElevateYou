package com.elevate5.elevateyou.model;

public class Appointment {

    private String category;
    private String description;
    private String date;
    private String time;
    private String location;
    private String doctorName;

    public Appointment(String category, String description, String date, String time, String location, String doctorName) {
        this.category = category;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.doctorName = doctorName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
}
