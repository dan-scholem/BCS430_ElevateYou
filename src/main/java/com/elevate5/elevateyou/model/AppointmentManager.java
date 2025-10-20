package com.elevate5.elevateyou.model;

import java.util.ArrayList;

public class AppointmentManager {

    private ArrayList<AppointmentModel> appointments;

    public AppointmentManager() {
        this.appointments = new ArrayList<AppointmentModel>();
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
