package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.UserLogin;
import com.elevate5.elevateyou.model.CalendarModel;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;
import java.time.LocalDate;

public class CalendarView extends Application {

    private final LocalDate today = LocalDate.now();

    private LocalDate selectedDate;

    private final static CalendarModel calendar = new CalendarModel();

    ObservableList<CalendarModel>  calendarModelObservableList = FXCollections.observableArrayList();

    @FXML
    private Label monthLabel;

    @FXML
    private Button decrementMonth;

    @FXML
    private Button incrementMonth;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private TableView<CalendarModel> calendarTableView;

    @FXML private TableColumn<CalendarModel,String> sunCol;
    @FXML private TableColumn<CalendarModel,String> monCol;
    @FXML private TableColumn<CalendarModel,String> tueCol;
    @FXML private TableColumn<CalendarModel,String> wedCol;
    @FXML private TableColumn<CalendarModel,String> thuCol;
    @FXML private TableColumn<CalendarModel,String> friCol;
    @FXML private TableColumn<CalendarModel,String> satCol;

    @FXML
    private void initialize(){

        sunCol.setCellValueFactory(new PropertyValueFactory<>("sun"));
        monCol.setCellValueFactory(new PropertyValueFactory<>("mon"));
        tueCol.setCellValueFactory(new PropertyValueFactory<>("tue"));
        wedCol.setCellValueFactory(new PropertyValueFactory<>("wed"));
        thuCol.setCellValueFactory(new PropertyValueFactory<>("thu"));
        friCol.setCellValueFactory(new PropertyValueFactory<>("fri"));
        satCol.setCellValueFactory(new PropertyValueFactory<>("sat"));

        calendarTableView.getSelectionModel().setCellSelectionEnabled(true);
        calendarTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        selectedDate = today;

        monthLabel.setText(selectedDate.getMonth().toString() + ",  " + selectedDate.getYear());

        populateCalendar(selectedDate, calendarTableView);

    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("CalendarView.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1008 , 631);
        stage.setTitle("Calendar");
        stage.setScene(scene);
        stage.show();
        //stage.setResizable(false);
    }

    @FXML
    private void handleDecrementMonth(ActionEvent event) {
        selectedDate = selectedDate.minusMonths(1);
        monthLabel.setText(selectedDate.getMonth().toString() + ",  " + selectedDate.getYear());
        calendarTableView.getItems().clear();
        populateCalendar(selectedDate, calendarTableView);
    }

    @FXML
    private void handleIncrementMonth(ActionEvent event) {
        selectedDate = selectedDate.plusMonths(1);
        monthLabel.setText(selectedDate.getMonth().toString() + ",  " + selectedDate.getYear());
        calendarTableView.getItems().clear();
        populateCalendar(selectedDate, calendarTableView);
    }

    @FXML
    private void populateCalendar(LocalDate date, TableView<CalendarModel> calendarTableView) {

        LocalDate firstDayOfMonth =  date.minusDays(date.getDayOfMonth() - 1);
        int firstDayAsInt = firstDayOfMonth.getDayOfWeek().getValue();
        int dayOfMonthCounter = 1;
        int daysInMonth = calendar.getDaysInMonth().get(date.getMonth().toString());

        for(int row = 0; row < 7; row++){
            CalendarModel week = new CalendarModel();
            for(int col = 0; col < 7; col++){
                if(row == 0 && col >= firstDayAsInt){
                    week.setDay(col, Integer.toString(dayOfMonthCounter));
                    dayOfMonthCounter++;
                } else if(row > 0 && dayOfMonthCounter <= daysInMonth){
                    week.setDay(col, Integer.toString(dayOfMonthCounter));
                    dayOfMonthCounter++;
                }

            }
            calendarTableView.getItems().add(week);
        }
    }


/*
    @FXML
    public static void populateCalendar(LocalDate date, GridPane calendarGrid){


        LocalDate firstDayOfMonth =  date.minusDays(date.getDayOfMonth() - 1);
        int firstDayAsInt = firstDayOfMonth.getDayOfWeek().getValue();
        int dayOfMonthCounter = 1;
        int daysInMonth = calendar.getDaysInMonth().get(date.getMonth().toString());

        for(int row = 0; row < 7; row++){
            for(int col = 0; col < 7; col++){
                if(row == 0){
                    String dayOfWeek = switch (col) {
                        case 0 -> "Sun";
                        case 1 -> "Mon";
                        case 2 -> "Tue";
                        case 3 -> "Wed";
                        case 4 -> "Thu";
                        case 5 -> "Fri";
                        case 6 -> "Sat";
                        default -> "Unknown";
                    };
                    Label label = new Label(dayOfWeek);
                    calendarGrid.add(label, col, row);
                    label.setPadding(new Insets(0, 0, 0, 10));
                } else if(row == 1 && col >= firstDayAsInt){
                    Label label = new Label(Integer.toString(dayOfMonthCounter));
                    calendarGrid.add(label, col, row);
                    GridPane.setHalignment(label, HPos.LEFT);
                    GridPane.setValignment(label, VPos.TOP);
                    label.setPadding(new Insets(0, 0, 0, 5));
                    dayOfMonthCounter++;
                } else if(row > 1 && dayOfMonthCounter <= daysInMonth){
                    Label label = new Label(Integer.toString(dayOfMonthCounter));
                    calendarGrid.add(label, col, row);
                    GridPane.setHalignment(label, HPos.LEFT);
                    GridPane.setValignment(label, VPos.TOP);
                    label.setPadding(new Insets(0, 0, 0, 5));
                    dayOfMonthCounter++;
                }

            }
        }

 */



    /*
        LocalDate today =  LocalDate.now();
        System.out.println("today: " + today);
        System.out.println("today: " + today.getDayOfMonth());
        System.out.println("today: " + today.getDayOfWeek());
        System.out.println("today: " + today.getMonth());

        LocalDate firstDay =  today.minusDays(today.getDayOfMonth() - 1);

        System.out.println("First Day of Month: " + firstDay + "DoW: " + firstDay.getDayOfWeek());

        //System.out.println("Days in Month: " + calendar.getDaysInMonth().get(today.getMonth().toString()));

        System.out.println("DoW as int: " + firstDay.getDayOfWeek().getValue());


        for(int row = 0; row < 7; row++){
            for(int col = 0; col < 7; col++){
                Label label = new Label("Cell " + row + ", " + col);
                calendarGrid.add(label, col, row);
            }
        }

        for(Node node : calendarGrid.getChildren()){
            int colIndex = GridPane.getColumnIndex(node);
            int rowIndex = GridPane.getRowIndex(node);
            System.out.println(rowIndex + ", " + colIndex);
        }


        //today = today.plusDays(1);
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTime(today);
        */
}



