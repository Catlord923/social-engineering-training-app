package com.example.service;

import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;
import com.example.model.ScenarioStage;
import com.example.dao.ScenarioDAO;
import com.example.model.Scenario;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Owns all scenario navigation state and data-access logic.
 * The controller calls into this service; the service fires back through
 * {@link NavigationCallback} so it never touches JavaFX nodes directly.
 */
public class ScenarioService {

    /**
     * Callback interface through which the service drives the controller.
     * Each method corresponds to a distinct UI state the controller must render.
     */
    public interface NavigationCallback {
        /** Render a normal, interactive stage. */
        void onShowStage(Scenario scenario, ScenarioStage stage,
                         List<ScenarioElement> elements, List<ScenarioOption> options);

        /** Render the terminal (result) screen for a normal scenario stage. */
        void onShowTerminal(Scenario scenario, ScenarioStage stage, int scenarioOrder);

        /**
         * Show the inline feedback toast for an option that has immediate feedback,
         * then advance to {@code nextStageOrder} when the user dismisses it.
         */
        void onShowImmediateFeedback(String feedbackText, int nextStageOrder);

        /** Render the bonus scenario registration form. */
        void onShowBonusScenario();

        /** Render the bonus result screen. */
        void onShowBonusTerminal(boolean skipped);

        /** Navigate away to the quiz transition screen. */
        void onNavigateToQuiz();

        /** Display an error message. */
        void onError(String message);
    }

    private final ScenarioDAO       dao      = new ScenarioDAO();
    private final NavigationCallback callback;

    private Scenario      currentScenario;
    private ScenarioStage currentStage;
    private int           currentScenarioOrder = 1;
    private boolean       bonusActive          = false;

    public ScenarioService(NavigationCallback callback) {
        this.callback = callback;
    }

    // Public API
    /** Entry point: load the first scenario. */
    public void start() {
        loadScenario(currentScenarioOrder);
    }

    /** Called when the user selects a scenario option. */
    public void handleOptionSelected(ScenarioOption option) {
        if (option.getImmediateFeedback() != null && !option.getImmediateFeedback().isBlank()) {
            callback.onShowImmediateFeedback(option.getImmediateFeedback(), option.getNextStageOrder());
            return;
        }
        Integer next = option.getNextStageOrder();
        if (next == null) { callback.onError("This option is missing a next stage."); return; }
        loadStage(next);
    }

    /** Called when the user dismisses an immediate-feedback toast. */
    public void handleFeedbackDismissed(int nextStageOrder) {
        loadStage(nextStageOrder);
    }

    /** Called when the user clicks Continue/Next after a terminal stage. */
    public void handleContinueAfterTerminal() {
        if (currentScenarioOrder < 4) {
            currentScenarioOrder++;
            loadScenario(currentScenarioOrder);
        } else if (!bonusActive) {
            bonusActive = true;
            callback.onShowBonusScenario();
        } else {
            callback.onNavigateToQuiz();
        }
    }

    /** Called when the bonus form is submitted (the trap was taken). */
    public void handleBonusSubmit() {
        callback.onShowBonusTerminal(false);
    }

    /** Called when the user skips the bonus form (the correct choice). */
    public void handleBonusSkip() {
        callback.onShowBonusTerminal(true);
    }

    /** Called when the bonus terminal's Finish button is clicked. */
    public void handleBonusFinish() {
        callback.onNavigateToQuiz();
    }

    // Internal loading
    private void loadScenario(int scenarioOrder) {
        currentScenario = dao.getScenarioByOrder(scenarioOrder);
        if (currentScenario == null) { callback.onError("Scenario not found."); return; }
        loadStage(1);
    }

    private void loadStage(int stageOrder) {
        currentStage = dao.getStageByOrder(currentScenario.getId(), stageOrder);
        if (currentStage == null) { callback.onError("Stage not found."); return; }

        if (currentStage.isTerminal()) {
            callback.onShowTerminal(currentScenario, currentStage, currentScenarioOrder);
            return;
        }

        List<ScenarioElement> elements = dao.getElementsForStage(currentStage.getId());
        List<ScenarioOption>  options  = dao.getOptionsForStage(currentStage.getId());

        // Shuffle options for normal (non-bonus) scenarios
        List<ScenarioOption> orderedOptions = new ArrayList<>(options);
        if (!bonusActive) Collections.shuffle(orderedOptions);

        callback.onShowStage(currentScenario, currentStage, elements, orderedOptions);
    }
}
