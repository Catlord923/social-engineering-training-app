package com.example.controller;

import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.fxml.FXML;

public class WelcomeController {

    @FXML
    private Label welcomeBodyLabel;

    @FXML
    public void initialize() {

        welcomeBodyLabel.setText("""
            Welcome to this interactive cybersecurity training application.

            Many cyber attacks succeed not because of technical weaknesses,
            but because attackers manipulate people through trust, urgency,
            fear, or curiosity.

            This application will help you recognise common threats,
            understand warning signs, and make safer decisions online.

            You will complete three stages:

            1. Learn the key concepts
            2. Practice with realistic scenarios
            3. Test your knowledge

            Press Start to begin.
            """);
    }

    @FXML
    private void handleStartButton(ActionEvent event) throws Exception {

        Parent root = FXMLLoader.load(
                getClass().getResource("/view/TheoryScreen.fxml")
        );

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        stage.setScene(new Scene(root));
        stage.show();
    }
}