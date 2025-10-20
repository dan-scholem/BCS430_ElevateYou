package com.elevate5.elevateyou.viewmodel;

import com.elevate5.elevateyou.model.AppointmentModel;
import com.elevate5.elevateyou.session.Session;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AppointmentViewModel {

    private final StringProperty docName = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty timeHour = new SimpleStringProperty();
    private final StringProperty timeMinute = new SimpleStringProperty();
    private final StringProperty timeAMPM = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private Session session;


    public void addAppointment(Session session) {
        String docName = getDocName();
        String type = getType();
        String date = getDate();
        String timeHour = getTimeHour();
        String timeMinute = getTimeMinute();
        String timeAMPM = getTimeAMPM();
        String address = getAddress();
        String notes = getNotes();
        String phone = getPhone();
        this.session = session;
        AppointmentModel.createAppointment(docName, phone, address, date, timeHour, timeMinute, timeAMPM, type, notes, session);
    }


    public String getDocName() {
        return docName.get();
    }

    public StringProperty docNameProperty() {
        return docName;
    }

    public String getType() {
        return type.get();
    }

    public StringProperty typeProperty() {
        return type;
    }

    public String getDate() {
        return date.get();
    }

    public StringProperty dateProperty() {
        return date;
    }

    public String getTimeHour() {
        return timeHour.get();
    }

    public StringProperty timeHourProperty() {
        return timeHour;
    }

    public String getTimeMinute() {
        return timeMinute.get();
    }

    public StringProperty timeMinuteProperty() {
        return timeMinute;
    }

    public String getTimeAMPM() {
        return timeAMPM.get();
    }

    public StringProperty timeAMPMProperty() {
        return timeAMPM;
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getNotes() {
        return notes.get();
    }

    public StringProperty notesProperty() {
        return notes;
    }
}
