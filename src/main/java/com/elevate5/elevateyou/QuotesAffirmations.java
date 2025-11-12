package com.elevate5.elevateyou;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class QuotesAffirmations {

    public static void loadQuotesAffirmationsScene(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(UserLogin.class.getResource("QuotesAffirmations.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Quotes & Affirmations");
        stage.setScene(scene);
        stage.show();
    }
}
