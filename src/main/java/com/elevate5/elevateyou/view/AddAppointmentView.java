package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.viewmodel.AppointmentViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddAppointmentView {

    @FXML
    private DatePicker appointmentDate =  new DatePicker();
    @FXML
    private ComboBox<String> appointmentTimeHour;
    @FXML
    private ComboBox<String> appointmentTimeMinute;
    @FXML
    private ComboBox<String> appointmentTimeAMPM;
    @FXML
    private TextField appointmentDoctorName;
    @FXML
    private TextField appointmentDoctorType;
    @FXML
    private TextField appointmentDoctorPhone;
    @FXML
    private TextField appointmentDoctorAddress;
    @FXML
    private TextArea appointmentNotes;

    private AppointmentViewModel appointmentViewModel = new AppointmentViewModel();

    Session session;

    @FXML
    public void initialize() {

        session = SessionManager.getSession();

        appointmentDate.getEditor().textProperty().bindBidirectional(appointmentViewModel.dateProperty());
        appointmentTimeHour.getEditor().textProperty().bindBidirectional(appointmentViewModel.timeHourProperty());
        appointmentTimeMinute.getEditor().textProperty().bindBidirectional(appointmentViewModel.timeMinuteProperty());
        appointmentTimeAMPM.valueProperty().bindBidirectional(appointmentViewModel.timeAMPMProperty());
        appointmentDoctorName.textProperty().bindBidirectional(appointmentViewModel.docNameProperty());
        appointmentDoctorType.textProperty().bindBidirectional(appointmentViewModel.typeProperty());
        appointmentDoctorPhone.textProperty().bindBidirectional(appointmentViewModel.phoneProperty());
        appointmentDoctorAddress.textProperty().bindBidirectional(appointmentViewModel.addressProperty());
        appointmentNotes.textProperty().bindBidirectional(appointmentViewModel.notesProperty());

        appointmentTimeHour.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
        appointmentTimeMinute.getItems().addAll("00", "15", "30", "45");
        appointmentTimeAMPM.getItems().addAll("AM", "PM");
        appointmentTimeAMPM.setValue("AM");

    }

    @FXML
    public void addNewAppointment(ActionEvent event) {
        appointmentViewModel.addAppointment();
        appointmentNotes.getScene().getWindow().hide();
    }

    @FXML
    public void cancelNewAppointment(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
}
