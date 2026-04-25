package com.example.controller;

import com.example.ui.ScenarioWidgetFactory;
import com.example.service.ScenarioService;
import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;
import com.example.model.ScenarioStage;
import javafx.scene.control.Button;
import com.example.model.Scenario;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the scenario screen.
 *
 * <p>Coordinates the scenario UI by connecting {@link ScenarioService}
 * with {@link ScenarioWidgetFactory}.</p>
 *
 * <p>The service manages scenario flow and state, while the factory builds
 * reusable UI widgets. This controller receives service callbacks and applies
 * the corresponding updates to the JavaFX view.</p>
 */
public class ScenarioController implements ScenarioService.NavigationCallback {

    @FXML private Label titleLabel;
    @FXML private Label promptLabel;
    @FXML private Label progressLabel;
    @FXML private VBox contentContainer;
    @FXML private VBox optionsContainer;

    private ScenarioService service;
    private ScenarioWidgetFactory factory;

    /**
     * Initializes the scenario screen after the FXML file is loaded.
     *
     * <p>Creates the widget factory, creates the scenario service, and starts
     * the first scenario.</p>
     */
    @FXML
    public void initialize() {
        // Pass controller handlers to the widget factory for user actions
        factory = new ScenarioWidgetFactory(
                this::handleOptionSelected,
                this::handleBonusSubmit,
                this::handleBonusSkip
        );
        service = new ScenarioService(this);
        service.start();
    }

    /**
     * Forwards a selected scenario option to the service layer.
     *
     * @param option option selected by the user
     */
    private void handleOptionSelected(ScenarioOption option) {
        service.handleOptionSelected(option);
    }

    /**
     * Forwards bonus form submission to the service layer.
     */
    private void handleBonusSubmit() {
        service.handleBonusSubmit();
    }

    /**
     * Forwards bonus form skipping to the service layer.
     */
    private void handleBonusSkip() {
        service.handleBonusSkip();
    }

    /**
     * Displays a non-terminal scenario stage.
     *
     * <p>The stage content is rendered into the main content area, while
     * selectable options are rendered into the options area.</p>
     */
    @Override
    public void onShowStage(Scenario scenario, ScenarioStage stage,
                            List<ScenarioElement> elements, List<ScenarioOption> options,
                            int totalScenarios) {
        titleLabel.setText(scenario.getTitle());
        promptLabel.setText(stage.getPrompt());
        progressLabel.setText("Scenario " + scenario.getScenarioOrder() + " of " + totalScenarios);

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        renderElements(elements);
        factory.renderOptionsInto(optionsContainer, options);
    }

    /**
     * Displays the result screen for a completed scenario.
     *
     * <p>The result card is styled differently depending on whether the user
     * reached a best, safe, or unsafe outcome.</p>
     */
    @Override
    public void onShowTerminal(Scenario scenario, ScenarioStage stage,
                               int scenarioOrder, int totalScenarios) {
        String resultIcon, accentColor, bgColor, tagText;
        Boolean success = stage.getSuccess();

        // Determine result styling based on the scenario outcome
        if (Boolean.TRUE.equals(success)) {
            boolean isBest = stage.getFeedbackTitle() != null
                    && stage.getFeedbackTitle().toLowerCase().contains("best");
            if (isBest) {
                resultIcon = "✓"; accentColor = "#22c55e"; bgColor = "#0f2a1e"; tagText = "Best Choice";
            } else {
                resultIcon = "✓"; accentColor = "#f59e0b"; bgColor = "#1a1200"; tagText = "Safe Choice";
            }
        } else {
            resultIcon = "✕"; accentColor = "#ef4444"; bgColor = "#2a0f0f"; tagText = "Unsafe Choice";
        }

        titleLabel.setText(scenario.getTitle());
        promptLabel.setText("Here is what happened:");
        progressLabel.setText("Scenario " + scenario.getScenarioOrder() + " of " + totalScenarios);

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        contentContainer.getChildren().add(
                factory.buildResultCard(accentColor, bgColor, resultIcon, tagText,
                        stage.getFeedbackTitle(), stage.getFeedbackText())
        );

        String btnText = scenarioOrder < totalScenarios ? "Next Scenario →" : "Complete Training →";
        Button continueButton = factory.buildActionButton(btnText, accentColor);
        continueButton.setOnAction(e -> service.handleContinueAfterTerminal());
        optionsContainer.getChildren().add(continueButton);
    }

    /**
     * Displays immediate feedback before moving to the next scenario stage.
     *
     * <p>Existing option buttons are disabled while the feedback message is
     * visible so the user cannot make another selection before continuing.</p>
     */
    @Override
    public void onShowImmediateFeedback(String feedbackText, int nextStageOrder) {
        optionsContainer.getChildren().forEach(node -> node.setDisable(true));

        VBox toast = factory.buildFeedbackToast(feedbackText);

        /*
        The Continue button is the last child of the toast.
        The button action is set here because the controller handles
        screen flow and progression logic.
        The factory only builds the UI
        */
        Button continueBtn = (Button) toast.getChildren().get(toast.getChildren().size() - 1);
        continueBtn.setOnAction(e -> {
            contentContainer.getChildren().remove(toast);
            service.handleFeedbackDismissed(nextStageOrder);
        });

        contentContainer.getChildren().add(toast);
    }

