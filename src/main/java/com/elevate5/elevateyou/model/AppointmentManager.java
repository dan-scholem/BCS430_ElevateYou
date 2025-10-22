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
import java.util.ArrayList;

public class AppointmentManager {

    private ArrayList<AppointmentModel> appointments;

    public AppointmentManager() {
        this.appointments = new ArrayList<AppointmentModel>();
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
            Long.parseLong(docPhone);
            if(docPhone.length() != 10){
                throw new RuntimeException("Invalid phone number");
            }
            docPhone = "(" + docPhone.substring(0, 3) + ")" + docPhone.substring(3, 6) + "-" + docPhone.substring(6);
            try{
                AppointmentModel newAppointment = new AppointmentModel(date, time, docName, docType, docPhone, docAddress, notes);
                //System.out.println(newAppointment);
                DocumentReference appointmentDocRef = App.fstore.collection("Appointments").document(session.getUserID());
                session.getUserAppointmentManager().addAppointment(newAppointment);
                ApiFuture<WriteResult> result = appointmentDocRef.set(session.getUserAppointmentManager());
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
        } catch(NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Error");
            alert.setContentText("Invalid Phone Number");
            alert.showAndWait();
        }
    }

    public static void deleteAppointment(AppointmentModel appointment, Session session) {
        session.getUserAppointmentManager().removeAppointment(appointment);
        try{
            DocumentReference appointmentDocRef = App.fstore.collection("Appointments").document(session.getUserID());
            ApiFuture<WriteResult> result = appointmentDocRef.set(session.getUserAppointmentManager());
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public void addAppointment(AppointmentModel appointment){
        this.appointments.add(appointment);
    }

    public void removeAppointment(AppointmentModel appointment){
        this.appointments.remove(appointment);
    }

    public ArrayList<AppointmentModel> getAppointments() {
        return appointments;
    }

    public void setAppointments(ArrayList<AppointmentModel> appointments) {
        this.appointments = appointments;
    }
}
