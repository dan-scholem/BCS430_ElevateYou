package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.session.Session;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import javafx.scene.control.Alert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

    public static void createAppointment(String docName, String docPhone, String docAddress, String date, String timeHour, String timeMinute, String timeAMPM, String docType, String notes, Session session) {
        String time = "";
        if(timeAMPM.equals("PM") && Integer.parseInt(timeHour) != 12){
            timeHour = Integer.toString(Integer.parseInt(timeHour) + 12);
        }
        if(timeAMPM.equals("AM") && Integer.parseInt(timeHour) == 12){
            timeHour = "00";
        }
        if(timeMinute.length() == 1){
            timeMinute = "0" + timeMinute;
        }
        if(timeHour.length() == 1){
            timeHour = "0" + timeHour;
        }
        time = timeHour + ":" + timeMinute;


        try{
            LocalDate testDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            LocalTime testTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            try{
                AppointmentModel newAppointment = new AppointmentModel(docType, notes, date, time, docAddress, docName, docPhone);
                System.out.println(newAppointment);
                DocumentReference eventDocRef = App.fstore.collection("Appointments").document(session.getUserID());
                session.getUserAppointmentManager().addAppointment(newAppointment);
                ApiFuture<WriteResult> result = eventDocRef.set(session.getUserAppointmentManager());
            } catch(Exception ex){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }

        } catch(DateTimeParseException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Invalid Date or Time");
            alert.showAndWait();
        }




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
        return this.docName + " " + this.docPhone + " " + this.time + " " + this.address;
    }
}
