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

    @FXML
    public void initialize() {
        // Intentionally empty.
    }

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