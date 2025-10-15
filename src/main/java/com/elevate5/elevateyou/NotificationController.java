package com.elevate5.elevateyou;

import com.elevate5.elevateyou.model.NotificationModel;
import com.elevate5.elevateyou.service.NotificationService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Notification dropdown controller (no scroll, no navigation).
 * - Bell toggles the dropdown (VBox)
 * - Badge shows total count
 * - Renders "Title: Body" lines
 */
public class NotificationController {

    @FXML private Button bellButton;
    @FXML private Label badgeLabel;
    @FXML private VBox dropdownList;

    private NotificationService service;

    /** Injected by DashboardController after FXML load. */
    public void setService(NotificationService service) {
        this.service = service;
        refreshBadge();
    }

    /** Toggle dropdown; refresh on open. */
    @FXML
    private void onBellClicked() {
        boolean show = !dropdownList.isVisible();
        dropdownList.setVisible(show);
        dropdownList.setManaged(show);
        if (show) {
            refreshList();
        }
    }

    /** Badge shows total count; hidden when zero. */
    private void refreshBadge() {
        if (service == null) return;
        List<NotificationModel> items = service.latest(200);
        int count = items.size();
        if (count <= 0) {
            badgeLabel.setText("");
            badgeLabel.setVisible(false);
            badgeLabel.setManaged(false);
        } else {
            badgeLabel.setText(String.valueOf(count));
            badgeLabel.setVisible(true);
            badgeLabel.setManaged(true);
        }
    }

    /** Render all items (no scroll, so keep list small in service.latest). */
    private void refreshList() {
        dropdownList.getChildren().clear();
        List<NotificationModel> items = service.latest(50);

        if (items.isEmpty()) {
            dropdownList.getChildren().add(new Label("No notifications found"));
            return;
        }

        for (NotificationModel n : items) {
            Label row = new Label(n.title + ": " + n.body); // e.g., "Appointment: You have Dentist at 2025-10-21 09:00"
            row.setWrapText(true);
            row.getStyleClass().add("notif-item"); // hook for CSS
            dropdownList.getChildren().add(row);
        }
    }
}