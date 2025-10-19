package com.elevate5.elevateyou.model;

public class AppointmentModel {

    private String category;
    private String description;
    private String date;
    private String time;
    private String location;
    private String docName;

    public AppointmentModel(String category, String description, String date, String time, String location, String docName) {
        this.category = category;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
        this.docName = docName;
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
        return docName;
    }

    public void setDoctorName(String docName) {
        this.docName = docName;
    }
}
