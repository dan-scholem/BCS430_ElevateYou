package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.UserLogin;
import com.elevate5.elevateyou.model.CalendarModel;
import com.elevate5.elevateyou.model.Event;
import com.elevate5.elevateyou.model.calendardata.DayData;
import com.elevate5.elevateyou.model.calendardata.WeekData;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class CalendarView extends Application {

    private final LocalDate today = LocalDate.now();

    private LocalDate selectedDate;

    private final static CalendarModel calendar = new CalendarModel();

    private ObservableList<WeekData> selection;

    @FXML
    private Label monthLabel;

    @FXML
    private Button decrementMonth;

    @FXML
    private Button incrementMonth;

    @FXML
    private TableView<WeekData> calendarTableView;

    @FXML
    private TableColumn<WeekData,DayData> sunCol;
    @FXML
    private TableColumn<WeekData,DayData> monCol;
    @FXML
    private TableColumn<WeekData,DayData> tueCol;
    @FXML
    private TableColumn<WeekData,DayData> wedCol;
    @FXML
    private TableColumn<WeekData,DayData> thuCol;
    @FXML
    private TableColumn<WeekData,DayData> friCol;
    @FXML
    private TableColumn<WeekData,DayData> satCol;

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
        calendarTableView.setFixedCellSize(100);

        selectedDate = today;

        monthLabel.setText(selectedDate.getMonth().toString() + ",  " + selectedDate.getYear());

        populateCalendar(selectedDate, calendarTableView);

        selection = calendarTableView.getSelectionModel().getSelectedItems();

        sunCol.setCellFactory(col -> {
                    TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                        @Override
                        protected void updateItem(DayData item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                            } else {
                                String dayOfMonth;
                                if (item.getDate() != null) {
                                    dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                                } else {
                                    dayOfMonth = "";
                                }
                                setText(dayOfMonth);
                                setStyle("-fx-alignment: TOP-LEFT;");
                            }
                        }
                    };
                    cell.setOnMouseClicked(event -> {
                        if(!cell.isEmpty()) {
                            System.out.println(cell.getItem().getDate().toString());
                        }
                    });
                    return cell;
                });




        monCol.setCellFactory(col -> {
            TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                @Override
                protected void updateItem(DayData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String dayOfMonth;
                        if (item.getDate() != null) {
                            dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                        } else {
                            dayOfMonth = "";
                        }
                        setText(dayOfMonth);
                        setStyle("-fx-alignment: TOP-LEFT;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if(!cell.isEmpty()) {
                    System.out.println(cell.getItem().getDate().toString());
                }
            });
            return cell;
        });

        tueCol.setCellFactory(col -> {
            TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                @Override
                protected void updateItem(DayData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String dayOfMonth;
                        if (item.getDate() != null) {
                            dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                            if(!item.getEvents().isEmpty()) {
                                for(Event event : item.getEvents()) {
                                    dayOfMonth +=  event.toString() + "\n";
                                }
                            }
                        } else {
                            dayOfMonth = "";
                        }
                        setText(dayOfMonth);
                        setStyle("-fx-alignment: TOP-LEFT;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if(!cell.isEmpty()) {
                    System.out.println(cell.getItem().getDate().toString());
                }
            });
            return cell;
        });

        wedCol.setCellFactory(col -> {
            TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                @Override
                protected void updateItem(DayData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String dayOfMonth;
                        if (item.getDate() != null) {
                            dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                        } else {
                            dayOfMonth = "";
                        }
                        setText(dayOfMonth);
                        setStyle("-fx-alignment: TOP-LEFT;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if(!cell.isEmpty()) {
                    System.out.println(cell.getItem().getDate().toString());
                }
            });
            return cell;
        });

        thuCol.setCellFactory(col -> {
            TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                @Override
                protected void updateItem(DayData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String dayOfMonth;
                        if (item.getDate() != null) {
                            dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                        } else {
                            dayOfMonth = "";
                        }
                        setText(dayOfMonth);
                        setStyle("-fx-alignment: TOP-LEFT;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if(!cell.isEmpty()) {
                    System.out.println(cell.getItem().getDate().toString());
                }
            });
            return cell;
        });

        friCol.setCellFactory(col -> {
            TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                @Override
                protected void updateItem(DayData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String dayOfMonth;
                        if (item.getDate() != null) {
                            dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                        } else {
                            dayOfMonth = "";
                        }
                        setText(dayOfMonth);
                        setStyle("-fx-alignment: TOP-LEFT;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if(!cell.isEmpty()) {
                    System.out.println(cell.getItem().getDate().toString());
                }
            });
            return cell;
        });

        satCol.setCellFactory(col -> {
            TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                @Override
                protected void updateItem(DayData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String dayOfMonth;
                        if (item.getDate() != null) {
                            dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                        } else {
                            dayOfMonth = "";
                        }
                        setText(dayOfMonth);
                        setStyle("-fx-alignment: TOP-LEFT;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                if(!cell.isEmpty()) {
                    System.out.println(cell.getItem().getDate().toString());
                }
            });
            return cell;
        });

        LocalDate sampleDate = LocalDate.of(2025,10,6);
        LocalTime sampleTime = LocalTime.of(16,30);

        Event sampleEvent = new Event(sampleDate, sampleTime, "Dr Appointment", "Appointment");

        System.out.println(sampleEvent.toString());

        //DayData sampleDay = new DayData(sampleEvent.getDate());

        LocalDate target = sampleEvent.getDate();

        for(WeekData week : calendar.getWeeks()){

        }

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
    private void populateCalendar(LocalDate date, TableView<WeekData> calendarTableView) {

        LocalDate currDate =  date.minusDays(date.getDayOfMonth() - 1);
        int firstDayAsInt = currDate.getDayOfWeek().getValue();
        int dayOfMonthCounter = 1;
        int daysInMonth = CalendarModel.getDaysInMonth().get(date.getMonth().toString());

        for(int row = 0; row < 6; row++){
            WeekData week = new WeekData();
            for(int col = 0; col < 7; col++){
                if(row == 0 && col >= firstDayAsInt){
                    DayData day = new DayData(currDate);
                    week.setDay(col, day);
                    week.addDay(day);
                    currDate = currDate.plusDays(1);
                    dayOfMonthCounter++;
                } else if(row > 0 && dayOfMonthCounter <= daysInMonth){
                    DayData day = new DayData(currDate);
                    week.setDay(col, day);
                    week.addDay(day);
                    currDate = currDate.plusDays(1);
                    dayOfMonthCounter++;
                }

            }
            calendar.addWeek(week);
            calendarTableView.getItems().add(week);
        }
    }

}



