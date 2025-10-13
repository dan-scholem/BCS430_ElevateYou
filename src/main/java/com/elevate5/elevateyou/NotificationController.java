package com.elevate5.elevateyou;

import com.elevate5.elevateyou.service.NotificationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public class NotificationController {
    @FXML
    private VBox dropdownList;
    @FXML
    private Label badgeLabel;
    @FXML
    private Button bellButton;
    @FXML
    private Button markAllButton;

    private void refreshBadge () {
        int count = nS.unreadCount();
        badgeLabel.setText(count > 0 ? String.valueOf(count) : "");
    }
    private void refreshList () {
        dropdownList.getChildren().clear();

        var items = nS.latest(20);
        if (items.isEmpty()) {
            dropdownList.getChildren().add(new Label("No notifications found"));
            return;
        }

        for (var it: items) {
            Label line = new Label(
                    (it.read ? "[Read] " : "[Unread] ") + it.title + " - " + it.body
            );
            line.setWrapText(true);
            line.setOnMouseClicked(event -> {
                nS.markOneRead(it.userId);
                refreshBadge();
                refreshList();
            });
            dropdownList.getChildren().add(line);
        }
        if (!dropdownList.getChildren().isEmpty()) {
            dropdownList.getChildren().add(markAllButton);
        }
    }
    private final NotificationService nS= new NotificationService();
    public void initialize() {
        refreshBadge();
    }

    @FXML
    private void onBellClicked() {
        boolean show = !dropdownList.isVisible();
        dropdownList.setVisible(show);
        dropdownList.setManaged(show);
        if (show) {
            refreshList();
        }
    }

    @FXML
    private void onMarkAllClicked() {
        nS.markAllRead();
        refreshList();
        refreshBadge();
    }
}
