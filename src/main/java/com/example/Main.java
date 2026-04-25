package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the JavaFX application.
 *
 * <p>Loads the welcome screen and configures the primary application window.</p>
 */
public class Main extends Application {

    /**
     * Starts the JavaFX application and displays the welcome screen.
     *
     * @param stage primary application window
     * @throws Exception if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/view/WelcomeScreen.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 1440, 1080);

        stage.setTitle("Social Engineering Training App");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}