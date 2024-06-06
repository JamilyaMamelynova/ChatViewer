package com.chat.viewer.chatviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

/**
 * The {@code HelloApplication} class serves as the entry point for the chat viewer application.
 * It initializes the JavaFX application, loads the FXML layout, and sets up the primary stage.
 */
public class HelloApplication extends Application {

    /**
     * The main entry point for all JavaFX applications. The start method is called after the
     * JavaFX runtime has been initialized.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main method is ignored in correctly deployed JavaFX application. main() serves as
     * the entry point for standalone Java applications, allowing the application to be launched.
     *
     * @param args the command line arguments
     * @throws FileNotFoundException if the file cannot be found (though not used in this method)
     */
    public static void main(String[] args) throws FileNotFoundException {
        launch();
    }
}
