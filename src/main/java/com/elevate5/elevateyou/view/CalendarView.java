package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.UserLogin;
import com.elevate5.elevateyou.model.CalendarModel;
import com.elevate5.elevateyou.model.Event;
import com.elevate5.elevateyou.model.EventManager;
import com.elevate5.elevateyou.model.calendardata.DayData;
import com.elevate5.elevateyou.model.calendardata.WeekData;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.application.Application;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;

public class CalendarView extends Application {


    private final LocalDate today = LocalDate.now();

    private LocalDate selectedDate;

    private final static CalendarModel calendar = new CalendarModel();

    private ObservableList<WeekData> selection;

    private final EventManager eventManager = new EventManager();

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

        LocalDate sampleDate = LocalDate.of(2025,10,7);
        LocalTime sampleTime = LocalTime.of(16,30);

        Event sampleEvent = new Event(sampleDate, sampleTime, "Dr Appointment", "Appointment");

        Event sample2 = new Event(LocalDate.of(2025, 10, 7), LocalTime.of(15, 00), "Take Meds", "Advil 20mg");

        LocalDate target = sampleEvent.getDate();

        eventManager.addEvent(sampleEvent.getDate(), sampleEvent);
        eventManager.addEvent(sample2.getDate(), sample2);
        Collections.sort(eventManager.getEvents().get(sampleEvent.getDate()), (e1, e2) -> e1.getTime().compareTo(e2.getTime()));

        System.out.println(eventManager.getEvents().get(sampleEvent.getDate()).getFirst().getDate().getDayOfWeek().toString());

        Event sample3 = new Event(LocalDate.of(2025, 11, 15), LocalTime.of(12, 00), "Workout", "Back and Biceps");

