package com.example.model;

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

    public int getId() {
        return id;
    }

    public int getScenarioId() {
        return scenarioId;
    }

    public int getStageOrder() {
        return stageOrder;
    }

    public String getStageTitle() {
        return stageTitle;
    }

    public String getPrompt() {
        return prompt;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getFeedbackTitle() {
        return feedbackTitle;
    }

    public String getFeedbackText() {
        return feedbackText;
    }
}