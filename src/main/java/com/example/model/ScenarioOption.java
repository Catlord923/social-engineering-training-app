package com.example.model;

public class ScenarioOption {
    private final int id;
    private final int stageId;
    private final int optionOrder;
    private final String optionText;
    private final Integer nextStageOrder;
    private final String immediateFeedback;

    public ScenarioOption(int id, int stageId, int optionOrder, String optionText, Integer nextStageOrder, String immediateFeedback) {
        this.id = id;
        this.stageId = stageId;
        this.optionOrder = optionOrder;
        this.optionText = optionText;
        this.nextStageOrder = nextStageOrder;
        this.immediateFeedback = immediateFeedback;
    }

    public int getId() {
        return id;
    }

    public int getStageId() {
        return stageId;
    }

    public int getOptionOrder() {
        return optionOrder;
    }

    public String getOptionText() {
        return optionText;
    }

    public Integer getNextStageOrder() {
        return nextStageOrder;
    }

    public String getImmediateFeedback() {
        return immediateFeedback;
    }
}