        eventManager.addEvent(sample3.getDate(), sample3);

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
                if(cell.getItem() != null && eventManager.getEvents().get(cell.getItem().getDate()) != null) {
                    for(Event events : eventManager.getEvents().get(cell.getItem().getDate())) {
                        System.out.println(events.toString());
                    }
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
                            if(eventManager.getEvents().containsKey(item.getDate())) {
                                for(Event event : eventManager.getEvents().get(item.getDate())) {
                                    dayOfMonth = dayOfMonth + event.toString() + "\n";
                                }
                            }
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
                if(cell.getItem() != null && eventManager.getEvents().get(cell.getItem().getDate()) != null) {
                    for(Event events : eventManager.getEvents().get(cell.getItem().getDate())) {
                        System.out.println(events.toString());
                    }
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
                        VBox vbox = new VBox();
                        String dayOfMonth;
                        if (item.getDate() != null) {
                            //dayOfMonth = item.getDate().getDayOfMonth() + "\n";
                            Label dowLabel = new Label(item.getDate().getDayOfMonth() + "");
                            vbox.getChildren().add(dowLabel);
                            if(eventManager.getEvents().containsKey(item.getDate())) {
                                for(Event event : eventManager.getEvents().get(item.getDate())) {
                                    //dayOfMonth = dayOfMonth + event.toString() + "\n";
                                    Button eventButton = new Button(event.toString());
                                    eventButton.setMaxWidth(Double.MAX_VALUE);
                                    eventButton.setAlignment(Pos.CENTER_LEFT);
                                    vbox.getChildren().add(eventButton);
                                }
                            }
                            if(!item.getEvents().isEmpty()) {
                                for(Event event : item.getEvents()) {
                                    //dayOfMonth +=  event.toString() + "\n";
                                }
                            }
                        } else {
                            dayOfMonth = "";
                        }
                        vbox.setFillWidth(true);
                        setGraphic(vbox);
                        //setText(dayOfMonth);
                        setStyle("-fx-alignment: TOP-LEFT;");
                    }
                }
            };
            cell.setOnMouseClicked(event -> {
                Popup popup = new Popup();

                if(cell.getItem() != null && !popup.isShowing()) {
                    popup.setAutoHide(true);

                    VBox popBox = new VBox();
                    Label popLabel = new Label("New Event");
                    TextField eventName = new TextField();
                    eventName.setPromptText("Event Name...");
                    ComboBox<String> hourBox = new ComboBox<>();
                    ComboBox<String> minuteBox = new ComboBox<>();
                    ComboBox<String> AMorPMBox = new ComboBox<>();
                    HBox timeBox = new HBox();
                    TextArea eventDescription = new TextArea();
                    eventDescription.setPromptText("Event Description...");
                    Button addEventButton = new Button("Add Event");
                    Button cancelEventButton = new Button("Cancel");
                    HBox buttonBox = new HBox();

                    hourBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12");
                    minuteBox.getItems().addAll("00", "15", "30", "45");
                    AMorPMBox.getItems().addAll("AM", "PM");
                    hourBox.setEditable(true);
                    hourBox.setPrefWidth(70);
                    minuteBox.setEditable(true);
                    minuteBox.setPrefWidth(70);

                    timeBox.getChildren().addAll(hourBox, minuteBox, AMorPMBox);
                    timeBox.setSpacing(5);

                    addEventButton.setOnAction(popEvent -> {
                        String name = eventName.getText();
                        String hour = hourBox.getValue();
                        String minute = minuteBox.getValue();
                        if (AMorPMBox.getValue().equals("PM")) {
                            hour = (Integer.parseInt(hourBox.getValue()) + 12) + "";
                        }
                        if (hour.length() == 1) {
                            hour = "0" + hour;
                        }
                        String time = hour + ":" + minute;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        LocalTime eventTime = LocalTime.parse(time, formatter);
                        String description = eventDescription.getText();
                        System.out.println(name + "\n" + cell.getItem().getDate() + "\n" + time + "\n" + description);
                        Event newEvent = new Event(cell.getItem().getDate(), eventTime, name, description);
                        eventManager.addEvent(newEvent.getDate(), newEvent);
                        Collections.sort(eventManager.getEvents().get(newEvent.getDate()), (e1, e2) -> e1.getTime().compareTo(e2.getTime()));
                        calendarTableView.refresh();
                        popup.hide();
                    });

                    cancelEventButton.setOnAction(popEvent -> {
                        popup.hide();
                    });

                    buttonBox.setSpacing(10);
                    buttonBox.getChildren().addAll(addEventButton, cancelEventButton);

                    popBox.getChildren().addAll(popLabel, eventName, timeBox, eventDescription, buttonBox);
                    popBox.setSpacing(5);

                    popLabel.setAlignment(Pos.TOP_LEFT);
                    popLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 10px;");
                    popLabel.setPrefSize(200, 20);

                    popup.getContent().add(popBox);

                    popup.show(calendarTableView.getScene().getWindow(), event.getScreenX(), event.getScreenY());
                }
                else{
                    popup.hide();
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
                            if(eventManager.getEvents().containsKey(item.getDate())) {
                                for(Event event : eventManager.getEvents().get(item.getDate())) {
                                    dayOfMonth = dayOfMonth + event.toString() + "\n";
                                }
                            }
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
                if(cell.getItem() != null && eventManager.getEvents().get(cell.getItem().getDate()) != null) {
                    for(Event events : eventManager.getEvents().get(cell.getItem().getDate())) {
                        System.out.println(events.toString());
                    }
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
                            if(eventManager.getEvents().containsKey(item.getDate())) {
                                for(Event event : eventManager.getEvents().get(item.getDate())) {
                                    dayOfMonth = dayOfMonth + event.toString() + "\n";
                                }
                            }
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
                if(cell.getItem() != null && eventManager.getEvents().get(cell.getItem().getDate()) != null) {
                    for(Event events : eventManager.getEvents().get(cell.getItem().getDate())) {
                        System.out.println(events.toString());
                    }
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
                            if(eventManager.getEvents().containsKey(item.getDate())) {
                                for(Event event : eventManager.getEvents().get(item.getDate())) {
                                    dayOfMonth = dayOfMonth + event.toString() + "\n";
                                }
                            }
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
                if(cell.getItem() != null && eventManager.getEvents().get(cell.getItem().getDate()) != null) {
                    for(Event events : eventManager.getEvents().get(cell.getItem().getDate())) {
                        System.out.println(events.toString());
                    }
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
                            if(eventManager.getEvents().containsKey(item.getDate())) {
                                for(Event event : eventManager.getEvents().get(item.getDate())) {
                                    dayOfMonth = dayOfMonth + event.toString() + "\n";
                                }
                            }
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
                if(cell.getItem() != null && eventManager.getEvents().get(cell.getItem().getDate()) != null) {
                    for(Event events : eventManager.getEvents().get(cell.getItem().getDate())) {
                        System.out.println(events.toString());
                    }
                }
            });
            return cell;
        });




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



