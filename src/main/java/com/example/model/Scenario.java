package com.example.model;

/**
 * Immutable model representing a training scenario.
 *
 * <p>A scenario is the top-level unit in the practice module and contains
 * one or more stages that the user progresses through.</p>
 *
 * <p>This class stores database-backed metadata such as ordering, type,
 * title, and optional descriptive text.</p>
 */
public class Scenario {

    /**
     * Creates a scenario model from a database row.
     *
     * @param id database identifier
     * @param scenarioOrder display order within the scenario sequence
     * @param type scenario category stored in the database
     * @param title scenario title shown to the user
     * @param introText introductory text stored for the scenario
     * @param learningOutcomes learning outcomes stored for the scenario
     */
    public Scenario(int id, int scenarioOrder, String type, String title, String introText, String learningOutcomes) {
        this.id = id;
        this.scenarioOrder = scenarioOrder;
        this.type = type;
        this.title = title;
        this.introText = introText;
        this.learningOutcomes = learningOutcomes;
    }
    private final int id;
    private final int scenarioOrder;
    private final String type;
    private final String title;
    private final String introText;
    private final String learningOutcomes;

    /**
     * Returns the scenario id from the database.
     *
     * @return scenario id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the display order of the scenario.
     *
     * @return scenario sequence number
     */
    public int getScenarioOrder() {
        return scenarioOrder;
    }

    /**
     * Returns the title shown to the user.
     *
     * @return scenario title
     */
    public String getTitle() {
        return title;
    }
}