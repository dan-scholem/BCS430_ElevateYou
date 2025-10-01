module com.elevate5.elevateyou {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires jbcrypt;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    requires google.cloud.firestore;
    requires com.google.common;
    requires com.google.auth.oauth2;
    requires com.google.api.apicommon;
    requires firebase.admin;
    requires google.cloud.core;
    requires com.google.auth;

    opens com.elevate5.elevateyou to javafx.fxml;
    exports com.elevate5.elevateyou;
    opens com.elevate5.elevateyou.view to javafx.fxml;
    exports com.elevate5.elevateyou.view;

}
