package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.model.User;
import com.elevate5.elevateyou.service.HealthSummaryService;
import com.elevate5.elevateyou.service.ShareService;
import com.elevate5.elevateyou.session.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ProfileView {

    @FXML
    private TextArea profileBio;

    @FXML
    private ImageView profilePicView;

    @FXML
    private Label friendEmailLabel;

    @FXML
    private Label friendLastNutritionLabel;

    @FXML
    private Label friendLastWaterLabel;

    @FXML
    private Label friendLastSleepLabel;

    @FXML
    private CheckBox shareHealthData;


    private User user;
    private String myUid;

    private final ShareService shareService = new ShareService();
    private final HealthSummaryService healthSummaryService = new HealthSummaryService();


    @FXML
    private void initialize() {
        if (SessionManager.getSession() != null &&
                SessionManager.getSession().getUser() != null) {

            myUid = SessionManager.getSession().getUser().getUid();
        } else {
            myUid = null;
        }

        if (shareHealthData != null && myUid == null) {
            shareHealthData.setDisable(true);
        }
    }


    public void setUser(User user) {
        this.user = user;

        Image profilePic = new Image(user.getProfileImageURL());
        profilePicView.setImage(profilePic);
        profileBio.setText(user.getUserBio());

        if (friendEmailLabel != null) {
            friendEmailLabel.setText(user.getEmail());
        }

        if (myUid == null) return;

        final String ownerUid = myUid;
        final String friendUid = user.getUserID();

        if (shareHealthData != null) {
            shareHealthData.setDisable(false);

            shareService.isSharing(ownerUid, friendUid)
                    .thenAccept(isSharing ->
                            Platform.runLater(() -> shareHealthData.setSelected(isSharing))
                    )
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });

            shareHealthData.setOnAction(e -> {
                boolean enabled = shareHealthData.isSelected();
                shareService.setSharing(ownerUid, friendUid, enabled)
                        .exceptionally(ex -> {
                            ex.printStackTrace();
                            Platform.runLater(() -> {
                                shareHealthData.setSelected(!enabled); // 回滚
                                new Alert(Alert.AlertType.ERROR,
                                        "Failed to update sharing setting:\n" + ex.getMessage()
                                ).showAndWait();
                            });
                            return null;
                        });
            });
        }

        shareService.isSharing(friendUid, ownerUid)
                .thenAccept(isSharing -> {
                    if (!isSharing) {
                        Platform.runLater(() -> {
                            friendLastNutritionLabel.setText("This friend is not sharing health data with you.");
                            friendLastWaterLabel.setText("");
                            friendLastSleepLabel.setText("");
                        });
                    } else {
                        loadFriendHealthSummary(friendUid);
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }


    private void loadFriendHealthSummary(String friendUid) {

        // Nutrition (Calories Intake)
        healthSummaryService.getTodayNutritionSummary(friendUid)
                .thenAccept(val -> Platform.runLater(() ->
                        friendLastNutritionLabel.setText("Calories Intake today: " + val)
                ))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });

        // Water
        healthSummaryService.getTodayWaterSummary(friendUid)
                .thenAccept(val -> Platform.runLater(() ->
                        friendLastWaterLabel.setText("Water Intake today: " + val)
                ))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });

        // Sleep Score
        healthSummaryService.getLastNightSleepSummary(friendUid)
                .thenAccept(val -> Platform.runLater(() ->
                        friendLastSleepLabel.setText("Sleep Score last night: " + val)
                ))
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }
}