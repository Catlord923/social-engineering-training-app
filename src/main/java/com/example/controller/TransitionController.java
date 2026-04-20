package com.example.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.fxml.FXML;

/**
 * Reusable controller for transition screens between major app modules
 * such as Theory -> Scenarios and Scenarios -> Quiz.
 */
public class TransitionController {

    @FXML
    private Label titleLabel;

    @FXML
    private VBox bodyContainer;

    @FXML
    private Button continueButton;

    /**
     * The FXML loader can call initialize automatically.
     * We leave it empty because the screen content is configured externally.
     */
    @FXML
    public void initialize() {
        // Intentionally empty.
    }

    /**
     * Configures the transition screen text dynamically.
     *
     * @param title The main title shown at the top of the screen.
     * @param bodyText The paragraph text shown in the main body area.
     *                 Separate paragraphs with double newlines.
     * @param buttonText The label for the continue button.
     */
    public void configure(String title, String bodyText, String buttonText) {
        titleLabel.setText(title);
        continueButton.setText(buttonText);

        bodyContainer.getChildren().clear();

        if (bodyText == null || bodyText.isBlank()) {
            return;
        }

        String cleanedText = cleanText(bodyText);
        String[] paragraphs = cleanedText.split("\\n\\s*\\n");

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();

            if (!trimmed.isEmpty()) {
                bodyContainer.getChildren().add(makeParagraph(trimmed));
            }
        }
    }

    /**
     * Handles the default continue action.
     * For now this takes the user to the Scenario screen.
     * Later you can make this configurable for Quiz as well.
     */
    @FXML
    private void handleContinueButton(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/view/ScenarioScreen.fxml")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private String cleanText(String text) {
        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

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
}