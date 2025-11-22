package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.App;
import com.elevate5.elevateyou.Dashboard;
import com.elevate5.elevateyou.UserLogin;
import com.elevate5.elevateyou.model.CalendarModel;
import com.elevate5.elevateyou.model.Event;
import com.elevate5.elevateyou.model.EventManager;
import com.elevate5.elevateyou.model.calendardata.DayData;
import com.elevate5.elevateyou.model.calendardata.WeekData;
import com.elevate5.elevateyou.session.Session;
import com.elevate5.elevateyou.session.SessionManager;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.WriteResult;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.application.Application;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarView extends Application {


    private final LocalDate today = LocalDate.now();

    private LocalDate selectedDate;

    private final static CalendarModel calendar = new CalendarModel();

    private final EventManager eventManager = new EventManager();

    @FXML
    private Label monthLabel;

    @FXML
    private Button decrementMonth;

    @FXML
    private Button incrementMonth;

    @FXML
    private Button backToDashboardButton;

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

    private final ArrayList<TableColumn<WeekData,DayData>> columns = new ArrayList<>();

    private Session session;

    @FXML
    private void initialize(){

        sunCol.setCellValueFactory(new PropertyValueFactory<>("sun"));
        monCol.setCellValueFactory(new PropertyValueFactory<>("mon"));
        tueCol.setCellValueFactory(new PropertyValueFactory<>("tue"));
        wedCol.setCellValueFactory(new PropertyValueFactory<>("wed"));
        thuCol.setCellValueFactory(new PropertyValueFactory<>("thu"));
        friCol.setCellValueFactory(new PropertyValueFactory<>("fri"));
        satCol.setCellValueFactory(new PropertyValueFactory<>("sat"));

        columns.add(sunCol);
        columns.add(monCol);
        columns.add(tueCol);
        columns.add(wedCol);
        columns.add(thuCol);
        columns.add(friCol);
        columns.add(satCol);


        calendarTableView.getSelectionModel().setCellSelectionEnabled(true);
        calendarTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        calendarTableView.setFixedCellSize(98);

        selectedDate = today;

        monthLabel.setText(selectedDate.getMonth().toString() + ",  " + selectedDate.getYear());

        populateCalendar(selectedDate, calendarTableView);

        //selection = calendarTableView.getSelectionModel().getSelectedItems();

        if(SessionManager.getSession() != null){
            session = SessionManager.getSession();
        }


        if(session != null) {

            eventManager.setEvents(session.getUserEventManager().getEvents());


        } else{
            /*
            LocalDate sampleDate = LocalDate.of(2025,10,7);
            LocalTime sampleTime = LocalTime.of(16,30);
            Event sampleEvent = new Event(sampleDate.toString(), sampleTime.toString(), "Dr Appointment", "Appointment");
            Event sample2 = new Event(LocalDate.of(2025, 10, 7).toString(), LocalTime.of(15, 00).toString(), "Take Meds", "Advil 20mg");
            eventManager.addEvent(sampleEvent.getDate().toString(), sampleEvent);
            eventManager.addEvent(sample2.getDate().toString(), sample2);
            Collections.sort(eventManager.getEvents().get(sampleEvent.getDate().toString()), (e1, e2) -> e1.getTime().compareTo(e2.getTime()));
            Event sample3 = new Event(LocalDate.of(2025, 11, 15).toString(), LocalTime.of(12, 00).toString(), "Workout", "Back and Biceps");
            eventManager.addEvent(sample3.getDate().toString(), sample3);

             */
        }



        //sets cell behavior within the calendar table
        for(TableColumn<WeekData, DayData> dayCol : columns){
            dayCol.setCellFactory(col -> {
                TableCell<WeekData, DayData> cell = new TableCell<WeekData, DayData>() {
                    @Override
                    protected void updateItem(DayData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            VBox vbox = new VBox();
                            if (item.getDate() != null) {
                                Label dowLabel = new Label(item.getDate().getDayOfMonth() + "");

                                vbox.getChildren().add(dowLabel);
                                //checks if there are events for the date in the cell and displays them
                                if(eventManager.getEvents() != null && eventManager.getEvents().containsKey(item.getDate().toString())) {
                                    //System.out.println("Events found at: " + item.getDate().toString());
                                    //System.out.println(eventManager.getEvents().get(item.getDate().toString()).getClass());
                                    ArrayList<Event> events = (ArrayList<Event>) eventManager.getEvents().get(item.getDate().toString());
                                    for(Event event : events) {
                                        //dayOfMonth = dayOfMonth + event.toString() + "\n";
                                        Button eventButton = new Button(event.toString());
                                        eventButton.setMaxWidth(Double.MAX_VALUE);
                                        eventButton.setAlignment(Pos.CENTER_LEFT);
                                        vbox.getChildren().add(eventButton);
                                        //button representing each event, opens a popup with event info populated
                                        eventButton.setOnAction(e -> {
                                            System.out.println("Selected event: " + event.toString());
                                            Popup popup = new Popup();
                                            if(!popup.isShowing()){
                                                popup.setAutoHide(true);

                                                VBox popBox = new VBox();
                                                popBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                                                Label popLabel = new Label("New Event");
                                                TextField eventName = new TextField();
                                                eventName.setText(event.getEventName());
                                                eventName.setPromptText("Event Name...");
                                                DatePicker eventDatePicker = new DatePicker();
                                                eventDatePicker.setValue(LocalDate.parse(event.getDate()));
                                                ComboBox<String> hourBox = new ComboBox<>();


                                                ComboBox<String> minuteBox = new ComboBox<>();
                                                if(LocalTime.parse(event.getTime()).getMinute() == 0){
                                                    minuteBox.setValue(LocalTime.parse(event.getTime()).getMinute() + "0");
                                                } else if(LocalTime.parse(event.getTime()).getMinute() < 10){
                                                    minuteBox.setValue("0" + LocalTime.parse(event.getTime()).getMinute());
                                                } else{
                                                    minuteBox.setValue(LocalTime.parse(event.getTime()).getMinute() + "");
                                                }

                                                ComboBox<String> AMorPMBox = new ComboBox<>();
                                                if(LocalTime.parse(event.getTime()).getHour() > 11){
                                                    AMorPMBox.setValue("PM");
                                                    hourBox.setValue(LocalTime.parse(event.getTime()).getHour() - 12 + "");
                                                } else{
                                                    AMorPMBox.setValue("AM");
                                                    hourBox.setValue(LocalTime.parse(event.getTime()).getHour() + "");
                                                }
                                                HBox timeBox = new HBox();
                                                TextArea eventDescription = new TextArea();
                                                eventDescription.setText(event.getEventDescription());
                                                eventDescription.setPromptText("Event Description...");
                                                Button updateEventButton = new Button("Update Event");
                                                Button cancelEventButton = new Button("Cancel");
                                                Button deleteEventButton = new Button("Delete");
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
                                                //button for updating event info
                                                updateEventButton.setOnAction(popEvent -> {
                                                    try{
                                                        LocalDate eventDate = eventDatePicker.getValue();
                                                        if(eventDate.getYear() < LocalDate.now().getYear()) {
                                                            eventDate = eventDate.withYear(2025);
                                                        }
                                                        LocalDate oldDate = LocalDate.parse(event.getDate());
                                                        String name = eventName.getText();
                                                        String hour = hourBox.getValue();
                                                        String minute = minuteBox.getValue();

                                                        if (AMorPMBox.getValue().equals("PM") && Integer.parseInt(hour) < 12) {
                                                            hour = Integer.parseInt(hour) + 12 + "";
                                                        }
                                                        if(AMorPMBox.getValue().equals("AM") && Integer.parseInt(hour) == 12) {
                                                            hour = "00";
                                                        }
                                                        if (hour.length() == 1) {
                                                            hour = "0" + hour;
                                                        }
                                                        if(minute.length() == 1){
                                                            minute = "0" +  minute;
                                                        }
                                                        String time = hour + ":" + minute;
                                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                                        LocalTime eventTime = LocalTime.parse(time, formatter);
                                                        String description = eventDescription.getText();
                                                        //System.out.println(name + "\n" + item.getDate() + "\n" + time + "\n" + description);
                                                        event.setDate(eventDate);
                                                        event.setTime(eventTime);
                                                        event.setEventName(name);
                                                        event.setEventDescription(description);
                                                        eventManager.getEvents().get(oldDate.toString()).remove(event);
                                                        eventManager.addEvent(event.getDate(), event);
                                                        Collections.sort(eventManager.getEvents().get(event.getDate()), (e1, e2) -> e1.getTime().compareTo(e2.getTime()));
                                                        if(session != null) {
                                                            if (!session.getUserEventManager().getEvents().isEmpty()) {
                                                                try {
                                                                    updateFirestore(eventManager, event, session);
                                                                } catch (IOException ex) {
                                                                    throw new RuntimeException(ex);
                                                                }
                                                            }
                                                        }
                                                        calendarTableView.refresh();
                                                        popup.hide();
                                                    } catch (Exception ex) {
                                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                                        alert.setTitle("Error");
                                                        alert.setHeaderText("Error");
                                                        alert.setContentText("Invalid Date or Time");
                                                        alert.showAndWait();
                                                    }

                                                });
                                                //button for canceling popup
                                                cancelEventButton.setOnAction(popEvent -> {
                                                    popup.hide();
                                                });
                                                //button for deleting event
                                                deleteEventButton.setOnAction(popEvent -> {
                                                    Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
                                                    deleteAlert.setTitle("Confirm Delete Event");
                                                    deleteAlert.setHeaderText("Delete Event");
                                                    deleteAlert.setContentText("Are you sure you want to delete this event? " + event.toString());
                                                    if(deleteAlert.showAndWait().get() == ButtonType.OK){
                                                        eventManager.getEvents().get(event.getDate()).remove(event);
                                                        if(session != null) {
                                                            if (!session.getUserEventManager().getEvents().isEmpty()) {
                                                                try {
                                                                    updateFirestore(eventManager, event, session);
                                                                } catch (IOException ex) {
                                                                    throw new RuntimeException(ex);
                                                                }
                                                            }
                                                        }
                                                        calendarTableView.refresh();
                                                        popup.hide();
                                                    }else{
                                                        popup.show(calendarTableView.getScene().getWindow());
                                                    }

                                                });

                                                buttonBox.setSpacing(10);
                                                buttonBox.getChildren().addAll(updateEventButton, cancelEventButton, deleteEventButton);

                                                popBox.getChildren().addAll(popLabel, eventName, eventDatePicker, timeBox, eventDescription, buttonBox);
                                                popBox.setSpacing(5);

                                                popLabel.setAlignment(Pos.TOP_LEFT);
                                                popLabel.setStyle("-fx-background-color: lightgray; -fx-padding: 10px;");
                                                popLabel.setPrefSize(200, 20);

                                                popup.getContent().add(popBox);

                                                popup.show(calendarTableView.getScene().getWindow());
                                            }

                                        });


                                    }
                                }
                            }
                            vbox.setFillWidth(true);
                            setGraphic(vbox);
                            setStyle("-fx-alignment: TOP-LEFT;");
                        }
                    }
                };

                //cell functionality when clicking on empty space within a date
                cell.setOnMouseClicked(event -> {
                    Popup popup = new Popup();
                    //cell.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
                    if(cell.getItem() != null && !popup.isShowing()) {
                        popup.setAutoHide(true);

                        VBox popBox = new VBox();
                        popBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                        Label popLabel = new Label("New Event");
                        TextField eventName = new TextField();
                        eventName.setPromptText("Event Name...");
                        DatePicker eventDatePicker = new DatePicker();
                        eventDatePicker.setValue(cell.getItem().getDate());
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

                        //button functionality for adding a new event
                        addEventButton.setOnAction(popEvent -> {
                            try {
                                LocalDate eventDate = eventDatePicker.getValue();
                                if(eventDate.getYear() < LocalDate.now().getYear()) {
                                    eventDate = eventDate.withYear(2025);
                                }
                                String name = eventName.getText();
                                String hour = hourBox.getValue();
                                String minute = minuteBox.getValue();
                                if (AMorPMBox.getValue().equals("PM") && Integer.parseInt(hour) < 12) {
                                    hour = Integer.parseInt(hour) + 12 + "";
                                }
                                if (AMorPMBox.getValue().equals("AM") && Integer.parseInt(hour) == 12) {
                                    hour = "00";
                                }
                                if (hour.length() == 1) {
                                    hour = "0" + hour;
                                }
                                if (minute.length() == 1) {
                                    minute = "0" + minute;
                                }
                                String time = hour + ":" + minute;
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                LocalTime eventTime = LocalTime.parse(time, formatter);
                                String description = eventDescription.getText();
                                System.out.println(name + "\n" + cell.getItem().getDate() + "\n" + time + "\n" + description);
                                Event newEvent = new Event(eventDate.toString(), eventTime.toString(), name, description);
                                eventManager.addEvent(newEvent.getDate(), newEvent);
                                System.out.println(eventManager.getEvents().keySet());
                                Collections.sort(eventManager.getEvents().get(newEvent.getDate()), (e1, e2) -> e1.getTime().compareTo(e2.getTime()));
                                if (session != null) {
                                    if (eventManager.getEvents() != null) {
                                        try {
                                            updateFirestore(eventManager, newEvent, session);
                                        } catch (IOException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                    }
                                }
                                calendarTableView.refresh();
                                popup.hide();
                            }catch (Exception ex) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setHeaderText("Error");
                                alert.setContentText(ex.getMessage() + "Invalid Date or Time");
                                alert.showAndWait();
                            }
                        });

                        //button functionality for canceling action
                        cancelEventButton.setOnAction(popEvent -> {
                            popup.hide();
                        });

                        buttonBox.setSpacing(10);
                        buttonBox.getChildren().addAll(addEventButton, cancelEventButton);

                        popBox.getChildren().addAll(popLabel, eventName, eventDatePicker, timeBox, eventDescription, buttonBox);
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

    //decreases the month and recreates the calendar
    @FXML
    private void handleDecrementMonth(ActionEvent event) {
        selectedDate = selectedDate.minusMonths(1);
        monthLabel.setText(selectedDate.getMonth().toString() + ",  " + selectedDate.getYear());
        calendarTableView.getItems().clear();
        calendarTableView.refresh();
        populateCalendar(selectedDate, calendarTableView);
    }

    //increases the month and recreates the calendar
    @FXML
    private void handleIncrementMonth(ActionEvent event) {
        selectedDate = selectedDate.plusMonths(1);
        monthLabel.setText(selectedDate.getMonth().toString() + ",  " + selectedDate.getYear());
        calendarTableView.getItems().clear();
        calendarTableView.refresh();
        populateCalendar(selectedDate, calendarTableView);
    }

    //creates the calendar based on the current month/year
    @FXML
    private void populateCalendar(LocalDate date, TableView<WeekData> calendarTableView) {

        LocalDate currDate =  date.minusDays(date.getDayOfMonth() - 1);
        int firstDayAsInt = currDate.getDayOfWeek().getValue();
        int dayOfMonthCounter = 1;
        int daysInMonth = CalendarModel.getDaysInMonth().get(date.getMonth().toString());
        if(date.getYear() % 4 == 0 && date.getMonth().toString().equals("FEBRUARY")) {
            daysInMonth++;
        }

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
            //calendar.addWeek(week);
            calendarTableView.getItems().add(week);
        }
    }

    //button for going back to the app dashboard
    @FXML
    private void backToDashboard(ActionEvent event) {
        try {
            Stage stage = (Stage) backToDashboardButton.getScene().getWindow();
            Dashboard.loadDashboardScene(stage);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void loadCalendarScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("CalendarView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Calendar");
        stage.setScene(scene);
        stage.show();
    }

    public static void updateFirestore(EventManager eventManager, Event newEvent, Session session) throws IOException {
        DocumentReference eventDocRef = App.fstore.collection("Events").document(session.getUserID());
        //Map<String, Object> eventData = new HashMap<>();
        //eventData.put(newEvent.getDate(), newEvent);
        try{
            ApiFuture<WriteResult> result = eventDocRef.set(eventManager);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "!!!");
            e.printStackTrace();
        }
    }




}



