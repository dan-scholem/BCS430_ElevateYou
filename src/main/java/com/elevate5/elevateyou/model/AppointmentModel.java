package com.elevate5.elevateyou.model;


public class AppointmentModel {

    private String date;
    private String time;
    private String docName;
    private String type;
    private String docPhone;
    private String address;
    private String notes;

    public AppointmentModel(String date, String time, String docName, String type, String docPhone, String address, String notes) {
        this.date = date;
        this.time = time;
        this.docName = docName;
        this.type = type;
        this.docPhone = docPhone;
        this.address = address;
        this.notes = notes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocPhone() {
        return docPhone;
    }
    public void setDocPhone(String docPhone) {
        this.docPhone = docPhone;
    }

    @Override
    public String toString() {
        return this.docName + " " + this.docPhone + " " + this.time + " " + this.date;
    }
}
