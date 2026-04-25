package com.example.controller;

import com.example.model.QuizOption;
import com.example.model.QuizQuestion;
import com.example.service.QuizService;

import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;

import java.util.List;

/**
 * Controller for the quiz screen.
 *
 * <p>Handles UI updates and delegates quiz logic to
 * {@link QuizService}.</p>
 *
 * <p>Implements {@link QuizService.QuizCallback} so the service layer can
 * request screen updates without depending on JavaFX classes directly.</p>
 *
 * <p>This controller contains no database access or scoring logic.</p>
 */
public class QuizController implements QuizService.QuizCallback {

    @FXML private Label titleLabel;
    @FXML private Label progressLabel;
    @FXML private Label questionLabel;
    @FXML private VBox  optionsContainer;
    @FXML private VBox  feedbackContainer;
    @FXML private Button nextButton;

    private QuizService service;

    /**
     * Options currently displayed for the active question.
     *
     * <p>Stored so answer results can highlight the selected and correct
     * buttons after the user submits an answer.</p>
     */
    private List<QuizOption> currentOptions;

    /**
     * Initializes the quiz screen after the FXML file is loaded.
     *
     * <p>Creates the quiz service and starts the first question.</p>
     */
    @FXML
    public void initialize() {
        service = new QuizService(this);
        service.start();
    }

    /**
     * Displays a quiz question and its answer options.
     *
     * @param question current question
     * @param options selectable answer options
     * @param questionNum current question number
     * @param totalQuestions total number of quiz questions
     */
    @Override
    public void onShowQuestion(QuizQuestion question, List<QuizOption> options,
                               int questionNum, int totalQuestions) {
        // Keep a reference so buttons can be styled after answering
        this.currentOptions = options;

        titleLabel.setText("Knowledge Quiz");
        progressLabel.setText("Question " + questionNum + " of " + totalQuestions);
        questionLabel.setText(question.getQuestionText());

        optionsContainer.getChildren().clear();
        feedbackContainer.getChildren().clear();

        nextButton.setDisable(true);

        // Change text on the final question
        nextButton.setText(questionNum == totalQuestions ? "Finish →" : "Next →");

        for (int i = 0; i < options.size(); i++) {
            final int index = i;
            Button button = buildOptionButton(options.get(i).getOptionText());
            button.setOnAction(e -> service.handleAnswerSelected(index));
            optionsContainer.getChildren().add(button);
        }
    }

    /**
     * Displays the result of a selected answer.
     *
     * <p>The selected option is highlighted, the correct answer is revealed
     * when needed, further selections are disabled, and feedback is shown.</p>
     *
     * @param selectedIndex selected option index
     * @param correctIndex correct option index
     * @param wasCorrect whether the selected answer was correct
     * @param explanation explanation text
     */
    @Override
    public void onAnswerResult(int selectedIndex, int correctIndex,
                               boolean wasCorrect, String explanation) {
        // Highlight selected and correct buttons
        applyStyle(selectedIndex, wasCorrect ? correctOptionStyle() : wrongOptionStyle());
        if (!wasCorrect && correctIndex >= 0) {
            applyStyle(correctIndex, correctOptionStyle());
        }

        // Disable all options to prevent re-answering
        optionsContainer.getChildren().forEach(node -> node.setDisable(true));

        showFeedback(wasCorrect, explanation);
        nextButton.setDisable(false);
    }

    /**
     * Displays the final quiz score and completion message.
     *
     * @param score correct answers achieved
     * @param total total quiz questions
     */
    @Override
    public void onShowFinalResults(int score, int total) {
        titleLabel.setText("Quiz Complete");
        progressLabel.setText("Final Score");
        questionLabel.setText("You scored " + score + " out of " + total + ".");

        optionsContainer.getChildren().clear();
        feedbackContainer.getChildren().clear();

        Label resultMessage = new Label(buildResultMessage(score, total));
        resultMessage.setWrapText(true);
        resultMessage.setMaxWidth(Double.MAX_VALUE);
        resultMessage.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-text-fill: #cbd5e1;" +
                        "-fx-line-spacing: 6px;"
        );
        feedbackContainer.getChildren().add(resultMessage);

        nextButton.setText("View Results →");
        nextButton.setDisable(false);

