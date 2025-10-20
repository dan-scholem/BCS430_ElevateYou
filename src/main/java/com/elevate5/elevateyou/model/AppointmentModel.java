package com.elevate5.elevateyou.model;

public class AppointmentModel {

    private String type;
    private String notes;
    private String date;
    private String time;
    private String location;
    private String docName;
    private String docPhone;

    public AppointmentModel(String type, String notes, String date, String time, String location, String docName, String docPhone) {
        this.type = type;
        this.notes = notes;
        this.date = date;
        this.time = time;
        this.location = location;
        this.docName = docName;
        this.docPhone = docPhone;
    }

    public static void createAppointment(String docName, String docPhone, String docAddress, String date, String timeHour, String timeMinute, String timeAMPM, String docType, String notes) {
        String time = "";
        if(timeAMPM.equals("PM") && Integer.parseInt(timeHour) != 12){
            time += (Integer.parseInt(timeHour) + 12);
        }
        time += ":" + timeMinute;

        AppointmentModel newAppointment = new AppointmentModel(docType, notes, date, time, docAddress, docName, docPhone);
        System.out.println(newAppointment);
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

    public String getDoctorPhone() {
        return docPhone;
    }
    public void setDoctorPhone(String docPhone) {
        this.docPhone = docPhone;
    }

    @Override
    public String toString() {
        return this.docName + " " + this.docPhone + " " + this.time + " " + this.location;
    }
}