    /**
     * Displays the bonus phishing form scenario.
     */
    @Override
    public void onShowBonusScenario() {
        titleLabel.setText("View Your Results");
        promptLabel.setText("Complete the step below to access your scenario results.");
        progressLabel.setText("Bonus");

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        contentContainer.getChildren().add(factory.buildRegistrationFormWidget());
    }

    /**
     * Displays the result of the bonus phishing scenario.
     *
     * @param skipped {@code true} if the user skipped the form;
     *                {@code false} if the user submitted it
     */
    @Override
    public void onShowBonusTerminal(boolean skipped) {
        String accentColor, bgColor, resultIcon, tagText, feedbackTitle, feedbackBody;

        if (skipped) {
            accentColor   = "#22c55e";
            bgColor       = "#0f2a1e";
            resultIcon    = "✓";
            tagText       = "Best Choice";
            feedbackTitle = "You skipped the form";
            feedbackBody  =
                    "Good instinct. Legitimate training platforms do not require you to create an " +
                            "account mid-session just to see your own results.\n\n" +
                            "Prompts that appear unexpectedly and ask for personal details - especially a " +
                            "name, email, and password together - are a common data-harvesting technique. " +
                            "When in doubt, skip or verify through an official channel before entering anything.";
        } else {
            accentColor   = "#ef4444";
            bgColor       = "#2a0f0f";
            resultIcon    = "✕";
            tagText       = "Unsafe Choice";
            feedbackTitle = "You submitted the form";
            feedbackBody  =
                    "This was a trap. A real attacker could have collected your name, email address, " +
                            "and password from that form - details you might reuse elsewhere.\n\n" +
                            "Unexpected registration prompts inserted into a trusted workflow are a social " +
                            "engineering technique designed to feel routine. Always question why a form is " +
                            "appearing, what will be done with the data, and whether it was expected at this point.";
        }

        titleLabel.setText("View Your Results");
        promptLabel.setText("Here is what happened:");
        progressLabel.setText("Bonus");

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        contentContainer.getChildren().add(
                factory.buildResultCard(accentColor, bgColor, resultIcon, tagText, feedbackTitle, feedbackBody)
        );

        Button finishButton = factory.buildActionButton("Finish →", accentColor);
        finishButton.setOnAction(e -> service.handleBonusFinish());
        optionsContainer.getChildren().add(finishButton);
    }

    /**
     * Opens the transition screen that leads to the quiz module.
     */
    @Override
    public void onNavigateToQuiz() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/TransitionScreen.fxml"));
            Parent root = loader.load();

            TransitionController tc = loader.getController();
            tc.configure(
                    "Knowledge Quiz",
                    "Well done! You have completed all the practice scenarios.\n\n" +
                            "Now it is time to put your knowledge to the test.\n\n" +
                            "Answer each question carefully based on what you have learned " +
                            "about social engineering attacks.",
                    "Start Quiz",
                    "/view/QuizScreen.fxml"
            );

            Stage stage = (Stage) optionsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            onError("Could not load the quiz screen: " + ex.getMessage());
        }
    }

    /**
     * Displays an error state on the scenario screen.
     *
     * @param message error message to show to the user
     */
    @Override
    public void onError(String message) {
        titleLabel.setText("Error");
        promptLabel.setText(message);
        progressLabel.setText("");
        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();
    }

    /**
     * Renders scenario elements into the main content container.
     *
     * <p>Related elements are grouped before rendering so composite widgets,
     * such as emails, SMS messages, popups, and chat messages, can be built
     * from multiple database rows.</p>
     *
     * @param elements scenario elements to render
     */
    private void renderElements(List<ScenarioElement> elements) {
        // Store related rows so composite widgets can be built later
        List<ScenarioElement> emailParts  = new ArrayList<>();
        List<ScenarioElement> smsParts    = new ArrayList<>();
        List<ScenarioElement> popupParts  = new ArrayList<>();
        List<ScenarioElement> chatParts   = new ArrayList<>();

        for (ScenarioElement el : elements) {
            switch (el.getElementType()) {
                case "EMAIL_FROM", "EMAIL_SUBJECT", "EMAIL_BODY" -> emailParts.add(el);
                case "SMS_SENDER",  "SMS_MESSAGE"                -> smsParts.add(el);
                case "POPUP_TITLE", "POPUP_BODY"                 -> popupParts.add(el);
                case "CHAT_SENDER", "CHAT_MESSAGE"               -> chatParts.add(el);
                case "WARNING_BOX" -> contentContainer.getChildren().add(factory.buildWarningBox(el));
                case "INFO_BOX"    -> contentContainer.getChildren().add(factory.buildInfoBox(el));
                case "LINK"        -> contentContainer.getChildren().add(factory.buildLinkLabel(el));
                default            -> contentContainer.getChildren().add(factory.makeBodyLabel(el.getValue()));
            }
        }

        if (!emailParts.isEmpty())  contentContainer.getChildren().add(factory.buildEmailWidget(emailParts));
        if (!smsParts.isEmpty())    contentContainer.getChildren().add(factory.buildSmsWidget(smsParts));
        if (!popupParts.isEmpty())  contentContainer.getChildren().add(factory.buildPopupWidget(popupParts));
        if (!chatParts.isEmpty())   contentContainer.getChildren().add(factory.buildChatWidget(chatParts));
    }
}
