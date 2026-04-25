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
 * Controller for reusable transition screens shown between major modules.
 *
 * <p>This screen is used to introduce the next application section,
 * display short instructional text, and navigate to the next FXML view.</p>
 */
public class TransitionController {

    @FXML private Label titleLabel;
    @FXML private VBox bodyContainer;
    @FXML private Button continueButton;

    /**
     * FXML resource path to be loaded when the continue button is pressed.
     */
    private String targetFxml;

    /**
     * Configures the transition screen content and destination view.
     *
     * @param title heading text displayed at the top of the screen
     * @param bodyText explanatory body text; blank lines create paragraphs
     * @param buttonText text shown on the continue button
     * @param targetFxml FXML file to open when continuing
     */
    public void configure(String title, String bodyText, String buttonText, String targetFxml) {
        this.targetFxml = targetFxml;
        titleLabel.setText(title);
        continueButton.setText(buttonText);

        bodyContainer.getChildren().clear();

        if (bodyText == null || bodyText.isBlank()) return;

        String cleanedText = cleanText(bodyText);
        // Split text into paragraphs using blank lines as separators
        String[] paragraphs = cleanedText.split("\\n\\s*\\n");

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (!trimmed.isEmpty()) {
                bodyContainer.getChildren().add(makeParagraph(trimmed));
            }
        }
    }

    /**
     * Loads the configured destination screen when the continue button is pressed.
     *
     * @param event button click event
     * @throws Exception if the target FXML cannot be loaded
     */
    @FXML
    private void handleContinueButton(ActionEvent event) throws Exception {
        if (targetFxml == null || targetFxml.isBlank()) {
            throw new IllegalStateException(
                    "Transition target not configured."
            );
        }
        Parent root = FXMLLoader.load(getClass().getResource(targetFxml));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Normalizes line endings and spacing in body text.
     *
     * @param text raw body text
     * @return cleaned text ready for rendering
     */
    private String cleanText(String text) {
        return text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    /**
     * Creates a styled paragraph label for the transition body area.
     *
     * @param text paragraph content
     * @return formatted JavaFX label
     */
    private Label makeParagraph(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        label.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: #94a3b8;" +
                        "-fx-line-spacing: 6px;" +
                        "-fx-text-alignment: center;"
        );
        return label;
    }
}
