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
 * Thin coordinator for the scenario screen.
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Owns the {@code @FXML} node references.</li>
 *   <li>Wires {@link ScenarioService} and {@link ScenarioWidgetFactory} together.</li>
 *   <li>Implements {@link ScenarioService.NavigationCallback} to translate service
 *       events into concrete UI updates.</li>
 * </ul>
 *
 * <p>It contains no DAO calls, no widget-building code, and no navigation state.
 */
public class ScenarioController implements ScenarioService.NavigationCallback {

    @FXML private Label titleLabel;
    @FXML private Label promptLabel;
    @FXML private Label progressLabel;
    @FXML private VBox  contentContainer;
    @FXML private VBox  optionsContainer;

    private ScenarioService service;
    private ScenarioWidgetFactory factory;

    @FXML
    public void initialize() {
        factory = new ScenarioWidgetFactory(
                this::handleOptionSelected,
                this::handleBonusSubmit,
                this::handleBonusSkip
        );
        service = new ScenarioService(this);
        service.start();
    }

    // User action forwarders
    // These are the lambdas / method references passed into the factory and service.
    // They exist here (rather than inline) so the controller remains the single point
    // that decides what to forward where.

    private void handleOptionSelected(ScenarioOption option) {
        service.handleOptionSelected(option);
    }

    private void handleBonusSubmit() {
        service.handleBonusSubmit();
    }

    private void handleBonusSkip() {
        service.handleBonusSkip();
    }

    // NavigationCallback implementation
    @Override
    public void onShowStage(Scenario scenario, ScenarioStage stage,
                            List<ScenarioElement> elements, List<ScenarioOption> options) {
        titleLabel.setText(scenario.getTitle());
        promptLabel.setText(stage.getPrompt());
        progressLabel.setText("Scenario " + scenario.getScenarioOrder() + " of 4");

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        renderElements(elements);
        factory.renderOptionsInto(optionsContainer, options);
    }

    @Override
    public void onShowTerminal(Scenario scenario, ScenarioStage stage, int scenarioOrder) {
        // Resolve tier colours
        String resultIcon, accentColor, bgColor, tagText;
        Boolean success = stage.getSuccess();

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
        progressLabel.setText("Scenario " + scenario.getScenarioOrder() + " of 4");

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        contentContainer.getChildren().add(
                factory.buildResultCard(accentColor, bgColor, resultIcon, tagText,
                        stage.getFeedbackTitle(), stage.getFeedbackText())
        );

        String btnText = scenarioOrder < 4 ? "Next Scenario →" : "Complete Training →";
        Button continueButton = factory.buildActionButton(btnText, accentColor);
        continueButton.setOnAction(e -> service.handleContinueAfterTerminal());
        optionsContainer.getChildren().add(continueButton);
    }

    @Override
    public void onShowImmediateFeedback(String feedbackText, int nextStageOrder) {
        optionsContainer.getChildren().forEach(node -> node.setDisable(true));

        VBox toast = factory.buildFeedbackToast(feedbackText);

        // The Continue button is the last child of the toast - its action is
        // wired here so the controller (not the factory) owns the navigation decision.
        Button continueBtn = (Button) toast.getChildren().get(toast.getChildren().size() - 1);
        continueBtn.setOnAction(e -> {
            contentContainer.getChildren().remove(toast);
            service.handleFeedbackDismissed(nextStageOrder);
        });

        contentContainer.getChildren().add(toast);
    }

    @Override
    public void onShowBonusScenario() {
        titleLabel.setText("View Your Results");
        promptLabel.setText("Complete the step below to access your scenario results.");
        progressLabel.setText("Bonus");

        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();

        contentContainer.getChildren().add(factory.buildRegistrationFormWidget());
    }

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

    @Override
    public void onError(String message) {
        titleLabel.setText("Error");
        promptLabel.setText(message);
        progressLabel.setText("");
        contentContainer.getChildren().clear();
        optionsContainer.getChildren().clear();
    }

    // Element rendering
    // Kept here (rather than in the factory) because it mutates contentContainer
    // directly and routes between multiple factory methods based on element type.
    private void renderElements(List<ScenarioElement> elements) {
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
