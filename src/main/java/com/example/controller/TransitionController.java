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
 * Reusable controller for transition screens between major app modules.
 */
public class TransitionController {

    @FXML private Label titleLabel;
    @FXML private VBox bodyContainer;
    @FXML private Button continueButton;

    /** FXML resource path the continue button will navigate to (e.g. "/view/ScenarioScreen.fxml"). */
    private String targetFxml;

    @FXML
    public void initialize() {}

    /**
     * Configures the transition screen.
     *
     * @param title       Heading text.
     * @param bodyText    Body copy (blank lines become paragraph breaks).
     * @param buttonText  CTA button label.
     * @param targetFxml  FXML resource path to load when the button is pressed.
     */
    public void configure(String title, String bodyText, String buttonText, String targetFxml) {
        this.targetFxml = targetFxml;
        titleLabel.setText(title);
        continueButton.setText(buttonText);

        bodyContainer.getChildren().clear();

        if (bodyText == null || bodyText.isBlank()) return;

        String cleanedText = cleanText(bodyText);
        String[] paragraphs = cleanedText.split("\\n\\s*\\n");

        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (!trimmed.isEmpty()) {
                bodyContainer.getChildren().add(makeParagraph(trimmed));
            }
        }
    }

    /** Backwards-compatible overload that keeps the original hard-coded scenario destination. */
    public void configure(String title, String bodyText, String buttonText) {
        configure(title, bodyText, buttonText, "/view/ScenarioScreen.fxml");
    }

    @FXML
    private void handleContinueButton(ActionEvent event) throws Exception {
        String fxml = (targetFxml != null && !targetFxml.isBlank())
                ? targetFxml
                : "/view/ScenarioScreen.fxml";
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
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
