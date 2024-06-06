module com.chat.viewer.chatviewer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.databind;
    requires org.junit.jupiter.api;
    requires junit;

    opens com.chat.viewer.chatviewer to javafx.fxml;
    opens com.chat.viewer.chatviewer.controller to javafx.fxml;
    opens com.chat.viewer.chatviewer.model to com.fasterxml.jackson.databind;
    exports com.chat.viewer.chatviewer.model;
    exports com.chat.viewer.chatviewer.controller;
    exports com.chat.viewer.chatviewer;
}