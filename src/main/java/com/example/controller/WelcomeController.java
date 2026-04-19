package com.example.controller;

import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.fxml.FXML;

/**
 * Controller for the initial Welcome screen.
 * Displays introductory text and handles the transition to the main application modules.
 */
public class WelcomeController {

    @FXML
    private VBox bodyContainer;

    // Hardcoded introduction text.
    // Static content is kept here rather than a database for simpler initial loading.
    private static final String[] PARAGRAPHS = {
            "Welcome to this interactive cybersecurity training application.",
            "Many cyber attacks succeed not because of technical weaknesses, but because attackers manipulate people through trust, urgency, fear, or curiosity.",
            "This application will help you recognise common threats, understand warning signs, and make safer decisions online.",
            "You will complete three stages:",
            "1. Learn the key concepts\n2. Practice with realistic scenarios\n3. Test your knowledge",
            "Press Start to begin."
    };

    /**
     * Populates the welcome screen with text labels during initialization.
     */
    @FXML
    public void initialize() {
        for (String text : PARAGRAPHS) {
            bodyContainer.getChildren().add(makeParagraph(text));
        }
    }

    /**
     * UI builder method to create stylized labels for the intro text.
     * @param text The string to display in the label.
     * @return A configured Label ready for the VBox.
     */
    private Label makeParagraph(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.TOP_LEFT);
        label.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-text-fill: #2b2b2b;" +
                        "-fx-line-spacing: 6px;"
        );
        return label;
    }

    /**
     * Switches the scene from the WelcomeScreen to the Theory module.
     * @param event The ActionEvent triggered by the Start button.
     * @throws Exception If the FXML file cannot be found or loaded.
     */
    @FXML
    private void handleStartButton(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/view/TheoryScreen.fxml")
        );

        // Retrieve the current Stage from the button click event
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Swap the scene content
        stage.setScene(new Scene(root));
        stage.show();
    }
}
