module com.example.appl {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;

    requires com.almasb.fxgl.all;

    opens com.example.appl to javafx.fxml;
    exports com.example.appl;
    exports com.example.appl.controllers;
    opens com.example.appl.controllers to javafx.fxml;
    exports com.example.appl.classes;
    opens com.example.appl.classes to javafx.fxml;
}