module com.elevate5.elevateyou {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires jbcrypt;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens com.elevate5.elevateyou to javafx.fxml;
    exports com.elevate5.elevateyou;
    opens com.elevate5.elevateyou.view to javafx.fxml;
    exports com.elevate5.elevateyou.view;

}
