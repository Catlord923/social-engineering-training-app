package com.example.model;

/**
 * Immutable model representing a selectable option within a scenario stage.
 *
 * <p>Scenario options are presented to the user as decision choices.
 * Selecting an option may advance to another stage and may optionally
 * display immediate feedback first.</p>
 */
public class ScenarioOption {
    private final int id;
    private final int stageId;
    private final int optionOrder;
    private final String optionText;
    private final Integer nextStageOrder;
    private final String immediateFeedback;

    /**
     * Creates a scenario option model using values loaded from the database.
     *
     * @param id database identifier
     * @param stageId parent stage identifier
     * @param optionOrder display order within the stage
     * @param optionText text shown on the option button
     * @param nextStageOrder next stage number, or {@code null} if none
     * @param immediateFeedback optional feedback shown before progressing
     */
    public ScenarioOption(int id, int stageId, int optionOrder, String optionText, Integer nextStageOrder, String immediateFeedback) {
        this.id = id;
        this.stageId = stageId;
        this.optionOrder = optionOrder;
        this.optionText = optionText;
        this.nextStageOrder = nextStageOrder;
        this.immediateFeedback = immediateFeedback;
    }

    /**
     * Returns the database identifier.
     *
     * @return option id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the text shown to the user.
     *
     * @return option label text
     */
    public String getOptionText() {
        return optionText;
    }

    /**
     * Returns the next stage number.
     *
     * @return next stage order, or {@code null}
     */
    public Integer getNextStageOrder() {
        return nextStageOrder;
    }

    /**
     * Returns optional immediate feedback text.
     *
     * @return feedback text or {@code null}
     */
    public String getImmediateFeedback() {
        return immediateFeedback;
    }
}