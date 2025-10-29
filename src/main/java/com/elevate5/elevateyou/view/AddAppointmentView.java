package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.viewmodel.AppointmentViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

    private final AppointmentViewModel appointmentViewModel = new AppointmentViewModel();

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

        if(session != null && session.getSelectedDoctor() != null) {
            appointmentDoctorName.setText(session.getSelectedDoctor().getFirstName() +  " " + session.getSelectedDoctor().getLastName());
            appointmentDoctorAddress.setText(session.getSelectedDoctor().getAddress());
            appointmentDoctorPhone.setText(session.getSelectedDoctor().getPhoneNumber());
            appointmentDoctorType.setText(session.getSelectedDoctor().getSpecialty());
        }

    }

    @FXML
    private void addNewAppointment(ActionEvent event) {
        if(appointmentDoctorPhone.getText().length() != 10){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invalid Phone Number");
            alert.setHeaderText("Invalid Phone Number");
            alert.setContentText("Please enter a valid Phone Number: Must Be 10 Digits");
            alert.showAndWait();
        }else {
            appointmentViewModel.addAppointment();
            if(session.getSelectedDoctor() != null) {
                session.setSelectedDoctor(null);
            }
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void cancelNewAppointment(ActionEvent event) {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }
}
