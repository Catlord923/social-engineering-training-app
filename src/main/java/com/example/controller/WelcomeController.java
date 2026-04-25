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
 * Controller for the initial welcome screen.
 *
 * <p>Displays introductory training information and starts the learning
 * sequence when the user chooses to continue.</p>
 */
public class WelcomeController {

    @FXML
    private VBox bodyContainer;

    /**
     * Static welcome screen paragraphs displayed in order.
     */
    private static final String[] PARAGRAPHS = {
            "Many cyber attacks succeed not because of technical weaknesses, but because attackers manipulate people through trust, urgency, fear, or curiosity.",
            "This application will help you recognise common threats, understand warning signs, and make safer decisions online.",
            "You will complete three stages:",
            "1. Learn the key concepts\n2. Practice with realistic scenarios\n3. Test your knowledge"
    };

    /**
     * Initializes the welcome screen after the FXML file is loaded.
     *
     * <p>Paragraph labels are created dynamically so formatting remains easier
     * to maintain than storing large styled text directly in FXML.</p>
     */
    @FXML
    public void initialize() {
        for (int i = 0; i < PARAGRAPHS.length; i++) {
            bodyContainer.getChildren().add(makeParagraph(PARAGRAPHS[i], i == 2));
        }
    }

    /**
     * Builds a styled paragraph label for the welcome screen body.
     *
     * @param text paragraph text
     * @param isIntroToList {@code true} when the paragraph introduces the
     *                      numbered list and should use emphasis styling
     * @return formatted paragraph label
     */
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

    /**
     * Opens the theory module when the Start button is pressed.
     *
     * @param event button click event
     * @throws Exception if the next screen cannot be loaded
     */
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
