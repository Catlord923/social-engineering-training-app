package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ScenarioFeedbackController {

    @FXML
    private Label scenarioTitleLabel;

    @FXML
    private Label resultTitleLabel;

    @FXML
    private Label feedbackBodyLabel;

    @FXML
    private Button continueButton;

    private int scenarioOrder;

    public void configure(int scenarioOrder, String scenarioTitle, String resultTitle, String feedbackText, boolean success) {
        this.scenarioOrder = scenarioOrder;
        scenarioTitleLabel.setText(scenarioTitle);
        resultTitleLabel.setText(resultTitle);
        feedbackBodyLabel.setText(feedbackText);
    }

    @FXML
    private void handleContinue() {
        // next step: load next scenario or bonus/transition
    }
}