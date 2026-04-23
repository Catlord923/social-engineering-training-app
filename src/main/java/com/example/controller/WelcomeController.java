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

    private static final String[] PARAGRAPHS = {
            "Many cyber attacks succeed not because of technical weaknesses, but because attackers manipulate people through trust, urgency, fear, or curiosity.",
            "This application will help you recognise common threats, understand warning signs, and make safer decisions online.",
            "You will complete three stages:",
            "1. Learn the key concepts\n2. Practice with realistic scenarios\n3. Test your knowledge"
    };

    @FXML
    public void initialize() {
        for (int i = 0; i < PARAGRAPHS.length; i++) {
            bodyContainer.getChildren().add(makeParagraph(PARAGRAPHS[i], i == 2));
        }
    }

    private Label makeParagraph(String text, boolean isIntroToList) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.TOP_LEFT);

        if (isIntroToList) {
            label.setStyle(
                    "-fx-font-size: 20px;" +
                            "-fx-text-fill: #94a3b8;" +
                            "-fx-line-spacing: 5px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-padding: 10 0 0 0;"
            );
        } else {
            label.setStyle(
                    "-fx-font-size: 20px;" +
                            "-fx-text-fill: #cbd5e1;" +
                            "-fx-line-spacing: 7px;"
            );
        }
        return label;
    }

    @FXML
    private void handleStartButton(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/view/TheoryScreen.fxml")
        );
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
