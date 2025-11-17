package com.elevate5.elevateyou;

import com.elevate5.elevateyou.dao.LiveChatDao;
import com.elevate5.elevateyou.model.LiveChatMessageModel;
import com.elevate5.elevateyou.service.LiveChatService;
import com.elevate5.elevateyou.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class LiveChatController {
    @FXML public Label welcometext;
    @FXML private Button appointmentsButton;
    @FXML private Button calendarButton;
    @FXML private Button chatButton;
    @FXML private Button dashButton;
    @FXML private Button exerciseButton;
    @FXML private Button foodButton;
    @FXML private Button friendsButton;
    @FXML private Button journalButton;
    @FXML private Button logoutButton;
    @FXML private Button medButton;
    @FXML private Button profileButton;
    @FXML private Button reviewsButton;
    @FXML private Button sleepButton;
    @FXML private Button tutorialsButton;

    @FXML private ListView<LiveChatMessageModel> messagesList;
    @FXML private TextField userInput;
    @FXML private Button sendButton;
    @FXML private Button clearButton;
    private LiveChatService chatService;

    @FXML
    private void initialize() {
        String uid  = SessionManager.getSession() != null ? SessionManager.getSession().getUserID() : null;
        chatService = new LiveChatService(uid, new LiveChatDao());
        messagesList.getItems().setAll(chatService.getSession().getRecent(Integer.MAX_VALUE));
        if (!messagesList.getItems().isEmpty()) {
            messagesList.scrollTo(messagesList.getItems().size() - 1);
        }

        messagesList.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(LiveChatMessageModel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }

                Label bubble = new Label((item.isUser() ? "You: " : "AI: ") + item.getContent());
                bubble.setWrapText(true);
                bubble.setMaxWidth(500);
                bubble.setStyle("-fx-background-color: "
                        + (item.isUser() ? "#d1f1ff" : "#f2f2f2")
                        + "; -fx-padding: 8 12; -fx-background-radius: 10;");

                HBox box = new HBox(bubble);
                box.setStyle("-fx-padding: 5; -fx-alignment: "
                        + (item.isUser() ? "CENTER_RIGHT" : "CENTER_LEFT") + ";");

                setGraphic(box);
            }
        });

        sendButton.setOnAction(e -> onSend());


        userInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) onSend();
        });
    }

    @FXML
    private void onSend() {
        String text = userInput.getText();
        if (text == null || text.isBlank()) return;

        messagesList.getItems().add(LiveChatMessageModel.user(text));
        messagesList.scrollTo(messagesList.getItems().size() - 1);
        userInput.clear();

        setBusy(true);

        chatService.chat(text)
                .thenAccept(reply -> Platform.runLater(() -> {
                    messagesList.getItems().add(LiveChatMessageModel.assistant(reply));
                    messagesList.scrollTo(messagesList.getItems().size() - 1);
                    setBusy(false);
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        new Alert(Alert.AlertType.ERROR,
                                "LiveChat failed: " +
                                        (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()),
                                ButtonType.OK).showAndWait();
                        setBusy(false);
                    });
                    return null;
                });
    }

    @FXML
    private void onClear() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Chat History");
        confirm.setHeaderText("Are you sure you want to delete all chat history?");
        confirm.setContentText("This action cannot be reversed.");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }

        setBusy(true);
        messagesList.getItems().clear();

        chatService.deleteHistoryHardField()
                .whenComplete((v, ex) -> Platform.runLater(() -> {
                    setBusy(false);
                    if (ex != null) {
                        new Alert(Alert.AlertType.ERROR,
                                "Failed to delete history: " +
                                        (ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage()))
                                .showAndWait();
                    }
                }));
    }

    private void setBusy(boolean busy) {
        sendButton.setDisable(busy);
        userInput.setDisable(busy);
    }
    @FXML
    private void dashboardButtonClick() {
        try {
            Stage stage = (Stage) dashButton.getScene().getWindow();
            Dashboard.loadDashboardScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void medicationButtonClick() {
        try {
            Stage stage = (Stage) medButton.getScene().getWindow();
            Medication.loadMedTrackerScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void foodButtonClick() {
        try {
            Stage stage = (Stage) foodButton.getScene().getWindow();
            CaloriesWaterIntake.loadCaloriesWaterIntakeScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void calendarButtonClick() {
        try {
            Stage stage = (Stage) calendarButton.getScene().getWindow();
            com.elevate5.elevateyou.view.CalendarView.loadCalendarScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void journalButtonClick() {
        try {
            Stage stage = (Stage) journalButton.getScene().getWindow();
            JournalEntry.loadJournalScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void appointmentButtonClick() {
        try {
            Stage stage = (Stage) appointmentsButton.getScene().getWindow();
            com.elevate5.elevateyou.view.AppointmentView.loadAppointmentScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void sleepButtonClick() {
        try {
            Stage stage = (Stage) sleepButton.getScene().getWindow();
            Sleep.loadSleepScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @FXML
    private void logoutUser() {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            UserLogin.loadUserLoginScene(stage);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}