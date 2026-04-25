package com.example.controller;

import com.example.session.AppSession;
import javafx.scene.layout.Priority;
import javafx.application.Platform;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;

/**
 * Controller for the results screen.
 *
 * <p>Reads accumulated performance data from {@link AppSession} and renders
 * a final summary covering scenario outcomes, the bonus phishing task,
 * quiz performance, and an overall feedback message.</p>
 *
 * <p>This screen is read-only and performs no scoring or persistence logic.</p>
 */
public class ResultsController {

    @FXML private VBox scenarioBreakdownContainer;
    @FXML private VBox bonusBreakdownContainer;
    @FXML private VBox quizBreakdownContainer;
    @FXML private VBox overallMessageContainer;

    /**
     * Initializes the results screen after the FXML file is loaded.
     *
     * <p>Reads session data and renders all result sections.</p>
     */
    @FXML
    public void initialize() {
        AppSession session = AppSession.getInstance();
        renderScenarioBreakdown(session);
        renderBonusBreakdown(session);
        renderQuizBreakdown(session);
        renderOverallMessage(session);
    }

    /**
     * Closes the application when the Exit button is pressed.
     */
    @FXML
    private void handleExit() {
        Platform.exit();
    }

    /**
     * Renders the scenario performance breakdown.
     *
     * @param s active application session
     */
    private void renderScenarioBreakdown(AppSession s) {
        scenarioBreakdownContainer.getChildren().clear();

        addStatRow(scenarioBreakdownContainer,
                "Best choices",   String.valueOf(s.getBestChoiceCount()),  "#22c55e");
        addStatRow(scenarioBreakdownContainer,
                "Safe choices",   String.valueOf(s.getSafeChoiceCount()),  "#f59e0b");
        addStatRow(scenarioBreakdownContainer,
                "Unsafe choices", String.valueOf(s.getUnsafeChoiceCount()), "#ef4444");
    }

    /**
     * Renders the bonus phishing scenario result.
     *
     * @param s active application session
     */
    private void renderBonusBreakdown(AppSession s) {
        bonusBreakdownContainer.getChildren().clear();

        Boolean skipped = s.getBonusSkipped();
        if (skipped == null) {
            bonusBreakdownContainer.getChildren().add(
                    makeDimLabel("Bonus scenario not completed.")
            );
            return;
        }

        String label  = skipped ? "Skipped the form (correct)"      : "Submitted the form (fell for it)";
        String colour = skipped ? "#22c55e"                          : "#ef4444";
        addStatRow(bonusBreakdownContainer, label, skipped ? "✓" : "✕", colour);
    }

    /**
     * Renders the quiz score summary.
     *
     * @param s active application session
     */
    private void renderQuizBreakdown(AppSession s) {
        quizBreakdownContainer.getChildren().clear();

        if (!s.isQuizDone()) {
            quizBreakdownContainer.getChildren().add(makeDimLabel("Quiz not completed."));
            return;
        }

        int score = s.getQuizScore();
        int total = s.getQuizTotal();
        double pct = total > 0 ? (score * 100.0 / total) : 0;
        String colour = pct >= 80 ? "#22c55e" : pct >= 60 ? "#f59e0b" : "#ef4444";

        addStatRow(quizBreakdownContainer,
                "Score",
                score + " / " + total + "  (" + Math.round(pct) + "%)",
                colour);
    }

    /**
     * Renders the final overall feedback message.
     *
     * @param s active application session
     */
    private void renderOverallMessage(AppSession s) {
        overallMessageContainer.getChildren().clear();

        String message = buildOverallMessage(s);
        Label label = new Label(message);
        label.setWrapText(true);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setStyle(
                "-fx-font-size: 19px;" +
                        "-fx-text-fill: #94a3b8;" +
                        "-fx-line-spacing: 7px;"
        );
        overallMessageContainer.getChildren().add(label);
    }

    /**
     * Builds an overall summary message based on total performance.
     *
     * @param s active application session
     * @return overall feedback message
     */
    private String buildOverallMessage(AppSession s) {
        int best = s.getBestChoiceCount();
        int unsafe = s.getUnsafeChoiceCount();
        Boolean bon = s.getBonusSkipped();
        int qScore = s.getQuizScore();
        int qTotal = s.getQuizTotal();
        double qPct = qTotal > 0 ? (qScore * 100.0 / qTotal) : 0;

        boolean allBest = best == s.getScenariosCompleted();
        boolean bonusGood = Boolean.TRUE.equals(bon);
        boolean quizStrong = qPct >= 80;

        if (allBest && bonusGood && quizStrong) {
            return "Outstanding performance. You consistently chose the best response, recognised the phishing trap, and demonstrated strong quiz knowledge. You are well-equipped to handle social engineering threats.";
        } else if (unsafe == 0 && bonusGood && quizStrong) {
            return "Strong result. You stayed safe throughout the scenarios, correctly identified the phishing form, and scored well on the quiz. A few scenarios had even safer responses available - worth reviewing.";
        } else if (unsafe == 0 && qPct >= 60) {
            return "You avoided unsafe choices in the scenarios and performed reasonably on the quiz. Review any scenarios where you did not choose the best option, and consider retaking the quiz to improve your score.";
        } else {
            return "There are some areas to work on. Review the theory section and replay the scenarios, paying close attention to warning signs such as urgency, unexpected requests, and unusual sender details.";
        }
    }

    /**
     * Adds a horizontal statistic row to the supplied container.
     *
     * <p>The label is left-aligned and the value is right-aligned using a
     * spacer region.</p>
     *
     * @param container destination container
     * @param labelText left label text
     * @param valueText right value text
     * @param accentColour text colour for the value
     */
    private void addStatRow(VBox container, String labelText, String valueText, String accentColour) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle(
                "-fx-background-color: #1e293b;" +
                        "-fx-background-radius: 8;"
        );

        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 16px; -fx-text-fill: #cbd5e1;");
        HBox.setHgrow(lbl, Priority.ALWAYS);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label val = new Label(valueText);
        val.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + accentColour + ";"
        );

        row.getChildren().addAll(lbl, spacer, val);
        container.getChildren().add(row);
    }

    /**
     * Builds a muted informational label.
     *
     * @param text label text
     * @return styled label
     */
    private Label makeDimLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #475569;");
        return lbl;
    }
}
