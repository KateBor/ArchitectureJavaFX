module com.example.architecture {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires lombok;
    requires org.slf4j;

    opens com.example.architecture to javafx.fxml;
    exports com.example.architecture;
    exports com.example.architecture.controllers;
    exports com.example.architecture.model;
    opens com.example.architecture.controllers to javafx.fxml;
}