package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.UserLogin;
import com.elevate5.elevateyou.model.DoctorModel;
import com.elevate5.elevateyou.model.LocationEntryModel;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DoctorSearchView extends Application {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField specialtyField;
    @FXML
    private ComboBox<LocationEntryModel> locationBox;
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

    List<LocationEntryModel> locations = new ArrayList<>();

    LocationEntryModel selectedLocation;

    @FXML
    private void initialize(){

        firstNameField.textProperty().bindBidirectional(doctorSearchViewModel.firstNameProperty());
        lastNameField.textProperty().bindBidirectional(doctorSearchViewModel.lastNameProperty());
        specialtyField.textProperty().bindBidirectional(doctorSearchViewModel.specialtyProperty());
        locationBox.valueProperty().bindBidirectional(doctorSearchViewModel.locationProperty());

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        locationBox.setEditable(true);

        try{
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/elevate5/elevateyou/uszips.csv"));
            String[] header = reader.readLine().split(",");
            String line;
            while((line = reader.readLine()) != null){
                String[] location = line.split(",");
                LocationEntryModel entry =  new LocationEntryModel(location[0].replaceAll("^\"|\"$",""), location[3].replaceAll("^\"|\"$",""), location[4].replaceAll("^\"|\"$",""));
                locations.add(entry);
                //System.out.println(entry);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        locationBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.length() >= 3){
                List<LocationEntryModel> matches;
                try{
                    Integer.parseInt(newValue);
                    matches = locations.stream()
                            .filter(entry -> entry.getPostalCode().startsWith(newValue))
                            .distinct()
                            .limit(5)
                            .collect(Collectors.toList());
                }catch(NumberFormatException e){
                    String input = newValue.toLowerCase();
                    matches = locations.stream()
                            .filter(entry -> entry.getCity().toLowerCase().startsWith(input))
                            .distinct()
                            .limit(5)
                            .collect(Collectors.toList());
                }

                if(!matches.isEmpty()){
                    locationBox.getItems().setAll(matches);
                    locationBox.show();
                }
            }
        });

        locationBox.setConverter(new StringConverter<LocationEntryModel>() {
            @Override
            public String toString(LocationEntryModel locationEntry){
                return locationEntry == null ? "" : locationEntry.toString();
            }
            @Override
            public LocationEntryModel fromString(String string){
                return locationBox.getSelectionModel().getSelectedItem();
            }
        });

    }


    @FXML
    private void searchButtonAction(ActionEvent event) {
        searchResults = FXCollections.observableArrayList(doctorSearchViewModel.searchDoctors());
        resultsTable.setItems(searchResults);
        resultsTable.refresh();
        locationBox.getSelectionModel().clearSelection();
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
        addAppointmentStage.initModality(Modality.WINDOW_MODAL);
        addAppointmentStage.setScene(addAppointmentScene);
        addAppointmentStage.setResizable(false);
        addAppointmentStage.setTitle("Add Appointment");
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        addAppointmentStage.showAndWait();
        stage.close();
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
