package com.example.model;

public class Scenario {
    private final int id;
    private final int scenarioOrder;
    private final String type;
    private final String title;
    private final String introText;
    private final String learningOutcomes;

    public Scenario(int id, int scenarioOrder, String type, String title, String introText, String learningOutcomes) {
        this.id = id;
        this.scenarioOrder = scenarioOrder;
        this.type = type;
        this.title = title;
        this.introText = introText;
        this.learningOutcomes = learningOutcomes;
    }

    public int getId() {
        return id;
    }

    public int getScenarioOrder() {
        return scenarioOrder;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getIntroText() {
        return introText;
    }

    public String getLearningOutcomes() {
        return learningOutcomes;
    }
}