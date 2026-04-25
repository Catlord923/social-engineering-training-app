package com.example.service;

import com.example.model.ScenarioElement;
import com.example.model.ScenarioOption;
import com.example.model.ScenarioStage;
import com.example.session.AppSession;
import com.example.dao.ScenarioDAO;
import com.example.model.Scenario;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for the scenario module.
 *
 * <p>This class manages scenario progression, stage transitions, option
 * handling, and communication with the controller through callbacks.</p>
 *
 * <p>The service contains no JavaFX UI code. Instead, screen updates are
 * delegated through {@link NavigationCallback}.</p>
 */
public class ScenarioService {

    /**
     * Callback methods implemented by the controller so the service can request
     * screen changes without depending on JavaFX components directly.
     */
    public interface NavigationCallback {
        void onShowStage(Scenario scenario, ScenarioStage stage,
                         List<ScenarioElement> elements, List<ScenarioOption> options,
                         int totalScenarios);
        void onShowTerminal(Scenario scenario, ScenarioStage stage,
                            int scenarioOrder, int totalScenarios);
        void onShowImmediateFeedback(String feedbackText, int nextStageOrder);
        void onShowBonusScenario();
        void onShowBonusTerminal(boolean skipped);
        void onNavigateToQuiz();
        void onError(String message);
    }

    private final ScenarioDAO dao = new ScenarioDAO();
    private final NavigationCallback callback;

    private Scenario currentScenario;
    private ScenarioStage currentStage;
    private int currentScenarioOrder = 1;
    private int totalScenarios = 0;
    // True once the bonus phishing scenario has been reached.
    private boolean bonusActive = false;

    /**
     * Creates the scenario service.
     *
     * @param callback object used to receive navigation and UI update requests
     */
    public ScenarioService(NavigationCallback callback) {
        this.callback = callback;
    }

    /**
     * Starts the scenario module by loading the first scenario.
     *
     * <p>The total scenario count is retrieved dynamically so progress indicators
     * do not rely on a hardcoded value.</p>
     */
    public void start() {
        totalScenarios = dao.getScenarioCount();
        if (totalScenarios == 0) { callback.onError("No scenarios found in the database."); return; }
        loadScenario(currentScenarioOrder);
    }

    /**
     * Handles selection of a scenario option.
     *
     * <p>If immediate feedback exists, it is shown before advancing.</p>
     *
     * @param option selected option
     */
    public void handleOptionSelected(ScenarioOption option) {
        if (option.getImmediateFeedback() != null && !option.getImmediateFeedback().isBlank()) {
            callback.onShowImmediateFeedback(option.getImmediateFeedback(), option.getNextStageOrder());
            return;
        }
        Integer next = option.getNextStageOrder();
        if (next == null) { callback.onError("This option is missing a next stage."); return; }
        loadStage(next);
    }

    /**
     * Continues to the next stage after feedback has been dismissed.
     *
     * @param nextStageOrder next stage number
     */
    public void handleFeedbackDismissed(int nextStageOrder) {
        loadStage(nextStageOrder);
    }

    /**
     * Continues after a terminal scenario outcome.
     *
     * <p>The completed result is first recorded in {@link AppSession},
     * then progression moves to the next scenario, bonus scenario,
     * or quiz module.</p>
     */
    public void handleContinueAfterTerminal() {
        // Record only normal scenario outcomes; bonus outcome is handled separately
        if (!bonusActive && currentStage != null && currentStage.isTerminal()) {
            Boolean success = currentStage.getSuccess();
            boolean isSuccess = Boolean.TRUE.equals(success);

            // Best outcomes are identified by feedback title text
            boolean isBest = isSuccess
                    && currentStage.getFeedbackTitle() != null
                    && currentStage.getFeedbackTitle().toLowerCase().contains("best");
            AppSession.getInstance().recordScenarioOutcome(isSuccess, isBest);
        }

        if (currentScenarioOrder < totalScenarios) {
            currentScenarioOrder++;
            loadScenario(currentScenarioOrder);
        } else if (!bonusActive) {
            bonusActive = true;
            callback.onShowBonusScenario();
        } else {
            callback.onNavigateToQuiz();
        }
    }

    /**
     * Records that the user submitted the bonus phishing form.
     */
    public void handleBonusSubmit() {
        AppSession.getInstance().recordBonusOutcome(false);
        callback.onShowBonusTerminal(false);
    }

    /**
     * Records that the user correctly skipped the bonus phishing form.
     */
    public void handleBonusSkip() {
        AppSession.getInstance().recordBonusOutcome(true);
        callback.onShowBonusTerminal(true);
    }

    /**
     * Navigates to the quiz after the bonus scenario is complete.
     */
    public void handleBonusFinish() {
        callback.onNavigateToQuiz();
    }

    /**
     * Loads a scenario and begins at stage one.
     *
     * @param scenarioOrder scenario number
     */
    private void loadScenario(int scenarioOrder) {
        currentScenario = dao.getScenarioByOrder(scenarioOrder);
        if (currentScenario == null) { callback.onError("Scenario not found."); return; }
        loadStage(1);
    }

    /**
     * Loads a stage for the current scenario.
     *
     * <p>Terminal stages display a result screen. Non-terminal stages load
     * their content elements and selectable options.</p>
     *
     * @param stageOrder stage number
     */
    private void loadStage(int stageOrder) {
        currentStage = dao.getStageByOrder(currentScenario.getId(), stageOrder);
        if (currentStage == null) { callback.onError("Stage not found."); return; }

        if (currentStage.isTerminal()) {
            callback.onShowTerminal(currentScenario, currentStage, currentScenarioOrder, totalScenarios);
            return;
        }

        List<ScenarioElement> elements = dao.getElementsForStage(currentStage.getId());
        List<ScenarioOption>  options  = dao.getOptionsForStage(currentStage.getId());

        List<ScenarioOption> orderedOptions = new ArrayList<>(options);

        // Randomize normal scenarios' options to reduce memorization
        if (!bonusActive) Collections.shuffle(orderedOptions);

        callback.onShowStage(currentScenario, currentStage, elements, orderedOptions, totalScenarios);
    }
}
