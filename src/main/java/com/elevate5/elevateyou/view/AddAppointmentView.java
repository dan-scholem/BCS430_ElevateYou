package com.elevate5.elevateyou.view;

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


    @FXML
    public void initialize() {
        appointmentDate.getEditor().textProperty().bindBidirectional(appointmentViewModel.dateProperty());
        appointmentTimeHour.getEditor().textProperty().bindBidirectional(appointmentViewModel.timeHourProperty());
        appointmentTimeMinute.getEditor().textProperty().bindBidirectional(appointmentViewModel.timeMinuteProperty());
        appointmentTimeAMPM.getEditor().textProperty().bindBidirectional(appointmentViewModel.timeAMPMProperty());
        appointmentDoctorName.textProperty().bindBidirectional(appointmentViewModel.docNameProperty());
        appointmentDoctorType.textProperty().bindBidirectional(appointmentViewModel.typeProperty());
        appointmentDoctorPhone.textProperty().bindBidirectional(appointmentViewModel.phoneProperty());
        appointmentDoctorAddress.textProperty().bindBidirectional(appointmentViewModel.addressProperty());
        appointmentNotes.textProperty().bindBidirectional(appointmentViewModel.notesProperty());

        appointmentTimeAMPM.getItems().add("AM");
        appointmentTimeAMPM.getItems().add("PM");

    }

    @FXML
    public void addNewAppointment(ActionEvent event) {
        appointmentViewModel.addAppointment();
    }

    @FXML
    public void cancelNewAppointment(ActionEvent event) {
        ((Node)event.getSource()).getScene().getWindow().hide();
    }
}
