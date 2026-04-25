package com.example.model;

/**
 * Immutable model representing a single stage within a training scenario.
 *
 * <p>Scenarios may contain multiple stages that guide the user through a
 * branching decision flow. A stage can either present selectable options
 * or act as a terminal outcome screen.</p>
 *
 * <p>Terminal stages may also store success metadata and feedback content
 * used for result summaries.</p>
 */
public class ScenarioStage {
    private final int id;
    private final int scenarioId;
    private final int stageOrder;
    private final String stageTitle;
    private final String prompt;
    private final boolean terminal;
    private final Boolean success;
    private final String feedbackTitle;
    private final String feedbackText;

    /**
     * Creates a scenario stage model using values loaded from the database.
     *
     * @param id stage id
     * @param scenarioId parent scenario identifier
     * @param stageOrder stored order within the scenario
     * @param stageTitle optional stage heading
     * @param prompt instructional or narrative prompt text
     * @param terminal {@code true} if this is a terminal stage
     * @param success terminal success state:
     *                {@code true} = successful outcome,
     *                {@code false} = unsuccessful outcome,
     *                {@code null} = not applicable
     * @param feedbackTitle optional terminal result title
     * @param feedbackText optional terminal result explanation
     */
    public ScenarioStage(
            int id,
            int scenarioId,
            int stageOrder,
            String stageTitle,
            String prompt,
            boolean terminal,
            Boolean success,
            String feedbackTitle,
            String feedbackText
    ) {
        this.id = id;
        this.scenarioId = scenarioId;
        this.stageOrder = stageOrder;
        this.stageTitle = stageTitle;
        this.prompt = prompt;
        this.terminal = terminal;
        this.success = success;
        this.feedbackTitle = feedbackTitle;
        this.feedbackText = feedbackText;
    }

    /**
     * Returns the stage id.
     *
     * @return stage id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the prompt text shown to the user.
     *
     * @return prompt text
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Returns whether this stage is terminal.
     *
     * @return {@code true} if terminal, otherwise {@code false}
     */
    public boolean isTerminal() {
        return terminal;
    }

    /**
     * Returns the success state for terminal stages.
     *
     * @return {@code true}, {@code false}, or {@code null}
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * Returns the terminal feedback title.
     *
     * @return feedback title, or {@code null}
     */
    public String getFeedbackTitle() {
        return feedbackTitle;
    }

    /**
     * Returns the terminal feedback text.
     *
     * @return feedback text, or {@code null}
     */
    public String getFeedbackText() {
        return feedbackText;
    }
}