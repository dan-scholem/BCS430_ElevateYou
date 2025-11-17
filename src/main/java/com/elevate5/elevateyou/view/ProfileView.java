package com.elevate5.elevateyou.view;

import com.elevate5.elevateyou.model.User;
import javafx.fxml.FXML;
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
    private void initialize() {

    }

    public void setUser(User user){
        System.out.println(user.getProfileImageURL() + "; " + user.fullName() + "; " + user.getUserBio());
        Image profilePic = new Image(user.getProfileImageURL());
        profilePicView.setImage(profilePic);
        profileBio.setText(user.getUserBio());
    }


}
