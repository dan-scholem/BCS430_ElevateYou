package com.elevate5.elevateyou.model;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
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

    //creates a user appointment, adds to appointment table and adds to user calendar
    public static void createAppointment(String docName, String docPhone, String docAddress, String date, String timeHour, String timeMinute, String timeAMPM, String docType, String notes) {
        String time;
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


        try{ //input validation
            LocalDate testDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            LocalTime testTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
            Long.parseLong(docPhone);
            if(docPhone.length() != 10){
                throw new RuntimeException("Invalid phone number");
            }
            docPhone = "(" + docPhone.substring(0, 3) + ") " + docPhone.substring(3, 6) + "-" + docPhone.substring(6);

            try{ //updates Firestore db with new appointment and corresponding calendar event
                AppointmentModel newAppointment = new AppointmentModel(date, time, docName, docType, docPhone, docAddress, notes);
                DocumentReference appointmentDocRef = App.fstore.collection("Appointments").document(SessionManager.getSession().getUserID());
                SessionManager.getSession().getUserAppointmentManager().addAppointment(newAppointment);
                ApiFuture<WriteResult> result = appointmentDocRef.set(SessionManager.getSession().getUserAppointmentManager());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dateLocal = LocalDate.parse(date, formatter);
                formatter2.format(dateLocal);
                SessionManager.getSession().getUserEventManager().addEvent(dateLocal.toString(), new Event(dateLocal.toString(), newAppointment.getTime(), newAppointment.getType() + " Appointment", newAppointment.toString()));
                DocumentReference eventDocRef = App.fstore.collection("Events").document(SessionManager.getSession().getUserID());
                result =  eventDocRef.set(SessionManager.getSession().getUserEventManager());
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

    //deletes user appointment from table and calendar
    public static void deleteAppointment(AppointmentModel appointment) {
        SessionManager.getSession().getUserAppointmentManager().removeAppointment(appointment);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dateLocal = LocalDate.parse(appointment.getDate(), formatter);
        formatter2.format(dateLocal);
        ArrayList<Event> eventDateArray = SessionManager.getSession().getUserEventManager().getEvents().get(dateLocal.toString());
        Event apptEvent = null;
        //checks if appointment is in user calendar and if found removes from calendar
        if(eventDateArray != null && !eventDateArray.isEmpty()){
            for(Event event : eventDateArray){
                if(event.getEventDescription().equals(appointment.toString())){
                    System.out.println("Match!");
                    apptEvent = event;
                }
            }
            if(apptEvent != null){
                SessionManager.getSession().getUserEventManager().getEvents().get(apptEvent.getDate()).remove(apptEvent);
            }
        }
        try{ //update Firestore db
            DocumentReference appointmentDocRef = App.fstore.collection("Appointments").document(SessionManager.getSession().getUserID());
            ApiFuture<WriteResult> result = appointmentDocRef.set(SessionManager.getSession().getUserAppointmentManager());
            DocumentReference eventDocRef = App.fstore.collection("Events").document(SessionManager.getSession().getUserID());
            result =  eventDocRef.set(SessionManager.getSession().getUserEventManager());
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    //adds appointment to list data structure
    public void addAppointment(AppointmentModel appointment){
        this.appointments.add(appointment);
    }

    //removes appointment from list data structure
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