        // Replace normal progression with final navigation
        nextButton.setOnAction(e -> service.handleFinish());
    }

    /**
     * Opens the transition screen leading to the results' module.
     */
    @Override
    public void onNavigateToResults() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TransitionScreen.fxml"));
            Parent root = loader.load();

            TransitionController tc = loader.getController();
            tc.configure(
                    "Training Complete",
                    "Congratulations! You have finished all three modules.\n\n" +
                            "Your results are ready. See how you performed across the scenarios and quiz.",
                    "See My Results",
                    "/view/ResultsScreen.fxml"
            );

            Stage stage = (Stage) nextButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            onError("Could not load the results screen: " + ex.getMessage());
        }
    }

    /**
     * Displays an error state on the quiz screen.
     *
     * @param message error message to show
     */
    @Override
    public void onError(String message) {
        titleLabel.setText("Error");
        questionLabel.setText(message);
        progressLabel.setText("");
        optionsContainer.getChildren().clear();
        feedbackContainer.getChildren().clear();
        nextButton.setDisable(true);
    }

    /**
     * Handles presses of the Next button.
     *
     * <p>Delegates progression logic to the service layer.</p>
     */
    @FXML
    private void handleNextButton() {
        service.handleNext();
    }

    /**
     * Applies a style string to the option button at the given index.
     *
     * @param index option index
     * @param style CSS style string
     */
    private void applyStyle(int index, String style) {
        if (index >= 0 && index < optionsContainer.getChildren().size()) {
            optionsContainer.getChildren().get(index).setStyle(style);
        }
    }

    /**
     * Displays feedback after an answer is selected.
     *
     * @param correct whether the selected answer was correct
     * @param explanation optional explanation text
     */
    private void showFeedback(boolean correct, String explanation) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.TOP_LEFT);
        card.setStyle(
                "-fx-background-color: " + (correct ? "#0f2a1e" : "#2a0f0f") + ";" +
                        "-fx-border-color: " + (correct ? "#22c55e" : "#ef4444") + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 16 18 16 18;"
        );

        Label heading = new Label(correct ? "Correct" : "Incorrect");
        heading.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + (correct ? "#4ade80" : "#f87171") + ";"
        );

        String bodyText = (explanation != null && !explanation.isBlank())
                ? explanation
                : (correct ? "That was the safest choice." : "That was not the safest choice.");
        Label body = new Label(bodyText);
        body.setWrapText(true);
        body.setMaxWidth(Double.MAX_VALUE);
        body.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #cbd5e1;" +
                        "-fx-line-spacing: 4px;"
        );

        card.getChildren().addAll(heading, body);
        feedbackContainer.getChildren().add(card);
    }

    /**
     * Builds a styled answer option button.
     *
     * @param text button text
     * @return styled option button
     */
    private Button buildOptionButton(String text) {
        Button button = new Button(text);
        button.setWrapText(true);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(54);
        VBox.setVgrow(button, Priority.NEVER);
        button.setStyle(defaultOptionStyle());
        return button;
    }

    /**
     * Returns a score summary message based on performance.
     *
     * @param score correct answers achieved
     * @param total total questions
     * @return result summary message
     */
    private String buildResultMessage(int score, int total) {
        if (score == total) {
            return "Excellent result. You showed strong awareness of common social engineering tactics and safe response behaviours.";
        } else if (score >= (int) Math.ceil(total * 0.8)) {
            return "Very good result. You recognised most warning signs and generally chose safe responses.";
        } else if (score >= (int) Math.ceil(total * 0.6)) {
            return "Good effort. You understood many of the key ideas, but some warning signs and response choices may need more attention.";
        } else {
            return "You may benefit from reviewing the theory and scenarios again. Social engineering attacks often rely on urgency, trust, and distraction.";
        }
    }

    /**
     * Default style for unselected option buttons.
     *
     * @return CSS style string
     */
    private String defaultOptionStyle() {
        return "-fx-background-color: #1e293b;" +
                "-fx-text-fill: #e2e8f0;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #334155;" +
                "-fx-border-radius: 8;" +
                "-fx-cursor: hand;" +
                "-fx-alignment: CENTER-LEFT;" +
                "-fx-padding: 12 20 12 20;";
    }

    /**
     * Style for correctly answered option buttons.
     *
     * @return CSS style string
     */
    private String correctOptionStyle() {
        return "-fx-background-color: #14532d;" +
                "-fx-text-fill: #dcfce7;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #22c55e;" +
                "-fx-border-radius: 8;" +
                "-fx-alignment: CENTER-LEFT;" +
                "-fx-padding: 12 20 12 20;";
    }

    /**
     * Style for incorrectly selected option buttons.
     *
     * @return CSS style string
     */
    private String wrongOptionStyle() {
        return "-fx-background-color: #7f1d1d;" +
                "-fx-text-fill: #fee2e2;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: #ef4444;" +
                "-fx-border-radius: 8;" +
                "-fx-alignment: CENTER-LEFT;" +
                "-fx-padding: 12 20 12 20;";
    }
}
