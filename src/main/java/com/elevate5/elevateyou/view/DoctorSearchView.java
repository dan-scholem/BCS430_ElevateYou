package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.UserLogin;
import com.elevate5.elevateyou.model.DoctorModel;
import com.elevate5.elevateyou.session.SessionManager;
import com.elevate5.elevateyou.viewmodel.DoctorSearchViewModel;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class DoctorSearchView extends Application {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField specialtyField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField distanceField;
    @FXML
    private TableView<DoctorModel> resultsTable;
    @FXML
    private TableColumn<DoctorModel, String> firstNameColumn;
    @FXML
    private TableColumn<DoctorModel, String> lastNameColumn;
    @FXML
    private TableColumn<DoctorModel, String> specialtyColumn;
    @FXML
    private TableColumn<DoctorModel, String> phoneNumberColumn;
    @FXML
    private TableColumn<DoctorModel, String> addressColumn;

    ObservableList<DoctorModel> searchResults = FXCollections.observableArrayList();

    DoctorSearchViewModel doctorSearchViewModel = new DoctorSearchViewModel();

    @FXML
    private void initialize(){

        firstNameField.textProperty().bindBidirectional(doctorSearchViewModel.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(doctorSearchViewModel.lastNameProperty());
        specialtyField.textProperty().bindBidirectional(doctorSearchViewModel.specialtyProperty());
        locationField.textProperty().bindBidirectional(doctorSearchViewModel.locationProperty());

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));


    }


    @FXML
    private void searchButtonAction(ActionEvent event) {
        searchResults = FXCollections.observableArrayList(doctorSearchViewModel.searchDoctors());
        resultsTable.setItems(searchResults);
        resultsTable.refresh();
    }


    @FXML
    private void bookButtonAction(ActionEvent event) throws IOException {
        if(SessionManager.getSession() != null) {
            SessionManager.getSession().setSelectedDoctor(resultsTable.getSelectionModel().getSelectedItem());
        }
        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("AddAppointmentView.fxml"));

        Scene addAppointmentScene = new Scene(fxmlLoader.load(),500,500);
        Stage addAppointmentStage = new Stage();
        addAppointmentStage.initOwner(((Node)event.getSource()).getScene().getWindow());
        addAppointmentStage.setScene(addAppointmentScene);
        addAppointmentStage.setResizable(false);
        addAppointmentStage.setTitle("Add Appointment");
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
        addAppointmentStage.show();
    }

    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }


    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("DoctorSearchView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 900 , 600);
        stage.setTitle("Search Doctors");
        stage.setScene(scene);
        stage.show();
        //stage.setResizable(false);
    }
}
